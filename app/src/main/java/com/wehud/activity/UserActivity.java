package com.wehud.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.wehud.R;
import com.wehud.adapter.PagesAdapter;
import com.wehud.adapter.PageAdapter;
import com.wehud.dialog.ListDialogFragment;
import com.wehud.dialog.TextDialogFragment;
import com.wehud.fragment.UserPlanningsFragment;
import com.wehud.fragment.UserPostsFragment;
import com.wehud.model.Follower;
import com.wehud.model.Page;
import com.wehud.model.Payload;
import com.wehud.model.Reward;
import com.wehud.model.Score;
import com.wehud.model.User;
import com.wehud.network.APICall;
import com.wehud.util.Constants;
import com.wehud.util.GsonUtils;
import com.wehud.util.PreferencesUtils;
import com.wehud.util.Utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserActivity extends AppCompatActivity
        implements View.OnClickListener, ViewPager.OnPageChangeListener,
        ListDialogFragment.OnListDialogDismissOkListener,
        TextDialogFragment.OnTextDialogDismissOkListener {

    private static final String KEY_USER_ID = "key_user_id";
    private static final String KEY_CURRENT_PAGE = "key_current_page";
    private static final String PARAM_PAGE = "page";

    private ImageView mAvatar;
    private TextView mUsername;
    private TextView mScore;
    private TextView mFollowers;
    private Button mFollowOrEventsButton;

    private User mCurrentUser;
    private String mUserId;
    private String mConnectedUserId;
    private boolean mIsConnectedUser;

    private int mCurrentPage;

    private boolean mPaused;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!mPaused) {
                String response = intent.getStringExtra(Constants.EXTRA_BROADCAST);
                Payload payload = GsonUtils.getInstance().fromJson(response, Payload.class);

                String code = payload.getCode();

                if (Integer.valueOf(code) == Constants.HTTP_OK) {
                    String content = payload.getContent();

                    switch (intent.getAction()) {
                        case Constants.INTENT_USER_GET:
                            mCurrentUser = GsonUtils.getInstance().fromJson(content, User.class);
                            final String avatar = mCurrentUser.getAvatar();
                            final String username = mCurrentUser.getUsername();
                            final Score score = Utils.getScore(mCurrentUser.getScore());
                            final String levelRank =
                                    getResources().getString(R.string.level, score.getLevel()) + "\n" +
                                    getResources().getString(R.string.rank, score.getRank());
                            final List<User> followers = mCurrentUser.getFollowers();
                            final String numFollowers = followers.size() + "\t"
                                    + getString(R.string.followerCount);

                            mConnectedUserId = PreferencesUtils.get(UserActivity.this, Constants.PREF_USER_ID);

                            if (mIsConnectedUser)
                                mFollowOrEventsButton.setText(getString(R.string.btnEvents));
                            else {
                                boolean found = false;
                                for (User user : followers) {
                                    if (user.getId().equals(mConnectedUserId)) {
                                        found = true;
                                        break;
                                    }
                                }

                                if (found) mFollowOrEventsButton.setText(
                                        getString(R.string.btnUnfollow)
                                );
                                else mFollowOrEventsButton.setText(getString(R.string.btnFollow));
                            }

                            if (!TextUtils.isEmpty(avatar))
                                Utils.loadImage(UserActivity.this, avatar, mAvatar, 256);
                            else mAvatar.setImageResource(R.mipmap.ic_launcher_round);

                            mUsername.setText(username);
                            mScore.setText(levelRank);
                            mFollowers.setText(numFollowers);
                            break;
                        case Constants.INTENT_USER_FOLLOW:
                            mFollowOrEventsButton.setText(getString(R.string.btnUnfollow));
                            final Follower newFollower = GsonUtils.getInstance().fromJson(
                                    content, Follower.class
                            );

                            mCurrentUser.follow(newFollower.getUser());
                            final String countAfterFollow = mCurrentUser.getFollowers().size()
                                    + "\t" + getString(R.string.followerCount
                            );

                            mFollowers.setText(countAfterFollow);

                            final Reward reward = Utils.getNestedReward(content);
                            if (Utils.isNotEmpty(reward.getEntities()))
                                Utils.generateRewardDialog(
                                        UserActivity.this,
                                        getSupportFragmentManager(),
                                        UserActivity.this,
                                        reward,
                                        0
                                );
                            else Utils.toast(
                                    UserActivity.this,
                                    R.string.message_followingUser,
                                    mCurrentUser.getUsername()
                            );
                            break;
                        case Constants.INTENT_USER_UNFOLLOW:
                            mFollowOrEventsButton.setText(getString(R.string.btnFollow));
                            final Follower oldFollowed = GsonUtils.getInstance().fromJson(
                                    content,
                                    Follower.class
                            );

                            mCurrentUser.unfollow(oldFollowed.getUser());
                            final String countAfterUnfollow = mCurrentUser.getFollowers().size()
                                    + "\t" + getString(R.string.followerCount
                            );

                            mFollowers.setText(countAfterUnfollow);

                            Utils.toast(
                                    UserActivity.this,
                                    R.string.message_unfollowingUser,
                                    mCurrentUser.getUsername()
                            );
                            break;
                        case Constants.INTENT_PAGES_LIST:
                            final Type pageListType = new TypeToken<List<Page>>(){}.getType();
                            final List<Page> pages = GsonUtils.getInstance().fromJson(
                                    content, pageListType
                            );

                            UserActivity.this.follow(pages);
                            break;
                        default:
                            break;
                    }
                } else {
                    int messageId;
                    switch (Integer.valueOf(code)) {
                        case Constants.HTTP_UNAUTHORIZED:
                            messageId = R.string.error_sessionExpired;
                            finish();
                            break;
                        case Constants.HTTP_INTERNAL_SERVER_ERROR:
                            messageId = R.string.error_server;
                            break;
                        default:
                            Utils.toast(UserActivity.this, R.string.error_general, code);
                            return;
                    }

                    Utils.toast(UserActivity.this, messageId);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        if (savedInstanceState != null) {
            mCurrentPage = savedInstanceState.getInt(KEY_CURRENT_PAGE);
            mUserId = savedInstanceState.getString(KEY_USER_ID);
        } else {
            mCurrentPage = 0;

            Intent intent = getIntent();
            if (intent != null) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    mUserId = bundle.getString(KEY_USER_ID);
                    mIsConnectedUser = Utils.isConnectedUser(this, mUserId);
                }
            }
        }

        mAvatar = (ImageView) findViewById(R.id.avatar);
        mUsername = (TextView) findViewById(R.id.username);
        mScore = (TextView) findViewById(R.id.score);
        mFollowers = (TextView) findViewById(R.id.followers);

        mFollowOrEventsButton = (Button) findViewById(R.id.btnFollow);
        mFollowOrEventsButton.setOnClickListener(this);

        TabLayout tabs = (TabLayout) findViewById(android.R.id.tabs);
        ViewPager pager = (ViewPager) findViewById(R.id.pager);

        PageAdapter adapter = new PageAdapter(getSupportFragmentManager());
        adapter.add(UserPostsFragment.newInstance(mUserId), getString(R.string.tab_posts), false);
        adapter.add(
                UserPlanningsFragment.newInstance(mUserId),
                getString(R.string.tab_plannings),
                false
        );

        pager.setAdapter(adapter);
        pager.setCurrentItem(mCurrentPage);
        pager.addOnPageChangeListener(this);
        tabs.setupWithViewPager(pager);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.INTENT_USER_GET);
        filter.addAction(Constants.INTENT_PAGES_LIST);
        filter.addAction(Constants.INTENT_USER_FOLLOW);
        filter.addAction(Constants.INTENT_USER_UNFOLLOW);

        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(mUserId) && !mPaused) this.getUser();

        mPaused = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPaused = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_PAGE, mCurrentPage);
        outState.putString(KEY_USER_ID, mUserId);
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.btnFollow:
                if (mFollowOrEventsButton.getText().toString()
                        .equals(getString(R.string.btnFollow)))
                    this.getPages();
                if (mFollowOrEventsButton.getText().toString()
                        .equals(getString(R.string.btnUnfollow)))
                    this.unfollow();
                if (mFollowOrEventsButton.getText().toString()
                        .equals(getString(R.string.btnEvents))) {
                    Intent intent = new Intent(this, EventsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(KEY_USER_ID, mUserId);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(final int position) {
        mCurrentPage = position;
    }

    @Override
    public void onPageScrollStateChanged(final int state) {

    }

    @Override
    public void onListDialogDismissOk(Object id, Parcelable p) {
        if (p instanceof Page) {
            Map<String, String> headers = new HashMap<>();
            headers.put(Constants.HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
            headers.put(Constants.HEADER_ACCEPT, Constants.APPLICATION_JSON);

            Map<String, String> parameters = new HashMap<>();
            parameters.put(
                    Constants.PARAM_TOKEN,
                    PreferencesUtils.get(UserActivity.this, Constants.PREF_TOKEN)
            );

            Map<String, String> page = new HashMap<>();
            page.put(PARAM_PAGE, ((Page) p).getTitle());

            final String body = GsonUtils.getInstance().toJson(page);

            final APICall call = new APICall(
                    this,
                    Constants.INTENT_USER_FOLLOW,
                    Constants.PATCH,
                    Constants.API_USER_FOLLOW + '/' + mUserId,
                    body,
                    headers,
                    parameters
            );
            if (!call.isLoading()) call.execute();
        }
    }

    @Override
    public void onTextDialogDismissOk(Object o) {
        Utils.toast(UserActivity.this, R.string.message_followingUser, mCurrentUser.getUsername());
    }

    private void getUser() {
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
        headers.put(Constants.HEADER_ACCEPT, Constants.APPLICATION_JSON);

        Map<String, String> parameters = new HashMap<>();
        parameters.put(Constants.PARAM_TOKEN, PreferencesUtils.get(this, Constants.PREF_TOKEN));

        final APICall call = new APICall(
                this,
                Constants.INTENT_USER_GET,
                Constants.GET,
                Constants.API_USERS_USER + '/' + mUserId,
                headers,
                parameters
        );
        if (!call.isLoading()) call.execute();
    }

    private void getPages() {
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
        headers.put(Constants.HEADER_ACCEPT, Constants.APPLICATION_JSON);

        Map<String, String> parameters = new HashMap<>();
        parameters.put(Constants.PARAM_TOKEN, PreferencesUtils.get(this, Constants.PREF_TOKEN));

        final APICall call = new APICall(
                this,
                Constants.INTENT_PAGES_LIST,
                Constants.GET,
                Constants.API_USERS_PAGES + '/' + mConnectedUserId,
                headers,
                parameters
        );
        if (!call.isLoading()) call.execute();
    }

    private void follow(List<Page> pages) {
        final RecyclerView.Adapter adapter = new PagesAdapter(pages);
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
                UserActivity.this
        );
        final DividerItemDecoration divider = new DividerItemDecoration(
                UserActivity.this, DividerItemDecoration.HORIZONTAL
        );

        ListDialogFragment.generate(
                getSupportFragmentManager(),
                UserActivity.this,
                getString(R.string.dialogTitle_choosePage),
                (ArrayList<Page>) pages,
                adapter,
                layoutManager,
                divider,
                0
        );
    }


    private void unfollow() {
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
        headers.put(Constants.HEADER_ACCEPT, Constants.APPLICATION_JSON);

        Map<String, String> parameters = new HashMap<>();
        parameters.put(Constants.PARAM_TOKEN, PreferencesUtils.get(this, Constants.PREF_TOKEN));

        final APICall call = new APICall(
                this,
                Constants.INTENT_USER_UNFOLLOW,
                Constants.PATCH,
                Constants.API_USER_UNFOLLOW + '/' + mUserId,
                headers,
                parameters
        );
        if (!call.isLoading()) call.execute();
    }
}

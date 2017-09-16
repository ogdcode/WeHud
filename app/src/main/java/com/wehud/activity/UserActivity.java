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
import com.wehud.adapter.VPAdapter;
import com.wehud.dialog.ListDialogFragment;
import com.wehud.fragment.UserPlanningsFragment;
import com.wehud.fragment.UserPostsFragment;
import com.wehud.model.Follower;
import com.wehud.model.Page;
import com.wehud.model.Payload;
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
        ListDialogFragment.OnListDialogDismissOkListener {

    private static final String KEY_CURRENT_PAGE = "key_currentPage";

    private ImageView mAvatar;
    private TextView mUsername;
    private TextView mScore;
    private TextView mFollowers;
    private Button mFollowOrEventsButton;

    private User mCurrentUser;
    private String mUserId;
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
                            String avatar = mCurrentUser.getAvatar();
                            String username = mCurrentUser.getUsername();
                            String score = String.valueOf(mCurrentUser.getScore())
                                    + "\t" + getString(R.string.score
                            );
                            List<User> followers = mCurrentUser.getFollowers();
                            String numFollowers = followers.size() + "\t"
                                    + getString(R.string.followerCount);

                            if (mIsConnectedUser)
                                mFollowOrEventsButton.setText(getString(R.string.btnEvents));
                            else {
                                boolean found = false;
                                for (User user : followers) {
                                    if (user.getId().equals(mUserId)) {
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
                            mScore.setText(score);
                            mFollowers.setText(numFollowers);
                            break;
                        case Constants.INTENT_USER_FOLLOW:
                            mFollowOrEventsButton.setText(getString(R.string.btnUnfollow));
                            Follower newFollower = GsonUtils.getInstance().fromJson(
                                    content, Follower.class
                            );

                            mCurrentUser.follow(newFollower.getUser());
                            String countAfterFollow = mCurrentUser.getFollowers().size()
                                    + "\t" + getString(R.string.followerCount
                            );

                            mFollowers.setText(countAfterFollow);

                            Utils.toast(
                                    UserActivity.this,
                                    R.string.message_followingUser,
                                    mCurrentUser.getUsername()
                            );
                            break;
                        case Constants.INTENT_USER_UNFOLLOW:
                            mFollowOrEventsButton.setText(getString(R.string.btnFollow));
                            Follower oldFollower = GsonUtils.getInstance().fromJson(content, Follower.class);

                            mCurrentUser.unfollow(oldFollower.getUser());
                            String countAfterUnfollow = mCurrentUser.getFollowers().size()
                                    + "\t" + getString(R.string.followerCount
                            );

                            mFollowers.setText(countAfterUnfollow);

                            Utils.toast(
                                    UserActivity.this,
                                    getString(R.string.message_unfollowingUser)
                            );
                            break;
                        case Constants.INTENT_PAGES_LIST:
                            Type pageListType = new TypeToken<List<Page>>(){}.getType();
                            List<Page> pages = GsonUtils.getInstance().fromJson(
                                    content, pageListType
                            );

                            UserActivity.this.follow(pages);
                            break;
                        default:
                            break;
                    }
                } else if (Integer.valueOf(code) == Constants.HTTP_INTERNAL_SERVER_ERROR)
                    Utils.toast(UserActivity.this, getString(R.string.error_server));
                else Utils.toast(UserActivity.this, R.string.error_general, code);
            }

            /*
            String payload = intent.getStringExtra(Constants.EXTRA_BROADCAST);

            if (intent.getAction().equals(Constants.INTENT_USER_GET) && !mPaused) {
                mCurrentUser = GsonUtils.getInstance().fromJson(payload, User.class);
                String avatar = mCurrentUser.getAvatar();
                String username = mCurrentUser.getUsername();
                String score = String.valueOf(mCurrentUser.getScore())
                        + "\t" + getString(R.string.score
                );
                List<User> followers = mCurrentUser.getFollowers();
                String numFollowers = followers.size() + "\t" + getString(R.string.followerCount);

                boolean found = false;
                for (User user : followers) {
                    if (user.getId().equals(mUserId)) {
                        found = true;
                        break;
                    }
                }

                if (found) mFollowOrEventsButton.setText(getString(R.string.btnUnfollow));
                else mFollowOrEventsButton.setText(getString(R.string.btnFollow));

                if (!TextUtils.isEmpty(avatar))
                    Utils.loadImage(UserActivity.this, avatar, mAvatar, 256);
                else mAvatar.setImageResource(R.mipmap.ic_launcher_round);

                mUsername.setText(username);
                mScore.setText(score);
                mFollowers.setText(numFollowers);
            }

            if (intent.getAction().equals(Constants.INTENT_PAGES_LIST) && !mPaused) {
                Type pageListType = new TypeToken<List<Page>>(){}.getType();
                ArrayList<Page> pages = GsonUtils.getInstance().fromJson(payload, pageListType);

                UserActivity.this.follow(pages);
            }

            if (intent.getAction().equals(Constants.INTENT_USER_FOLLOW) && !mPaused) {
                mFollowOrEventsButton.setText(getString(R.string.btnUnfollow));
                Follower newFollower = GsonUtils.getInstance().fromJson(payload, Follower.class);

                mCurrentUser.follow(newFollower.getUser());
                String newNbrFollowers = mCurrentUser.getFollowers().size()
                        + "\t" + getString(R.string.followerCount
                );

                mFollowers.setText(newNbrFollowers);

                Utils.toast(UserActivity.this, getString(R.string.message_followingUser));
            }

            if (intent.getAction().equals(Constants.INTENT_USER_UNFOLLOW) && !mPaused) {
                mFollowOrEventsButton.setText(getString(R.string.btnFollow));
                Follower oldFollower = GsonUtils.getInstance().fromJson(payload, Follower.class);

                mCurrentUser.unfollow(oldFollower.getUser());
                String newNbrFollowers = mCurrentUser.getFollowers().size()
                        + "\t" + getString(R.string.followerCount
                );

                mFollowers.setText(newNbrFollowers);

                Utils.toast(UserActivity.this, getString(R.string.message_unfollowingUser));
            }
            */
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        mIsConnectedUser = Utils.isConnectedUser(this, PreferencesUtils.get(this, Constants.PREF_USER_ID));

        if (savedInstanceState != null) {
            mCurrentPage = savedInstanceState.getInt(KEY_CURRENT_PAGE);
            mUserId = savedInstanceState.getString(Constants.PREF_USER_ID);
        } else {
            mCurrentPage = 0;

            Intent intent = getIntent();
            if (intent != null) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) mUserId = bundle.getString(Constants.PREF_USER_ID);
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

        VPAdapter adapter = new VPAdapter(getSupportFragmentManager());
        adapter.add(UserPostsFragment.newInstance(mUserId), getString(R.string.tab_posts));
        adapter.add(UserPlanningsFragment.newInstance(mUserId), getString(R.string.tab_plannings));

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
        outState.putString(Constants.PREF_USER_ID, mUserId);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnFollow:
                if (mFollowOrEventsButton.getText().toString()
                        .equals(getString(R.string.btnFollow)))
                    this.getPages();
                if (mFollowOrEventsButton.getText().toString()
                        .equals(getString(R.string.btnUnfollow)))
                    this.unfollow();
                if (mFollowOrEventsButton.getText().toString()
                        .equals(getString(R.string.btnEvents)))
                    startActivity(new Intent(this, EventsActivity.class));
                break;
            default:
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mCurrentPage = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onListDialogDismissOk(Parcelable p) {

    }

    private void getUser() {
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
        headers.put(Constants.HEADER_ACCEPT, Constants.APPLICATION_JSON);

        Map<String, String> parameters = new HashMap<>();
        parameters.put(Constants.PARAM_TOKEN, Constants.TOKEN);

        APICall call = new APICall(
                this,
                Constants.INTENT_USER_GET,
                Constants.GET,
                Constants.API_USERS + '/' + mUserId,
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
        parameters.put(Constants.PARAM_TOKEN, Constants.TOKEN);

        APICall call = new APICall(
                this,
                Constants.INTENT_PAGES_LIST,
                Constants.GET,
                Constants.API_PAGES,
                headers,
                parameters
        );
        if (!call.isLoading()) call.execute();
    }

    private void follow(List<Page> pages) {
        RecyclerView.Adapter adapter = new PagesAdapter(pages);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
                UserActivity.this
        );
        DividerItemDecoration divider = new DividerItemDecoration(
                UserActivity.this, DividerItemDecoration.HORIZONTAL
        );

        ListDialogFragment.generate(
                getSupportFragmentManager(),
                UserActivity.this,
                getString(R.string.dialogTitle_choosePage),
                (ArrayList<Page>) pages,
                adapter,
                layoutManager,
                divider
        );
    }


    private void unfollow() {
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
        headers.put(Constants.HEADER_ACCEPT, Constants.APPLICATION_JSON);

        Map<String, String> parameters = new HashMap<>();
        parameters.put(Constants.PARAM_TOKEN, Constants.TOKEN);

        APICall call = new APICall(
                this,
                Constants.INTENT_USER_UNFOLLOW,
                Constants.PATCH,
                Constants.API_USER_UNFOLLOW,
                headers,
                parameters
        );
        if (!call.isLoading()) call.execute();
    }
}

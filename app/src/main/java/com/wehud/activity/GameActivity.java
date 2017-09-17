package com.wehud.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.wehud.R;
import com.wehud.adapter.PagesAdapter;
import com.wehud.dialog.ListDialogFragment;
import com.wehud.model.Follower;
import com.wehud.model.Game;
import com.wehud.model.Page;
import com.wehud.model.Payload;
import com.wehud.model.Status;
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

public class GameActivity extends AppCompatActivity implements View.OnClickListener,
        ListDialogFragment.OnListDialogDismissOkListener {

    private static final String KEY_GAME_ID = "key_game_id";
    private static final String PARAM_PAGE = "page";

    private ViewGroup mGenresLayout;
    private ViewGroup mDevelopersLayout;
    private ViewGroup mPublishersLayout;
    private ViewGroup mFranchiseLayout;
    private ViewGroup mMainGameLayout;
    private ViewGroup mModesLayout;
    private ViewGroup mFirstReleaseDateLayout;
    private ViewGroup mSynopsisLayout;
    private ViewGroup mWebsiteLayout;
    private ViewGroup mEsrbLayout;
    private ViewGroup mPegiLayout;

    private ImageView mCover;
    private TextView mName;
    private TextView mStatus;
    private TextView mFollowers;
    private TextView mGenres;
    private TextView mDevelopers;
    private TextView mPublishers;
    private TextView mFranchise;
    private TextView mMainGame;
    private TextView mModes;
    private TextView mFirstReleaseDate;
    private TextView mSynopsis;
    private TextView mWebsite;
    private TextView mEsrb;
    private TextView mPegi;

    private Button mFollowButton;

    private Game mCurrentGame;

    private boolean mPaused;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!mPaused) {
                final String response = intent.getStringExtra(Constants.EXTRA_BROADCAST);
                final Payload payload = GsonUtils.getInstance().fromJson(response, Payload.class);

                final String code = payload.getCode();

                if (Integer.valueOf(code) == Constants.HTTP_OK) {
                    final String content = payload.getContent();

                    switch (intent.getAction()) {
                        case Constants.INTENT_GAME_GET:
                            GameActivity.this.fillGamePage(content);
                            break;
                        case Constants.INTENT_PAGES_LIST:
                            final Type pageListType = new TypeToken<List<Page>>(){}.getType();
                            ArrayList<Page> pages = GsonUtils.getInstance().fromJson(
                                    content, pageListType
                            );
                            GameActivity.this.follow(pages);
                            break;
                        case Constants.INTENT_GAME_FOLLOW:
                            mFollowButton.setText(getString(R.string.btnUnfollow));
                            final Follower newFollower = GsonUtils.getInstance().fromJson(
                                    content, Follower.class
                            );

                            mCurrentGame.follow(newFollower.getUser());
                            final String countAfterFollow = mCurrentGame.getFollowers().size()
                                    + "\t" + getString(R.string.followerCount);

                            mFollowers.setText(countAfterFollow);

                            Utils.toast(GameActivity.this,
                                    R.string.message_followingGame, mCurrentGame.getName()
                            );
                            break;
                        case Constants.INTENT_GAME_UNFOLLOW:
                            mFollowButton.setText(getString(R.string.btnFollow));
                            final Follower oldFollowed = GsonUtils.getInstance().fromJson(content, Follower.class);

                            mCurrentGame.unfollow(oldFollowed.getUser());
                            final String countAfterUnfollow = mCurrentGame.getFollowers().size()
                                    + "\t" + getString(R.string.followerCount);

                            mFollowers.setText(countAfterUnfollow);

                            Utils.toast(GameActivity.this,
                                    R.string.message_unfollowingGame, mCurrentGame.getName()
                            );
                            break;
                        default:
                            break;
                    }
                } else {
                    int messageId;
                    switch (Integer.valueOf(code)) {
                        case Constants.HTTP_METHOD_NOT_ALLOWED:
                            messageId = R.string.error_sessionExpired;
                            finish();
                            break;
                        case Constants.HTTP_INTERNAL_SERVER_ERROR:
                            messageId = R.string.error_server;
                            break;
                        default:
                            Utils.toast(GameActivity.this, R.string.error_general, code);
                            return;
                    }

                    Utils.toast(GameActivity.this, messageId);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        mGenresLayout = (ViewGroup) findViewById(R.id.layout_genres);
        mDevelopersLayout = (ViewGroup) findViewById(R.id.layout_developers);
        mPublishersLayout = (ViewGroup) findViewById(R.id.layout_publishers);
        mFranchiseLayout = (ViewGroup) findViewById(R.id.layout_franchise);
        mMainGameLayout = (ViewGroup) findViewById(R.id.layout_mainGame);
        mModesLayout = (ViewGroup) findViewById(R.id.layout_modes);
        mFirstReleaseDateLayout = (ViewGroup) findViewById(R.id.layout_firstReleaseDate);
        mSynopsisLayout = (ViewGroup) findViewById(R.id.layout_synopsis);
        mWebsiteLayout = (ViewGroup) findViewById(R.id.layout_website);
        mEsrbLayout = (ViewGroup) findViewById(R.id.layout_esrb);
        mPegiLayout = (ViewGroup) findViewById(R.id.layout_pegi);

        mCover = (ImageView) findViewById(R.id.avatar);
        mName = (TextView) findViewById(R.id.name);
        mStatus = (TextView) findViewById(R.id.status);
        mFollowers = (TextView) findViewById(R.id.followers);
        mGenres = (TextView) findViewById(R.id.genres);
        mDevelopers = (TextView) findViewById(R.id.developers);
        mPublishers = (TextView) findViewById(R.id.publishers);
        mFranchise = (TextView) findViewById(R.id.franchise);
        mMainGame = (TextView) findViewById(R.id.mainGame);
        mModes = (TextView) findViewById(R.id.modes);
        mFirstReleaseDate = (TextView) findViewById(R.id.firstReleaseDate);
        mSynopsis = (TextView) findViewById(R.id.synopsis);
        mWebsite = (TextView) findViewById(R.id.website);
        mEsrb = (TextView) findViewById(R.id.esrb);
        mPegi = (TextView) findViewById(R.id.pegi);

        mFollowButton = (Button) findViewById(R.id.btnFollow);
        mFollowButton.setOnClickListener(this);

        this.initializeFields();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.INTENT_GAME_GET);
        filter.addAction(Constants.INTENT_PAGES_LIST);
        filter.addAction(Constants.INTENT_GAME_FOLLOW);
        filter.addAction(Constants.INTENT_GAME_UNFOLLOW);

        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mPaused) {
            final Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                this.getGame(bundle.getString(KEY_GAME_ID));
            }
        }

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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnFollow:
                if (mFollowButton.getText().equals(getString(R.string.btnFollow)))
                    this.getPages();
                if (mFollowButton.getText().equals(getString(R.string.btnUnfollow)))
                    this.unfollow();
                break;
            default:
                break;
        }
    }

    @Override
    public void onListDialogDismissOk(Parcelable p) {
        if (p instanceof Page) {
            final String pageTitle = ((Page) p).getTitle();

            Map<String, String> headers = new HashMap<>();
            headers.put(Constants.HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
            headers.put(Constants.HEADER_ACCEPT, Constants.APPLICATION_JSON);

            Map<String, String> parameters = new HashMap<>();
            parameters.put(Constants.PARAM_TOKEN, PreferencesUtils.get(this, Constants.PREF_TOKEN));

            Map<String, String> page = new HashMap<>();
            page.put(PARAM_PAGE, pageTitle);

            final String body = GsonUtils.getInstance().toJson(page);

            final APICall call = new APICall(
                    this,
                    Constants.INTENT_GAME_FOLLOW,
                    Constants.PATCH,
                    Constants.API_GAME_FOLLOW + '/' + mCurrentGame.getId(),
                    body,
                    headers,
                    parameters
            );
            if (!call.isLoading()) call.execute();
        }
    }

    private void initializeFields() {
        mGenresLayout.setVisibility(View.GONE);
        mDevelopersLayout.setVisibility(View.GONE);
        mPublishersLayout.setVisibility(View.GONE);
        mFranchiseLayout.setVisibility(View.GONE);
        mMainGameLayout.setVisibility(View.GONE);
        mModesLayout.setVisibility(View.GONE);
        mFirstReleaseDateLayout.setVisibility(View.GONE);
        mSynopsisLayout.setVisibility(View.GONE);
        mWebsiteLayout.setVisibility(View.GONE);
        mEsrbLayout.setVisibility(View.GONE);
        mPegiLayout.setVisibility(View.GONE);
    }

    private void fillGamePage(String content) {
        final String connectedUserId = PreferencesUtils.get(this, Constants.PREF_USER_ID);

        mCurrentGame = GsonUtils.getInstance().fromJson(content, Game.class);
        final String cover = "https://" + mCurrentGame.getCover();
        final String name = mCurrentGame.getName();
        final Status status = Utils.getStatus(GameActivity.this, mCurrentGame.getStatus());
        final List<User> followers = mCurrentGame.getFollowers();
        final List<String> genres = mCurrentGame.getGenres();
        final List<String> developers = mCurrentGame.getDevelopers();
        final List<String> publishers = mCurrentGame.getPublishers();
        final String franchise = mCurrentGame.getFranchise();
        final String mainGame = mCurrentGame.getGame();
        final List<String> modes = mCurrentGame.getModes();
        final String firstReleaseDate = Utils.timestampToLocalDateString(
                mCurrentGame.getFirstReleaseDate()
        );
        final String synopsis = mCurrentGame.getSynopsis();
        final String website = mCurrentGame.getWebsite();
        final String esrb = mCurrentGame.getEsrb();
        final String pegi = mCurrentGame.getPegi();

        Utils.loadImage(GameActivity.this, cover, mCover);
        mName.setText(name);

        boolean found = false;
        for (User user : followers) {
            if (user.getId().equals(connectedUserId)) {
                found = true;
                break;
            }
        }

        if (found) mFollowButton.setText(getString(R.string.btnUnfollow));
        else mFollowButton.setText(getString(R.string.btnFollow));

        final String numFollowers = followers.size() + ' '
                + getString(R.string.followerCount);
        mFollowers.setText(numFollowers);

        mStatus.setCompoundDrawablesWithIntrinsicBounds(
                status.getIcon(), 0, 0, 0
        );
        mStatus.setText(status.getDescription());
        if (!genres.isEmpty()) {
            Utils.putStringListIntoTextView(mGenres, genres);
            mGenresLayout.setVisibility(View.VISIBLE);
        }
        if (!developers.isEmpty()) {
            Utils.putStringListIntoTextView(mDevelopers, developers);
            mDevelopersLayout.setVisibility(View.VISIBLE);
        }
        if (!publishers.isEmpty()) {
            Utils.putStringListIntoTextView(mPublishers, publishers);
            mPublishersLayout.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(franchise)) {
            mFranchise.setText(franchise);
            mFranchiseLayout.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(mainGame)) {
            mMainGame.setText(mainGame);
            mMainGameLayout.setVisibility(View.VISIBLE);
        }
        if (!modes.isEmpty()) {
            Utils.putStringListIntoTextView(mModes, modes);
            mModesLayout.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(firstReleaseDate)) {
            mFirstReleaseDate.setText(firstReleaseDate);
            mFirstReleaseDateLayout.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(synopsis)) {
            mSynopsis.setText(synopsis);
            mSynopsisLayout.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(website)) {
            mWebsite.setText(website);
            mWebsiteLayout.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(esrb)) {
            mEsrb.setText(esrb);
            mEsrbLayout.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(pegi)) {
            mPegi.setText(pegi);
            mPegiLayout.setVisibility(View.VISIBLE);
        }
    }

    private void getGame(String id) {
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
        headers.put(Constants.HEADER_ACCEPT, Constants.APPLICATION_JSON);

        Map<String, String> parameters = new HashMap<>();
        parameters.put(Constants.PARAM_TOKEN, PreferencesUtils.get(this, Constants.PREF_TOKEN));

        final APICall call = new APICall(
                this,
                Constants.INTENT_GAME_GET,
                Constants.GET,
                Constants.API_GAMES + '/' + id,
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
                Constants.API_PAGES,
                headers,
                parameters
        );
        if (!call.isLoading()) call.execute();
    }

    private void follow(ArrayList<Page> pages) {
        final RecyclerView.Adapter adapter = new PagesAdapter(pages);
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
                GameActivity.this
        );
        final DividerItemDecoration divider = new DividerItemDecoration(
                GameActivity.this, DividerItemDecoration.HORIZONTAL
        );

        ListDialogFragment.generate(
                getSupportFragmentManager(),
                GameActivity.this,
                getString(R.string.dialogTitle_choosePage),
                pages,
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
        parameters.put(Constants.PARAM_TOKEN, PreferencesUtils.get(this, Constants.PREF_TOKEN));

        final APICall call = new APICall(
                this,
                Constants.INTENT_GAME_UNFOLLOW,
                Constants.PATCH,
                Constants.API_GAME_UNFOLLOW + '/' + mCurrentGame.getId(),
                headers,
                parameters
        );
        if (!call.isLoading()) call.execute();
    }
}

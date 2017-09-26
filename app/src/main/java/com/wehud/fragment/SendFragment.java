package com.wehud.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.wehud.R;
import com.wehud.adapter.GamesAdapter;
import com.wehud.adapter.UsersAdapter;
import com.wehud.dialog.ListDialogFragment;
import com.wehud.dialog.TextDialogFragment;
import com.wehud.model.Game;
import com.wehud.model.Payload;
import com.wehud.model.Reward;
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

public class SendFragment extends Fragment
        implements View.OnClickListener, CompoundButton.OnCheckedChangeListener,
        ListDialogFragment.OnListDialogDismissOkListener,
        TextDialogFragment.OnTextDialogDismissOkListener {

    private static final String PARAM_TEXT = "text";
    private static final String PARAM_VIDEO_URI = "videoUri";
    private static final String PARAM_GAME = "game";
    private static final String PARAM_RATING = "rating";
    private static final String PARAM_FOLLOWER = "receiver";

    private Context mContext;
    private EditText mNewPostText;
    private EditText mNewPostVideo;
    private ViewGroup mNewPostGameLayout;
    private ViewGroup mNewPostFollowerLayout;
    private TextView mNewPostGame;
    private TextView mNewPostFollower;
    private CheckBox mNewPostIsOpinion;
    private CheckBox mNewPostIsMessage;
    private RatingBar mNewPostGameRating;
    private Button mAddGameButton;
    private Button mAddFollowerButton;

    private List<Game> mGames;
    private List<User> mFollowers;

    private boolean mPaused;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (!mPaused) {
                final String response = intent.getStringExtra(Constants.EXTRA_BROADCAST);
                final Payload payload = GsonUtils.getInstance().fromJson(response, Payload.class);

                final String code = payload.getCode();

                if (Integer.valueOf(code) == Constants.HTTP_OK ||
                        Integer.valueOf(code) == Constants.HTTP_CREATED) {
                    final String content = payload.getContent();

                    switch (intent.getAction()) {
                        case Constants.INTENT_POSTS_ADD:
                            Reward reward = Utils.getNestedReward(content);
                            if (Utils.isNotEmpty(reward.getEntities())) {
                                Utils.generateRewardDialog(
                                        mContext,
                                        getFragmentManager(),
                                        SendFragment.this,
                                        reward,
                                        0
                                );
                            } else Utils.toast(mContext, R.string.message_newPost);
                            break;
                        case Constants.INTENT_FOLLOWERS_LIST:
                            User currentUser = GsonUtils.getInstance().fromJson(
                                    content, User.class
                            );
                            mFollowers = currentUser.getFollowers();
                            if (mFollowers == null || mFollowers.isEmpty()) {
                                mNewPostIsMessage.setVisibility(View.GONE);
                                mAddFollowerButton.setVisibility(View.GONE);
                            }
                            break;
                        case Constants.INTENT_GAMES_LIST:
                            final Type gameListType = new TypeToken<List<Game>>(){}.getType();
                            mGames = GsonUtils.getInstance().fromJson(content, gameListType);
                            if (mGames == null || mGames.isEmpty()) {
                                mNewPostIsOpinion.setVisibility(View.GONE);
                                mAddGameButton.setVisibility(View.GONE);
                                mNewPostGameRating.setVisibility(View.GONE);
                            }
                            break;
                        default:
                            break;
                    }

                } else {
                    int messageId;
                    switch (Integer.valueOf(code)) {
                        case Constants.HTTP_UNAUTHORIZED:
                            messageId = R.string.error_sessionExpired;
                            getActivity().finish();
                            break;
                        case Constants.HTTP_INTERNAL_SERVER_ERROR:
                            messageId = R.string.error_server;
                            break;
                        default:
                            Utils.toast(mContext, R.string.error_general, code);
                            return;
                    }

                    Utils.toast(mContext, messageId);
                }
            }
        }
    };

    public static SendFragment newInstance() {
        return new SendFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        final View view = inflater.inflate(R.layout.fragment_send, container, false);
        mContext = view.getContext();

        mNewPostText = (EditText) view.findViewById(R.id.newPost_text);
        mNewPostVideo = (EditText) view.findViewById(R.id.newPost_video);
        mNewPostGameLayout = (ViewGroup) view.findViewById(R.id.newPost_gameLayout);
        mNewPostFollowerLayout = (ViewGroup) view.findViewById(R.id.newPost_followerLayout);
        mNewPostGame = (TextView) view.findViewById(R.id.newPost_game);
        mNewPostFollower = (TextView) view.findViewById(R.id.newPost_follower);
        mNewPostGameRating = (RatingBar) view.findViewById(R.id.newPost_gameRating);

        mNewPostIsOpinion = (CheckBox) view.findViewById(R.id.newPost_isOpinion);
        mNewPostIsMessage = (CheckBox) view.findViewById(R.id.newPost_isMessage);
        mNewPostIsOpinion.setOnCheckedChangeListener(this);
        mNewPostIsMessage.setOnCheckedChangeListener(this);

        mAddGameButton = (Button) view.findViewById(R.id.newPost_btnAddGame);
        mAddFollowerButton = (Button) view.findViewById(R.id.newPost_btnAddFollower);
        mAddGameButton.setOnClickListener(this);
        mAddFollowerButton.setOnClickListener(this);

        mNewPostGameLayout.setVisibility(View.GONE);
        mNewPostFollowerLayout.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.INTENT_FOLLOWERS_LIST);
        filter.addAction(Constants.INTENT_GAMES_LIST);
        filter.addAction(Constants.INTENT_POSTS_ADD);
        mContext.registerReceiver(mReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        mPaused = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mPaused) {
            this.getFollowers();
            this.getGames();
        }

        mPaused = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContext.unregisterReceiver(mReceiver);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_publish, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_post_add:
                this.createPost();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public void onClick(View view) {
        final FragmentManager manager = getFragmentManager();
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
        final DividerItemDecoration divider = new DividerItemDecoration(
                mContext, DividerItemDecoration.HORIZONTAL
        );
        String dialogTitle;
        ArrayList<? extends Parcelable> list;
        RecyclerView.Adapter adapter;

        switch (view.getId()) {
            case R.id.newPost_btnAddGame:
                dialogTitle = getString(R.string.dialogTitle_addGame);
                list = (ArrayList<Game>) mGames;
                adapter = new GamesAdapter(mGames);
                break;
            case R.id.newPost_btnAddFollower:
                dialogTitle = getString(R.string.dialogTitle_addFollower);
                list = (ArrayList<User>) mFollowers;
                adapter = new UsersAdapter(mFollowers);
                break;
            default:
                return;
        }

        ListDialogFragment.generate(
                manager,
                this,
                dialogTitle,
                list,
                adapter,
                layoutManager,
                divider
        );
    }

    @Override
    public void onCheckedChanged(CompoundButton checkbox, boolean checked) {
        switch (checkbox.getId()) {
            case R.id.newPost_isOpinion:
                mNewPostGameLayout.setVisibility(checked ? View.VISIBLE : View.GONE);
                break;
            case R.id.newPost_isMessage:
                mNewPostFollowerLayout.setVisibility(checked ? View.VISIBLE : View.GONE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onListDialogDismissOk(Parcelable p) {
        if (p instanceof User)
            mNewPostFollower.setText(((User) p).getUsername());
        if (p instanceof Game)
            mNewPostGame.setText(((Game) p).getName());
    }

    @Override
    public void onTextDialogDismissOk(Object o) {
        Utils.toast(mContext, R.string.message_newPost);
    }

    private void getFollowers() {
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
        headers.put(Constants.HEADER_ACCEPT, Constants.APPLICATION_JSON);

        Map<String, String> parameters = new HashMap<>();
        parameters.put(Constants.PARAM_TOKEN, PreferencesUtils.get(mContext, Constants.PREF_TOKEN));

        final String connectedUserId = PreferencesUtils.get(mContext, Constants.PREF_USER_ID);

        final APICall call = new APICall(
                mContext,
                Constants.INTENT_FOLLOWERS_LIST,
                Constants.GET,
                Constants.API_USERS + '/' + connectedUserId,
                headers,
                parameters
        );
        if (!call.isLoading()) call.execute();
    }

    private void getGames() {
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
        headers.put(Constants.HEADER_ACCEPT, Constants.APPLICATION_JSON);

        final APICall call = new APICall(
                mContext,
                Constants.INTENT_GAMES_LIST,
                Constants.GET,
                Constants.API_GAMES,
                headers
        );
        if (!call.isLoading()) call.execute();
    }

    private void createPost() {
        final String text = mNewPostText.getText().toString();
        final String videoUri = mNewPostVideo.getText().toString();
        final String game = mNewPostGame.getText().toString();
        final String follower = mNewPostFollower.getText().toString();
        final String rating = String.valueOf(mNewPostGameRating.getRating());

        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
        headers.put(Constants.HEADER_ACCEPT, Constants.APPLICATION_JSON);

        Map<String, String> parameters = new HashMap<>();
        parameters.put(Constants.PARAM_TOKEN, PreferencesUtils.get(mContext, Constants.PREF_TOKEN));

        Map<String, String> post = new HashMap<>();
        post.put(PARAM_TEXT, text);

        if (!TextUtils.isEmpty(videoUri)) post.put(PARAM_VIDEO_URI, videoUri);

        if (mNewPostGameLayout.getVisibility() == View.VISIBLE)
            if (!TextUtils.isEmpty(game) && !TextUtils.isEmpty(rating)) {
                post.put(PARAM_GAME, game);
                post.put(PARAM_RATING, rating);
            }

        if (mNewPostFollowerLayout.getVisibility() == View.VISIBLE)
            if (!TextUtils.isEmpty(follower)) post.put(PARAM_FOLLOWER, follower);

        final String body = GsonUtils.getInstance().toJson(post);

        final APICall call = new APICall(
                mContext,
                Constants.INTENT_POSTS_ADD,
                Constants.POST,
                Constants.API_POSTS,
                body,
                headers,
                parameters
        );
        if (!call.isLoading()) call.execute();
    }
}

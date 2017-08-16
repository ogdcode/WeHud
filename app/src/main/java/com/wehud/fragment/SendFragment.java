package com.wehud.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
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
import com.wehud.adapter.UsersAdapter;
import com.wehud.dialog.ListDialogFragment;
import com.wehud.model.Game;
import com.wehud.model.User;
import com.wehud.network.APICall;
import com.wehud.util.Constants;
import com.wehud.util.GsonUtils;
import com.wehud.util.Utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SendFragment extends Fragment
        implements View.OnClickListener, CompoundButton.OnCheckedChangeListener,
        ListDialogFragment.OnDismissOkListener {

    private static final String PARAM_TEXT = "text";
    private static final String PARAM_VIDEO_URI = "videoUri";
    private static final String PARAM_GAME = "game";
    private static final String PARAM_RATING = "rating";
    private static final String PARAM_FOLLOWER = "receiver";

    private Context mContext;
    private EditText mNewPostText;
    private EditText mNewPostVideo;
    private CheckBox mNewPostIsOpinion;
    private CheckBox mNewPostIsMessage;
    private ViewGroup mNewPostGameLayout;
    private ViewGroup mNewPostFollowerLayout;
    private TextView mNewPostGame;
    private TextView mNewPostFollower;
    private RatingBar mNewPostGameRating;

    private List<Game> mGames;
    private ArrayList<User> mFollowers;

    private boolean mPaused;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String payload = intent.getStringExtra(Constants.EXTRA_API_RESPONSE);
            Log.d("MAIN", payload);

            if (intent.getAction().equals(Constants.INTENT_USERS_LIST) && !mPaused) {
                Type userListType = new TypeToken<List<User>>(){}.getType();
                mFollowers = GsonUtils.getInstance().fromJson(payload, userListType);
            }

            if (intent.getAction().equals(Constants.INTENT_POSTS_ADD) && !mPaused) {
                Utils.toast(mContext, "You published a post!");
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

        View view = inflater.inflate(R.layout.fragment_send, container, false);
        mContext = view.getContext();

        mNewPostText = (EditText) view.findViewById(R.id.newPost_text);
        mNewPostVideo = (EditText) view.findViewById(R.id.newPost_video);
        mNewPostIsOpinion = (CheckBox) view.findViewById(R.id.newPost_isOpinion);
        mNewPostIsMessage = (CheckBox) view.findViewById(R.id.newPost_isMessage);
        mNewPostGameLayout = (ViewGroup) view.findViewById(R.id.newPost_gameLayout);
        mNewPostFollowerLayout = (ViewGroup) view.findViewById(R.id.newPost_followerLayout);
        mNewPostGame = (TextView) view.findViewById(R.id.newPost_game);
        mNewPostFollower = (TextView) view.findViewById(R.id.newPost_follower);
        mNewPostGameRating = (RatingBar) view.findViewById(R.id.newPost_gameRating);

        mNewPostIsOpinion.setOnCheckedChangeListener(this);
        mNewPostIsMessage.setOnCheckedChangeListener(this);

        Button addGameButton = (Button) view.findViewById(R.id.newPost_btnAddGame);
        Button addFollowerButton = (Button) view.findViewById(R.id.newPost_btnAddFollower);
        addGameButton.setOnClickListener(this);
        addFollowerButton.setOnClickListener(this);

        mNewPostGameLayout.setVisibility(View.GONE);
        mNewPostFollowerLayout.setVisibility(View.GONE);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.INTENT_USERS_LIST);
        filter.addAction(Constants.INTENT_POSTS_ADD);
        mContext.registerReceiver(mReceiver, filter);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        mPaused = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mPaused) this.getFollowers();

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
            case R.id.menu_add:
                this.createPost();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.newPost_btnAddGame:
                break;
            case R.id.newPost_btnAddFollower:
                UsersAdapter adapter = new UsersAdapter(mFollowers);
                ListDialogFragment.generate(
                        getFragmentManager(),
                        this,
                        "Choose a follower",
                        mFollowers,
                        adapter
                );
                break;
            default:
                break;
        }
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
    public void onDismissOk(Parcelable p) {
        Log.d("MAIN", p.toString());
    }

    private void getFollowers() {
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
        headers.put(Constants.HEADER_ACCEPT, Constants.APPLICATION_JSON);

        APICall call = new APICall(
                mContext,
                Constants.INTENT_USERS_LIST,
                Constants.GET,
                Constants.API_USERS + "/all",
                headers
        );
        if (!call.isLoading()) call.execute();
    }

    private void createPost() {
        String text = mNewPostText.getText().toString();
        String videoUri = mNewPostVideo.getText().toString();
        String game = mNewPostGame.getText().toString();
        String follower = mNewPostFollower.getText().toString();
        String rating = String.valueOf(mNewPostGameRating.getRating());

        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
        headers.put(Constants.HEADER_ACCEPT, Constants.APPLICATION_JSON);

        Map<String, String> parameters = new HashMap<>();
        parameters.put(Constants.PARAM_TOKEN, Constants.TOKEN);

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

        String body = GsonUtils.getInstance().toJson(post);

        APICall call = new APICall(
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

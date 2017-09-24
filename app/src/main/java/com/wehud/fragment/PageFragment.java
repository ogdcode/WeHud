package com.wehud.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;
import com.wehud.R;
import com.wehud.adapter.PostsAdapter;
import com.wehud.dialog.TextDialogFragment;
import com.wehud.model.Payload;
import com.wehud.model.Post;
import com.wehud.model.RefreshResponse;
import com.wehud.model.Reward;
import com.wehud.network.APICall;
import com.wehud.util.Constants;
import com.wehud.util.GsonUtils;
import com.wehud.util.Utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        TextDialogFragment.OnTextDialogDismissOkListener {

    private static final String KEY_ID = "key_id";
    private static final String KEY_INDEX = "key_index";
    private static final String KEY_POSTS = "key_posts";

    private Context mContext;
    private PostsAdapter mAdapter;
    private View mEmptyLayout;
    private SwipeRefreshLayout mSwipeLayout;
    private RecyclerView mPostListView;

    private boolean mIsIndexZero;
    private String mId;
    private List<Post> mPosts;

    private boolean mPaused;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!mPaused) {
                if (!TextUtils.isEmpty(intent.getStringExtra(Constants.EXTRA_REFRESH_POSTS))) {
                    String payload = intent.getStringExtra(Constants.EXTRA_REFRESH_POSTS);
                    RefreshResponse response = GsonUtils.getInstance().fromJson(
                            payload,
                            RefreshResponse.class
                    );

                    if (response.getId().equals(mId)) {
                        mPosts = response.getPosts();
                        if (mPosts != null && !mPosts.isEmpty()) {
                            mAdapter.notifyDataSetChanged();
                            mPostListView.invalidate();
                        } else {
                            mEmptyLayout.setVisibility(View.VISIBLE);
                            mSwipeLayout.setVisibility(View.GONE);
                        }
                        mSwipeLayout.setRefreshing(false);
                    }
                }

                if (!TextUtils.isEmpty(intent.getStringExtra(Constants.EXTRA_BROADCAST))) {
                    String response = intent.getStringExtra(Constants.EXTRA_BROADCAST);
                    Payload payload = GsonUtils.getInstance().fromJson(response, Payload.class);

                    String code = payload.getCode();

                    if (Integer.valueOf(code) == Constants.HTTP_OK ||
                            Integer.valueOf(code) == Constants.HTTP_NO_CONTENT) {
                        String content = payload.getContent();

                        if (intent.getAction().equals(Constants.INTENT_POSTS_LIST)) {
                            final Type postListType = new TypeToken<List<Post>>(){}.getType();
                            mPosts = GsonUtils.getInstance().fromJson(content, postListType);
                            if (mPosts != null && !mPosts.isEmpty()) {
                                mAdapter = new PostsAdapter(mPosts, true);
                                mPostListView.setAdapter(mAdapter);

                                mEmptyLayout.setVisibility(View.GONE);
                                mSwipeLayout.setVisibility(View.VISIBLE);
                                mSwipeLayout.setRefreshing(false);
                            }
                        }
                        if (intent.getAction().equals(Constants.INTENT_POST_LIKE)) {
                            if (Integer.valueOf(code) == Constants.HTTP_OK) {
                                Reward reward = GsonUtils.getInstance().fromJson(
                                        content,
                                        Reward.class
                                );
                                Utils.generateRewardDialog(
                                        mContext,
                                        getFragmentManager(),
                                        PageFragment.this,
                                        reward,
                                        0
                                );
                            } else if (!mIsIndexZero) {
                                Intent likeIntent = new Intent(Constants.INTENT_REFRESH_PAGE);
                                likeIntent.putExtra(Constants.EXTRA_REFRESH_PAGE, mId);
                                mContext.sendBroadcast(likeIntent);
                            }
                            else getPosts();
                        }
                        if (intent.getAction().equals(Constants.INTENT_POST_DISLIKE)) {
                            if (!mIsIndexZero) {
                                Intent likeIntent = new Intent(Constants.INTENT_REFRESH_PAGE);
                                likeIntent.putExtra(Constants.EXTRA_REFRESH_PAGE, mId);
                                mContext.sendBroadcast(likeIntent);
                            } else getPosts();
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
        }
    };

    public static PageFragment newInstance(int index, String id, List<Post> posts) {
        PageFragment fragment = new PageFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_INDEX, index);
        args.putString(KEY_ID, id);
        if (posts != null) args.putParcelableArrayList(KEY_POSTS, (ArrayList<Post>) posts);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page, container, false);
        mContext = view.getContext();

        mEmptyLayout = view.findViewById(R.id.layout_empty);
        mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.layout_swipe);
        mPostListView = (RecyclerView) view.findViewById(android.R.id.list);

        mEmptyLayout.setVisibility(View.VISIBLE);
        mSwipeLayout.setVisibility(View.GONE);

        mSwipeLayout.setOnRefreshListener(this);
        mPostListView.setLayoutManager(new LinearLayoutManager(mContext));

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null)
            mPosts = savedInstanceState.getParcelableArrayList(KEY_POSTS);
        else {
            final IntentFilter filter = new IntentFilter();
            final Bundle args = getArguments();
            mId = args.getString(KEY_ID);
            mIsIndexZero = args.getInt(KEY_INDEX) == 0;
            if (mIsIndexZero) filter.addAction(Constants.INTENT_POSTS_LIST);
            else {
                mPosts = args.getParcelableArrayList(KEY_POSTS);
                if (mPosts != null && !mPosts.isEmpty()) {
                    mAdapter = new PostsAdapter(mPosts, true);
                    mPostListView.setAdapter(mAdapter);
                    mEmptyLayout.setVisibility(View.GONE);
                    mSwipeLayout.setVisibility(View.VISIBLE);
                }
            }

            filter.addAction(Constants.INTENT_POST_LIKE);
            filter.addAction(Constants.INTENT_POST_DISLIKE);
            filter.addAction(Constants.INTENT_REFRESH_POSTS);

            mContext.registerReceiver(mReceiver, filter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mPaused && mIsIndexZero) this.getPosts();

        mPaused = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        mPaused = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContext.unregisterReceiver(mReceiver);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mPosts != null && !mPosts.isEmpty())
            outState.putParcelableArrayList(KEY_POSTS, (ArrayList<Post>) mPosts);
    }

    @Override
    public void onRefresh() {
        if (mIsIndexZero) this.getPosts();
    }

    @Override
    public void onTextDialogDismissOk(Object id) {
        Intent likeIntent = new Intent(Constants.INTENT_REFRESH_PAGE);
        likeIntent.putExtra(Constants.EXTRA_REFRESH_PAGE, mId);
        mContext.sendBroadcast(likeIntent);
    }

    private void getPosts() {
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
        headers.put(Constants.HEADER_ACCEPT, Constants.APPLICATION_JSON);

        final APICall call = new APICall(
                mContext,
                Constants.INTENT_POSTS_LIST,
                Constants.GET,
                Constants.API_POSTS,
                headers
        );
        if (!call.isLoading()) call.execute();
    }
}

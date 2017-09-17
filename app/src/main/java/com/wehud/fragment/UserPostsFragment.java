package com.wehud.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;
import com.wehud.R;
import com.wehud.adapter.PostsAdapter;
import com.wehud.model.Payload;
import com.wehud.model.Post;
import com.wehud.network.APICall;
import com.wehud.util.Constants;
import com.wehud.util.GsonUtils;
import com.wehud.util.Utils;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserPostsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private Context mContext;
    private View mEmptyLayout;
    private SwipeRefreshLayout mSwipeLayout;
    private RecyclerView mPostListView;

    private String mUserId;

    private boolean mPaused;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.INTENT_POSTS_LIST) && !mPaused) {
                final String response = intent.getStringExtra(Constants.EXTRA_BROADCAST);
                final Payload payload = GsonUtils.getInstance().fromJson(response, Payload.class);

                final String code = payload.getCode();

                if (Integer.valueOf(code) == Constants.HTTP_OK) {
                    String content = payload.getContent();

                    final Type postListType = new TypeToken<List<Post>>(){}.getType();
                    final List<Post> posts = GsonUtils.getInstance()
                            .fromJson(content, postListType);
                    if (!posts.isEmpty()) {
                        final PostsAdapter adapter = new PostsAdapter(posts, false);
                        mPostListView.setAdapter(adapter);

                        mEmptyLayout.setVisibility(View.GONE);
                        mSwipeLayout.setVisibility(View.VISIBLE);
                        mSwipeLayout.setRefreshing(false);
                    }
                } else {
                    int messageId;
                    switch (Integer.valueOf(code)) {
                        case Constants.HTTP_METHOD_NOT_ALLOWED:
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

    public static Fragment newInstance(String userId) {
        Fragment fragment = new UserPostsFragment();
        Bundle args = new Bundle();
        args.putString(Constants.PREF_USER_ID, userId);
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_user_posts, container, false);
        mContext = view.getContext();

        mEmptyLayout = view.findViewById(R.id.layout_empty);
        mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.layout_swipe);
        mPostListView = (RecyclerView) view.findViewById(android.R.id.list);

        mEmptyLayout.setVisibility(View.VISIBLE);
        mSwipeLayout.setVisibility(View.GONE);

        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setRefreshing(true);
        this.getPosts();

        mPostListView.setLayoutManager(new LinearLayoutManager(mContext));
        mPostListView.addItemDecoration(new DividerItemDecoration(mContext,
                DividerItemDecoration.HORIZONTAL));

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null)
            mUserId = savedInstanceState.getString(Constants.PREF_USER_ID);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.INTENT_POSTS_LIST);
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
        Bundle args = getArguments();
        if (args != null) {
            mUserId = args.getString(Constants.PREF_USER_ID);
            if (!TextUtils.isEmpty(mUserId) && !mPaused) this.getPosts();
            else mSwipeLayout.setRefreshing(false);
        }

        mPaused = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContext.unregisterReceiver(mReceiver);
    }

    @Override
    public void onRefresh() {
        this.getPosts();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.PREF_USER_ID, mUserId);
    }

    private void getPosts() {
        if (!TextUtils.isEmpty(mUserId)) {
            Map<String, String> headers = new HashMap<>();
            headers.put(Constants.HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
            headers.put(Constants.HEADER_ACCEPT, Constants.APPLICATION_JSON);

            final APICall call = new APICall(
                    mContext,
                    Constants.INTENT_POSTS_LIST,
                    Constants.GET,
                    Constants.API_USERS_POSTS + '/' + mUserId,
                    headers
            );
            if (!call.isLoading()) call.execute();
        }
    }
}

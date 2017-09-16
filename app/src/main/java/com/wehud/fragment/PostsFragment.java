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

public class PostsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String KEY_PAGE_POSTS = "key_page_posts";

    private Context mContext;
    private View mEmptyLayout;
    private SwipeRefreshLayout mSwipeLayout;
    private RecyclerView mPostListView;

    private List<Post> mPosts;

    private boolean mPaused;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.INTENT_POSTS_LIST) && !mPaused) {
                String response = intent.getStringExtra(Constants.EXTRA_BROADCAST);
                Payload payload = GsonUtils.getInstance().fromJson(response, Payload.class);

                String code = payload.getCode();

                if (Integer.valueOf(code) == Constants.HTTP_OK) {
                    String content = payload.getContent();

                    Type postListType = new TypeToken<List<Post>>(){}.getType();
                    mPosts = GsonUtils.getInstance().fromJson(content, postListType);
                    if (!mPosts.isEmpty()) {
                        PostsAdapter adapter = new PostsAdapter(mPosts, true);
                        mPostListView.setAdapter(adapter);

                        mEmptyLayout.setVisibility(View.GONE);
                        mSwipeLayout.setVisibility(View.VISIBLE);
                        mSwipeLayout.setRefreshing(false);
                    }
                } else if (Integer.valueOf(code) == Constants.HTTP_INTERNAL_SERVER_ERROR)
                    Utils.toast(mContext, getString(R.string.error_server));
                else Utils.toast(mContext, R.string.error_general, code);
            }

            /*
            String payload = intent.getStringExtra(Constants.EXTRA_BROADCAST);

            if (intent.getAction().equals(Constants.INTENT_POSTS_LIST) && !mPaused) {
                Type postListType = new TypeToken<List<Post>>(){}.getType();
                mPosts = GsonUtils.getInstance().fromJson(payload, postListType);
                if (!mPosts.isEmpty()) {
                    PostsAdapter adapter = new PostsAdapter(mPosts, true);
                    mPostListView.setAdapter(adapter);

                    mEmptyLayout.setVisibility(View.GONE);
                    mSwipeLayout.setVisibility(View.VISIBLE);
                    mSwipeLayout.setRefreshing(false);
                }
            }
            */
        }
    };

    public static PostsFragment newInstance() {
        return new PostsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_posts, container, false);
        mContext = view.getContext();

        mEmptyLayout = view.findViewById(R.id.layout_empty);
        mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.layout_swipe);
        mPostListView = (RecyclerView) view.findViewById(android.R.id.list);

        mEmptyLayout.setVisibility(View.VISIBLE);
        mSwipeLayout.setVisibility(View.GONE);

        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setRefreshing(true);

        Bundle args = getArguments();
        if (args != null) {
            mPosts = args.getParcelableArrayList(KEY_PAGE_POSTS);
            if (mPosts != null && mPosts.isEmpty()) mSwipeLayout.setRefreshing(false);
        } else this.getPosts();


        mPostListView.setLayoutManager(new LinearLayoutManager(mContext));
        mPostListView.addItemDecoration(new DividerItemDecoration(mContext,
                DividerItemDecoration.HORIZONTAL));

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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
        mPaused = false;
        if (mReceiver != null) this.getPosts();
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

    private void getPosts() {
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
        headers.put(Constants.HEADER_ACCEPT, Constants.APPLICATION_JSON);

        APICall call = new APICall(
                mContext,
                Constants.INTENT_POSTS_LIST,
                Constants.GET,
                Constants.API_POSTS,
                headers
        );
        if (!call.isLoading()) call.execute();
    }
}

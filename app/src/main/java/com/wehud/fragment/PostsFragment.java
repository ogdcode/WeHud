package com.wehud.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;
import com.wehud.R;
import com.wehud.adapter.PostsAdapter;
import com.wehud.dialog.TextDialogFragment;
import com.wehud.model.Payload;
import com.wehud.model.Post;
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

public class PostsFragment extends Fragment implements TextDialogFragment.OnTextDialogDismissOkListener {

    private static final String KEY_PAGE_POSTS = "key_page_posts";

    private Context mContext;
    private View mEmptyLayout;
    private RecyclerView mPostListView;

    private List<Post> mPosts;

    private boolean mPaused;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!mPaused) {
                final String response = intent.getStringExtra(Constants.EXTRA_BROADCAST);
                final Payload payload = GsonUtils.getInstance().fromJson(response, Payload.class);

                final String code = payload.getCode();

                if (Integer.valueOf(code) == Constants.HTTP_OK ||
                        Integer.valueOf(code) == Constants.HTTP_NO_CONTENT) {
                    final String content = payload.getContent();

                    switch (intent.getAction()) {
                        case Constants.INTENT_POSTS_LIST:

                            final Type postListType = new TypeToken<List<Post>>(){}.getType();
                            mPosts = GsonUtils.getInstance().fromJson(content, postListType);
                            if (!mPosts.isEmpty()) {
                                final PostsAdapter adapter = new PostsAdapter(mPosts, true);
                                mPostListView.setLayoutManager(new LinearLayoutManager(mContext));
                                mPostListView.setAdapter(adapter);
                                mPostListView.setVisibility(View.VISIBLE);
                                mEmptyLayout.setVisibility(View.GONE);
                            }
                            break;
                        case Constants.INTENT_POST_LIKE:
                            if (Integer.valueOf(code) == Constants.HTTP_NO_CONTENT)
                                mContext.sendBroadcast(new Intent(Constants.EXTRA_REFRESH_PAGES));
                            else {
                                Reward reward = GsonUtils.getInstance().fromJson(
                                        content,
                                        Reward.class
                                );
                                Utils.generateRewardDialog(
                                        mContext,
                                        getFragmentManager(),
                                        PostsFragment.this,
                                        reward,
                                        0
                                );
                            }
                            break;
                        case Constants.INTENT_POST_DISLIKE:
                            mContext.sendBroadcast(new Intent(Constants.EXTRA_REFRESH_PAGES));
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

    public static PostsFragment newInstance() {
        return new PostsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_posts, container, false);
        mContext = view.getContext();

        mEmptyLayout = view.findViewById(R.id.layout_empty);
        mPostListView = (RecyclerView) view.findViewById(android.R.id.list);

        mEmptyLayout.setVisibility(View.VISIBLE);
        mPostListView.setVisibility(View.GONE);

        if (savedInstanceState != null)
            mPosts = savedInstanceState.getParcelableArrayList(KEY_PAGE_POSTS);
        else {
            final Bundle args = getArguments();
            if (args != null) {
                mPosts = args.getParcelableArrayList(KEY_PAGE_POSTS);
                if (mPosts != null && !mPosts.isEmpty()) {
                    final PostsAdapter adapter = new PostsAdapter(mPosts, true);
                    mPostListView.setLayoutManager(new LinearLayoutManager(mContext));
                    mPostListView.setAdapter(adapter);
                    mPostListView.setVisibility(View.VISIBLE);
                }
            } else this.getPosts();
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.INTENT_POSTS_LIST);
        filter.addAction(Constants.INTENT_POST_LIKE);
        filter.addAction(Constants.INTENT_POST_DISLIKE);
        mContext.registerReceiver(mReceiver, filter);
    }

    @Override
    public void onResume() {
        super.onResume();
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
        outState.putParcelableArrayList(KEY_PAGE_POSTS, (ArrayList<Post>) mPosts);
    }

    @Override
    public void onTextDialogDismissOk(Object o) {
        mContext.sendBroadcast(new Intent(Constants.EXTRA_REFRESH_PAGES));
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

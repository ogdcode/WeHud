package com.wehud.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;
import com.wehud.R;
import com.wehud.adapter.GamesAdapter;
import com.wehud.model.Game;
import com.wehud.model.Payload;
import com.wehud.network.APICall;
import com.wehud.util.Constants;
import com.wehud.util.GsonUtils;
import com.wehud.util.Utils;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GamesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private Context mContext;
    private View mEmptyLayout;
    private SwipeRefreshLayout mSwipeLayout;
    private RecyclerView mGameListView;

    private boolean mPaused;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.INTENT_GAMES_LIST) && !mPaused) {
                final String response = intent.getStringExtra(Constants.EXTRA_BROADCAST);
                final Payload payload = GsonUtils.getInstance().fromJson(response, Payload.class);

                final String code = payload.getCode();

                if (Integer.valueOf(code) == Constants.HTTP_OK) {
                    final String content = payload.getContent();

                    final Type gameListType = new TypeToken<List<Game>>(){}.getType();
                    final List<Game> games = GsonUtils.getInstance().fromJson(content, gameListType);

                    if (!games.isEmpty()) {
                        GamesAdapter adapter = new GamesAdapter(games);
                        adapter.setViewResourceId(1);
                        mGameListView.setAdapter(adapter);

                        mEmptyLayout.setVisibility(View.GONE);
                        mSwipeLayout.setVisibility(View.VISIBLE);
                        mSwipeLayout.setRefreshing(false);
                    }
                } else if (Integer.valueOf(code) == Constants.HTTP_INTERNAL_SERVER_ERROR)
                    Utils.toast(mContext, R.string.error_server);
                else Utils.toast(mContext, R.string.error_general, code);
            }
        }
    };

    public static GamesFragment newInstance() {
        return new GamesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_games, container, false);
        mContext = view.getContext();

        mEmptyLayout = view.findViewById(R.id.layout_empty);
        mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.layout_swipe);

        mGameListView = (RecyclerView) view.findViewById(android.R.id.list);
        mGameListView.setLayoutManager(new GridLayoutManager(mContext, 2));

        mEmptyLayout.setVisibility(View.VISIBLE);
        mSwipeLayout.setVisibility(View.GONE);

        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setRefreshing(true);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.INTENT_GAMES_LIST);
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
        if (!mPaused) this.getGames();

        mPaused = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContext.unregisterReceiver(mReceiver);
    }

    @Override
    public void onRefresh() {
        this.getGames();
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
}

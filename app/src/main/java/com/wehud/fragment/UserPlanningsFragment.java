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
import com.wehud.adapter.PlanningsAdapter;
import com.wehud.model.Planning;
import com.wehud.network.APICall;
import com.wehud.util.Constants;
import com.wehud.util.GsonUtils;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserPlanningsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String KEY_USER_ID = "key_user_id";
    private String mUserId;

    private Context mContext;
    private View mEmptyLayout;
    private SwipeRefreshLayout mSwipeLayout;
    private RecyclerView mPlanningListView;

    private List<Planning> mPlannings;

    private boolean mPaused;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String payload = intent.getStringExtra(Constants.EXTRA_BROADCAST);

            if (intent.getAction().equals(Constants.INTENT_PLANNINGS_LIST) && !mPaused) {
                Type planningListType = new TypeToken<List<Planning>>(){}.getType();
                mPlannings = GsonUtils.getInstance().fromJson(payload, planningListType);

                if (!mPlannings.isEmpty()) {
                    PlanningsAdapter adapter = new PlanningsAdapter(mPlannings);
                    mPlanningListView.setAdapter(adapter);

                    mEmptyLayout.setVisibility(View.GONE);
                    mSwipeLayout.setVisibility(View.VISIBLE);
                    mSwipeLayout.setRefreshing(false);
                }
            }
        }
    };

    public static Fragment newInstance(String userId) {
        Fragment fragment = new UserPlanningsFragment();
        Bundle args = new Bundle();
        args.putString(KEY_USER_ID, userId);
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_events, container, false);
        mContext = view.getContext();

        mEmptyLayout = view.findViewById(R.id.layout_empty);
        mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.layout_swipe);
        mPlanningListView = (RecyclerView) view.findViewById(android.R.id.list);

        mEmptyLayout.setVisibility(View.VISIBLE);
        mSwipeLayout.setVisibility(View.GONE);

        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setRefreshing(true);

        mPlanningListView.setLayoutManager(new LinearLayoutManager(mContext));
        mPlanningListView.addItemDecoration(new DividerItemDecoration(mContext,
                DividerItemDecoration.HORIZONTAL));

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) mUserId = savedInstanceState.getString(KEY_USER_ID);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.INTENT_PLANNINGS_LIST);

        mContext.registerReceiver(mReceiver, filter);
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle args = getArguments();
        if (args != null) {
            mUserId = args.getString(KEY_USER_ID);
            if (!TextUtils.isEmpty(mUserId) && !mPaused) this.getPlannings();
        }

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
    public void onRefresh() {
        if (!TextUtils.isEmpty(mUserId)) this.getPlannings();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_USER_ID, mUserId);
    }

    private void getPlannings() {
        if (!TextUtils.isEmpty(mUserId)) {
            Map<String, String> headers = new HashMap<>();
            headers.put(Constants.HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
            headers.put(Constants.HEADER_ACCEPT, Constants.APPLICATION_JSON);

            APICall call = new APICall(
                    mContext,
                    Constants.INTENT_PLANNINGS_LIST,
                    Constants.GET,
                    Constants.API_USERS_PLANNINGS + '/' + mUserId,
                    headers
            );
            if (!call.isLoading()) call.execute();
        }
    }
}

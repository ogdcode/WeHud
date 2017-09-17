package com.wehud.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.gson.reflect.TypeToken;
import com.wehud.R;
import com.wehud.adapter.PlanningsAdapter;
import com.wehud.dialog.EditDialogFragment;
import com.wehud.model.Payload;
import com.wehud.model.Planning;
import com.wehud.network.APICall;
import com.wehud.util.Constants;
import com.wehud.util.GsonUtils;
import com.wehud.util.PreferencesUtils;
import com.wehud.util.Utils;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserPlanningsFragment extends Fragment
        implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener,
        EditDialogFragment.OnEditDialogDismissOkListener {

    private static final String PARAM_TITLE = "title";

    private Context mContext;
    private View mEmptyLayout;
    private SwipeRefreshLayout mSwipeLayout;
    private RecyclerView mPlanningListView;

    private String mCurrentUserId;
    private FragmentManager mManager;

    private boolean mPaused;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!mPaused) {
                final String response = intent.getStringExtra(Constants.EXTRA_BROADCAST);
                final Payload payload = GsonUtils.getInstance().fromJson(response, Payload.class);

                final String code = payload.getCode();

                if (Integer.valueOf(code) == Constants.HTTP_OK ||
                        Integer.valueOf(code) == Constants.HTTP_CREATED ||
                        Integer.valueOf(code) == Constants.HTTP_NO_CONTENT) {
                    final String content = payload.getContent();

                    switch (intent.getAction()) {
                        case Constants.INTENT_PLANNINGS_ADD:
                            Utils.toast(mContext, R.string.message_addPlanningSuccess);
                            getPlannings();
                            break;
                        case Constants.INTENT_PLANNINGS_DELETE:
                            Utils.toast(mContext, R.string.message_deletePlanningSuccess);
                            getPlannings();
                            break;
                        case Constants.INTENT_PLANNINGS_LIST:
                            final Type planningListType = new TypeToken<List<Planning>>(){}
                                    .getType();
                            final List<Planning> plannings = GsonUtils.getInstance().fromJson(
                                    content, planningListType
                            );

                            if (!plannings.isEmpty()) {
                                PlanningsAdapter adapter = new PlanningsAdapter(plannings);
                                adapter.setFragmentManager(mManager);
                                mPlanningListView.setAdapter(adapter);

                                mEmptyLayout.setVisibility(View.GONE);
                                mSwipeLayout.setVisibility(View.VISIBLE);
                            } else {
                                mEmptyLayout.setVisibility(View.VISIBLE);
                                mSwipeLayout.setVisibility(View.GONE);
                            }

                            mSwipeLayout.setRefreshing(false);
                            break;
                        default:
                            break;
                    }
                } else if (Integer.valueOf(code) == Constants.HTTP_INTERNAL_SERVER_ERROR)
                    Utils.toast(mContext, R.string.error_server);
                else Utils.toast(mContext, R.string.error_general, code);
            }
        }
    };

    public static Fragment newInstance(String userId) {
        Fragment fragment = new UserPlanningsFragment();
        Bundle args = new Bundle();
        args.putString(Constants.PREF_USER_ID, userId);
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_user_plannings, container, false);
        mContext = view.getContext();
        mManager = getFragmentManager();

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

        Button createFirstPlanningButton = (Button) view.findViewById(R.id.btnCreateFirstPlanning);
        createFirstPlanningButton.setOnClickListener(this);

        final boolean isConnectedUser = Utils.isConnectedUser(
                mContext,
                PreferencesUtils.get(mContext, Constants.PREF_USER_ID)
        );
        if (!isConnectedUser) createFirstPlanningButton.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null)
            mCurrentUserId = savedInstanceState.getString(Constants.PREF_USER_ID);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.INTENT_PLANNINGS_LIST);
        filter.addAction(Constants.INTENT_PLANNINGS_ADD);
        filter.addAction(Constants.INTENT_PLANNINGS_DELETE);

        mContext.registerReceiver(mReceiver, filter);
    }

    @Override
    public void onResume() {
        super.onResume();
        final Bundle args = getArguments();
        if (args != null) {
            mCurrentUserId = args.getString(Constants.PREF_USER_ID);
            if (!TextUtils.isEmpty(mCurrentUserId) && !mPaused) this.getPlannings();
            else mSwipeLayout.setRefreshing(false);
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.PREF_USER_ID, mCurrentUserId);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCreateFirstPlanning:
                EditDialogFragment.generate(
                        mManager,
                        this,
                        0,
                        getString(R.string.dialogTitle_createPlanning),
                        null,
                        getString(R.string.hint_planningTitle),
                        false
                );
                break;
            default:
                break;
        }
    }

    @Override
    public void onRefresh() {
        if (!TextUtils.isEmpty(mCurrentUserId)) this.getPlannings();
    }

    @Override
    public void onEditDialogDismissOk(Object id, String text) {
        if (!TextUtils.isEmpty(text)) this.createPlanning(text);
    }

    private void getPlannings() {
        if (!TextUtils.isEmpty(mCurrentUserId)) {
            Map<String, String> headers = new HashMap<>();
            headers.put(Constants.HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
            headers.put(Constants.HEADER_ACCEPT, Constants.APPLICATION_JSON);

            final APICall call = new APICall(
                    mContext,
                    Constants.INTENT_PLANNINGS_LIST,
                    Constants.GET,
                    Constants.API_USERS_PLANNINGS + '/' + mCurrentUserId,
                    headers
            );
            if (!call.isLoading()) call.execute();
        }
    }

    private void createPlanning(String title) {
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
        headers.put(Constants.HEADER_ACCEPT, Constants.APPLICATION_JSON);

        Map<String, String> parameters = new HashMap<>();
        parameters.put(Constants.PARAM_TOKEN, PreferencesUtils.get(mContext, Constants.PREF_TOKEN));

        Map<String, String> planning = new HashMap<>();
        planning.put(PARAM_TITLE, title);

        final String body = GsonUtils.getInstance().toJson(planning);

        final APICall call = new APICall(
                mContext,
                Constants.INTENT_PLANNINGS_ADD,
                Constants.POST,
                Constants.API_PLANNINGS,
                body,
                headers,
                parameters
        );
        if (!call.isLoading()) call.execute();
    }
}

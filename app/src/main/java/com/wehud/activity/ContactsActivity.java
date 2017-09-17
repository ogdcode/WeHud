package com.wehud.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.wehud.R;
import com.wehud.adapter.UsersAdapter;
import com.wehud.model.Payload;
import com.wehud.model.User;
import com.wehud.network.APICall;
import com.wehud.util.Constants;
import com.wehud.util.GsonUtils;
import com.wehud.util.PreferencesUtils;
import com.wehud.util.Utils;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactsActivity extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener {

    private View mEmptyLayout;
    private SwipeRefreshLayout mSwipeLayout;
    private RecyclerView mContactListView;

    private boolean mPaused;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.INTENT_FOLLOWERS_LIST) && !mPaused) {
                final String response = intent.getStringExtra(Constants.EXTRA_BROADCAST);
                final Payload payload = GsonUtils.getInstance().fromJson(response, Payload.class);

                final String code = payload.getCode();

                if (Integer.valueOf(code) == Constants.HTTP_OK) {
                    final String content = payload.getContent();
                    final User connectedUser = GsonUtils.getInstance().fromJson(content, User.class);

                    final List<User> followers = connectedUser.getFollowers();

                    if (!followers.isEmpty()) {
                        final UsersAdapter adapter = new UsersAdapter(followers);
                        adapter.setViewResourceId(1);
                        mContactListView.setAdapter(adapter);

                        mEmptyLayout.setVisibility(View.GONE);
                        mSwipeLayout.setVisibility(View.VISIBLE);
                        mSwipeLayout.setRefreshing(false);
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
                            Utils.toast(ContactsActivity.this, R.string.error_general, code);
                            return;
                    }

                    Utils.toast(ContactsActivity.this, messageId);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        if (ab != null)
            ab.setDisplayHomeAsUpEnabled(true);

        mEmptyLayout = findViewById(R.id.layout_empty);
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.layout_swipe);
        mContactListView = (RecyclerView) findViewById(android.R.id.list);

        mEmptyLayout.setVisibility(View.VISIBLE);
        mSwipeLayout.setVisibility(View.GONE);

        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setRefreshing(true);

        mContactListView.setLayoutManager(new LinearLayoutManager(this));
        mContactListView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.HORIZONTAL));

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.INTENT_FOLLOWERS_LIST);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mPaused) this.getFollowers();

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public void onRefresh() {
        this.getFollowers();
    }

    private void getFollowers() {
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
        headers.put(Constants.HEADER_ACCEPT, Constants.APPLICATION_JSON);

        final String connectedUserId = PreferencesUtils.get(this, Constants.PREF_USER_ID);

        final APICall call = new APICall(
                this,
                Constants.INTENT_FOLLOWERS_LIST,
                Constants.GET,
                Constants.API_USERS + '/' + connectedUserId,
                headers
        );
        if (!call.isLoading()) call.execute();
    }
}

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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.wehud.R;
import com.wehud.adapter.UsersAdapter;
import com.wehud.model.User;
import com.wehud.network.APICall;
import com.wehud.util.Constants;
import com.wehud.util.GsonUtils;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private View mEmptyLayout;
    private SwipeRefreshLayout mSwipeLayout;
    private RecyclerView mContactListView;

    private List<User> mFollowers;

    private boolean mPaused;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String payload = intent.getStringExtra(Constants.EXTRA_API_RESPONSE);

            if (intent.getAction().equals(Constants.INTENT_FOLLOWERS_LIST) && !mPaused) {
                Type userListType = new TypeToken<List<User>>(){}.getType();
                mFollowers = GsonUtils.getInstance().fromJson(payload, userListType);

                if (!mFollowers.isEmpty()) {
                    UsersAdapter adapter = new UsersAdapter(mFollowers);
                    adapter.setViewResourceId(1);
                    mContactListView.setAdapter(adapter);

                    mEmptyLayout.setVisibility(View.GONE);
                    mSwipeLayout.setVisibility(View.VISIBLE);
                    mSwipeLayout.setRefreshing(false);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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

        APICall call = new APICall(
                this,
                Constants.INTENT_FOLLOWERS_LIST,
                Constants.GET,
                Constants.API_USERS + "/all",
                headers
        );
        if (!call.isLoading()) call.execute();
    }
}

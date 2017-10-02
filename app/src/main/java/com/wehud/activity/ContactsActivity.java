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

import com.wehud.R;
import com.wehud.adapter.UsersAdapter;
import com.wehud.model.Payload;
import com.wehud.model.User;
import com.wehud.network.APICall;
import com.wehud.util.Constants;
import com.wehud.util.GsonUtils;
import com.wehud.util.PreferencesUtils;
import com.wehud.util.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This {@link AppCompatActivity} subclass displays a list of
 * the connected user's followers, and indicates whether they
 * are currently connected to the application or not.
 *
 * @author Olivier Gonçalves, 2017.
 */
public class ContactsActivity extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener {

    /**
     * A layout shown when the connected user has no followers.
     */
    private View mEmptyLayout;

    /**
     * A layout containing the list of followers and enabling swipe-to-refresh functionality.
     */
    private SwipeRefreshLayout mSwipeLayout;

    /**
     * A layout containing a list of {@link User} that follow the connected user.
     */
    private RecyclerView mContactListView;

    /**
     * This is used to control the state of this Activity's {@link BroadcastReceiver}
     * instance, so as to prevent it from listening to {@link Intent}s non-stop.
     */
    private boolean mPaused;

    /**
     * This object receives {@link Intent}s sent from requests made to the API.
     * Different lines of code are executed depending on the Intent's content.
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(Constants.INTENT_FOLLOWERS_LIST) && !mPaused) {
                // Response contains the info of a User. Get the appropriate JSON
                // and retrieve the followers of the User using the User class and GsonUtils.

                final String response = intent.getStringExtra(Constants.EXTRA_BROADCAST);
                final Payload payload = GsonUtils.getInstance().fromJson(response, Payload.class);

                final String code = payload.getCode();

                if (Integer.valueOf(code) == Constants.HTTP_OK) {
                    final String content = payload.getContent();
                    final User connectedUser = GsonUtils.getInstance().fromJson(content, User.class);

                    final List<User> followers = connectedUser.getFollowers();

                    if (Utils.isNotEmpty(followers)) {
                        final UsersAdapter adapter = new UsersAdapter(followers);

                        // Forces the use of a particular ViewHolder in UsersAdapter.
                        adapter.setViewResourceId(1);

                        mContactListView.setAdapter(adapter);

                        mEmptyLayout.setVisibility(View.GONE);
                        mSwipeLayout.setVisibility(View.VISIBLE);

                        // Manually set it in case no swipe was detected prior to refreshing.
                        mSwipeLayout.setRefreshing(false);
                    }
                } else {
                    int messageId;

                    // If token expires, code is 403, and user is thrown back at login screen.
                    switch (Integer.valueOf(code)) {
                        case Constants.HTTP_UNAUTHORIZED:
                            messageId = R.string.error_sessionExpired;
                            startActivity(new Intent(ContactsActivity.this, LoginActivity.class));
                            PreferencesUtils.clear(ContactsActivity.this);
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

        // Provides up navigation.
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

        // Forces refresh icon to appear, bacause when in OnCreate
        // we do not have the connected user's info.
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

        // Calling this.getFollowers() since setting refreshing to true is not enough.
        // However, only when not paused, which means the first time this Activity is accessed to.
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

    /**
     * Calls the API to retrieve a {@link User} object containing a {@link List<User>}
     * that are their followers.
     */
    private void getFollowers() {
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
        headers.put(Constants.HEADER_ACCEPT, Constants.APPLICATION_JSON);

        final String connectedUserId = PreferencesUtils.get(this, Constants.PREF_USER_ID);

        final APICall call = new APICall(
                this,
                Constants.INTENT_FOLLOWERS_LIST,
                Constants.GET,
                Constants.API_USERS_USER + '/' + connectedUserId,
                headers
        );
        if (!call.isLoading()) call.execute();
    }
}

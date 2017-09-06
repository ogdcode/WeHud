package com.wehud.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.wehud.R;
import com.wehud.adapter.EventsAdapter;
import com.wehud.adapter.PlanningsAdapter;
import com.wehud.dialog.DatePickerDialogFragment;
import com.wehud.model.Event;
import com.wehud.model.Planning;
import com.wehud.network.APICall;
import com.wehud.util.Constants;
import com.wehud.util.GsonUtils;
import com.wehud.util.Utils;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventsActivity extends AppCompatActivity
        implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static final int ID_DIALOG_START_DATE = 0;
    private static final int ID_DIALOG_END_DATE = 1;

    private static final String PARAM_TITLE = "title";
    private static final String PARAM_DESCRIPTION = "description";
    private static final String PARAM_START_DATE = "startDate";
    private static final String PARAM_END_DATE = "endDate";
    private static final String PARAM_PLANNING = "planning";

    private static final String KEY_PLANNINGS = "key_plannings";
    private static final String KEY_USER_ID = "key_user_id";
    private String mUserId;

    private View mEmptyLayout;
    private SwipeRefreshLayout mSwipeLayout;
    private RecyclerView mEventListView;

    private List<Planning> mPlannings;
    private List<Event> mEvents;

    private boolean mPaused;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String payload = intent.getStringExtra(Constants.EXTRA_BROADCAST);

            if (intent.getAction().equals(Constants.INTENT_EVENTS_LIST) && !mPaused) {
                Type eventListType = new TypeToken<List<Event>>() {
                }.getType();
                mEvents = GsonUtils.getInstance().fromJson(payload, eventListType);

                if (!mEvents.isEmpty()) {
                    EventsAdapter adapter = new EventsAdapter(mEvents);
                    mEventListView.setAdapter(adapter);

                    mEmptyLayout.setVisibility(View.GONE);
                    mSwipeLayout.setVisibility(View.VISIBLE);
                    mSwipeLayout.setRefreshing(false);
                }
            }

            if (intent.getAction().equals(Constants.INTENT_EVENTS_ADD) && !mPaused) {
                mSwipeLayout.setRefreshing(true);
                Utils.toast(EventsActivity.this, getString(R.string.message_createEventSuccess));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        mEmptyLayout = findViewById(R.id.layout_empty);
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.layout_swipe);
        mEventListView = (RecyclerView) findViewById(android.R.id.list);

        mEmptyLayout.setVisibility(View.VISIBLE);
        mSwipeLayout.setVisibility(View.GONE);

        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setRefreshing(true);

        mEventListView.setLayoutManager(new LinearLayoutManager(this));
        mEventListView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL)
        );

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.INTENT_EVENTS_LIST);
        filter.addAction(Constants.INTENT_EVENTS_ADD);

        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mPlannings = bundle.getParcelableArrayList(KEY_PLANNINGS);
            mUserId = bundle.getString(KEY_USER_ID);
            if (!TextUtils.isEmpty(mUserId) && !mPaused) this.getEvents();
        }

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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_events, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_event_add:
                this.generateNewEventDialog();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    public void onClick(final View view) {
        final DatePickerDialogFragment.OnDatePickListener datePickerListener =
                new DatePickerDialogFragment.OnDatePickListener() {
                    @Override
                    public void onDatePick(final int id, int year, int month, int dayOfMonth) {
                        if (id == ID_DIALOG_START_DATE || id == ID_DIALOG_END_DATE) {
                            final Calendar cal = Calendar.getInstance();
                            cal.set(year, month, dayOfMonth);

                            final Date date = cal.getTime();
                            final DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT);
                            view.setTag(date.getTime());
                            ((TextView) view).setText(formatter.format(date));
                        }
                    }
                };

        switch (view.getId()) {
            case R.id.startDate:
                DatePickerDialogFragment.generate(
                        getSupportFragmentManager(),
                        datePickerListener,
                        getString(R.string.dialogTitle_chooseEventStartDate),
                        ID_DIALOG_START_DATE
                );
                break;
            case R.id.endDate:
                DatePickerDialogFragment.generate(
                        getSupportFragmentManager(),
                        datePickerListener,
                        getString(R.string.dialogTitle_chooseEventEndDate),
                        ID_DIALOG_END_DATE
                );
                break;
            default:
                break;
        }
    }

    @Override
    public void onRefresh() {
        if (!TextUtils.isEmpty(mUserId)) this.getEvents();
    }

    @SuppressLint("InflateParams")
    private void generateNewEventDialog() {
        final View headerView = LayoutInflater.from(this).inflate(R.layout.dialog_header, null);
        final View bodyView = LayoutInflater.from(this).inflate(R.layout.dialog_event, null);

        final TextView dialogTitleView = (TextView) headerView.findViewById(R.id.dialog_title);
        dialogTitleView.setText(R.string.dialogTitle_newEvent);

        final EditText titleView = (EditText) bodyView.findViewById(R.id.title);
        final EditText descriptionView = (EditText) bodyView.findViewById(R.id.description);
        final TextView startDateView = (TextView) bodyView.findViewById(R.id.startDate);
        final TextView endDateView = (TextView) bodyView.findViewById(R.id.endDate);
        final Spinner planningView = (Spinner) bodyView.findViewById(R.id.planning);

        final ArrayAdapter<Planning> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                mPlannings
        );
        planningView.setAdapter(adapter);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCustomTitle(headerView);
        builder.setView(bodyView);
        builder.setPositiveButton(getString(R.string.add), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                final TextView firstInvalidField = Utils.getFirstInvalidField(titleView, descriptionView);
                if (firstInvalidField != null)
                    firstInvalidField.setError(getString(R.string.error_fieldRequired));
                else {
                    final String title = titleView.getText().toString();
                    final String description = descriptionView.getText().toString();
                    final String startDate = (startDateView.getTag() == null ?
                            "" : startDateView.getText().toString()
                    );
                    final String endDate = (endDateView.getTag() == null ?
                            "" : endDateView.getText().toString()
                    );
                    final String planning = (planningView.getSelectedItem() == null ?
                            "" : planningView.getSelectedItem().toString()
                    );

                    addEvent(title, description, startDate, endDate, planning);
                    dialog.dismiss();
                }


            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    private void addEvent(String title, String description, String startDate,
                          String endDate, String planning
    ) {
        if (!TextUtils.isEmpty(mUserId)) {
            Map<String, String> headers = new HashMap<>();
            headers.put(Constants.HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
            headers.put(Constants.HEADER_ACCEPT, Constants.APPLICATION_JSON);

            Map<String, String> parameters = new HashMap<>();
            parameters.put(KEY_USER_ID, mUserId);
            parameters.put(Constants.PARAM_TOKEN, Constants.TOKEN);

            Map<String, String> event = new HashMap<>();
            event.put(PARAM_TITLE, title);
            event.put(PARAM_DESCRIPTION, description);
            if (!TextUtils.isEmpty(startDate)) event.put(PARAM_START_DATE, startDate);
            if (!TextUtils.isEmpty(endDate)) event.put(PARAM_END_DATE, endDate);
            if (!TextUtils.isEmpty(planning)) event.put(PARAM_PLANNING, planning);

            String body = GsonUtils.getInstance().toJson(event);

            APICall call = new APICall(
                    this,
                    Constants.INTENT_EVENTS_ADD,
                    Constants.POST,
                    Constants.API_EVENTS,
                    body,
                    headers,
                    parameters
            );
            if (!call.isLoading()) call.execute();
        }
    }

    private void getEvents() {
        if (!TextUtils.isEmpty(mUserId)) {
            Map<String, String> headers = new HashMap<>();
            headers.put(Constants.HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
            headers.put(Constants.HEADER_ACCEPT, Constants.APPLICATION_JSON);

            APICall call = new APICall(
                    this,
                    Constants.INTENT_EVENTS_LIST,
                    Constants.GET,
                    Constants.API_USERS_EVENTS + '/' + mUserId,
                    headers
            );
            if (!call.isLoading()) call.execute();
        }
    }
}

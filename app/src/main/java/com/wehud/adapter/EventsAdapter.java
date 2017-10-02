package com.wehud.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.wehud.R;
import com.wehud.dialog.ListDialogFragment;
import com.wehud.dialog.TextDialogFragment;
import com.wehud.model.Event;
import com.wehud.model.Planning;
import com.wehud.model.Tag;
import com.wehud.network.APICall;
import com.wehud.util.Constants;
import com.wehud.util.GsonUtils;
import com.wehud.util.PreferencesUtils;
import com.wehud.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventsVH> {

    private static final String PREFIX_DELETE = "delete";
    private static final String PREFIX_UNBIND = "unbind";

    private static final String PARAM_PLANNING = "planning";

    private List<Planning> mPlannings;
    private List<Event> mEvents;

    private FragmentManager mManager;

    public EventsAdapter(List<Event> events) {
        mEvents = events;
    }

    public void setPlannings(List<Planning> plannings) {
        mPlannings = plannings;
    }

    public void setFragmentManager(FragmentManager manager) {
        mManager = manager;
    }

    @Override
    public EventsVH onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event, parent, false);
        return new EventsVH(view);
    }

    @Override
    public void onBindViewHolder(final EventsVH holder, int position) {
        final Event event = mEvents.get(position);

        final String userId = event.getCreator().getId();
        final String title = event.getTitle();
        final String description = event.getDescription();
        final String startDate = event.getStartDateTimeString();
        final String endDate = event.getEndDateTimeString();
        final String planning = event.getPlanning();
        final Tag tag = Utils.getTag(event.getTag());

        event.setStartDateTime(Utils.isoDateTimeStringToTimestamp(startDate));
        event.setEndDateTime(Utils.isoDateTimeStringToTimestamp(endDate));

        holder.title.setText(title);
        holder.description.setText(description);
        holder.startDate.setText(Utils.timestampToLocalDateTimeString(event.getStartDateTime()));
        holder.endDate.setText(Utils.timestampToLocalDateTimeString(event.getEndDateTime()));
        holder.tag.setImageResource(tag.getIcon());

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextDialogFragment.generate(
                        mManager,
                        holder,
                        holder.context.getString(R.string.dialogTitle_deleteEvent),
                        holder.context.getString(R.string.dialogText_deleteEvent),
                        PREFIX_DELETE + '_' + event.getId()
                );
            }
        });

        if (TextUtils.isEmpty(planning))
            holder.bindUnbindButton.setText(holder.context.getString(R.string.btnBind));
        else holder.bindUnbindButton.setText(
                holder.context.getResources().getString(R.string.btnUnbind, planning)
        );

        holder.bindUnbindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.bindUnbindButton.getText().toString().equals(
                        holder.context.getString(R.string.btnBind)
                )) {
                    PlanningsAdapter planningsAdapter = new PlanningsAdapter(mPlannings);
                    planningsAdapter.setViewResourceId(0);
                    final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
                            holder.context
                    );
                    ListDialogFragment.generate(
                            mManager,
                            holder,
                            holder.context.getString(R.string.dialogTitle_choosePlanning),
                            (ArrayList<Planning>) mPlannings,
                            planningsAdapter,
                            layoutManager,
                            null,
                            event.getId()
                    );
                }

                if (holder.bindUnbindButton.getText().toString().contains(
                        holder.context.getString(R.string.btnUnbindContains)
                ))
                    TextDialogFragment.generate(
                            mManager,
                            holder,
                            holder.context.getString(R.string.dialogTitle_eventUnbind),
                            holder.context.getResources().getString(
                                    R.string.dialogText_eventUnbind,
                                    planning
                            ),
                            PREFIX_UNBIND + '_' + event.getId()
                    );
            }
        });

        if (!Utils.isNotEmpty(mPlannings)) holder.bindUnbindButton.setVisibility(View.GONE);
        else holder.bindUnbindButton.setVisibility(View.VISIBLE);

        if (!Utils.isConnectedUser(holder.context, userId))
            holder.deleteButton.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    static class EventsVH extends RecyclerView.ViewHolder
            implements View.OnClickListener, ListDialogFragment.OnListDialogDismissOkListener,
            TextDialogFragment.OnTextDialogDismissOkListener {
        private Context context;
        private TextView title;
        private TextView description;
        private TextView startDate;
        private TextView endDate;
        private ImageView tag;
        private ImageButton deleteButton;
        private ImageButton expandCollapseButton;
        private Button bindUnbindButton;
        private ViewGroup expandedLayout;

        EventsVH(View view) {
            super(view);
            context = view.getContext();

            final boolean isConnectedUser = Utils.isConnectedUser(
                    context,
                    PreferencesUtils.get(context, Constants.PREF_USER_ID)
            );

            title = (TextView) view.findViewById(R.id.title);
            description = (TextView) view.findViewById(R.id.description);
            startDate = (TextView) view.findViewById(R.id.startDateTime);
            endDate = (TextView) view.findViewById(R.id.endDateTime);
            tag = (ImageView) view.findViewById(R.id.tag);
            deleteButton = (ImageButton) view.findViewById(R.id.btnDelete);
            bindUnbindButton = (Button) view.findViewById(R.id.btnBindUnbind);

            expandCollapseButton = (ImageButton) view.findViewById(R.id.btnExpandCollapse);
            expandCollapseButton.setImageResource(R.drawable.ic_expand);
            expandCollapseButton.setOnClickListener(this);

            expandedLayout = (ViewGroup) view.findViewById(R.id.layout_expanded);
            expandedLayout.setVisibility(View.GONE);

            ViewGroup actionsLayout = (ViewGroup) view.findViewById(R.id.layout_actions);
            if (!isConnectedUser) actionsLayout.setVisibility(View.GONE);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.btnExpandCollapse) {
                if (expandedLayout.getVisibility() == View.GONE) {
                    Utils.expand(expandedLayout);
                    expandCollapseButton.setImageResource(R.drawable.ic_collapse);
                } else if (expandedLayout.getVisibility() == View.VISIBLE) {
                    Utils.collapse(expandedLayout);
                    expandCollapseButton.setImageResource(R.drawable.ic_expand);
                }
            }
        }

        @Override
        public void onListDialogDismissOk(Object id, Parcelable p) {
            if (p instanceof Planning) {
                Map<String, String> headers = new HashMap<>();
                headers.put(Constants.HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
                headers.put(Constants.HEADER_ACCEPT, Constants.APPLICATION_JSON);

                Map<String, String> parameters = new HashMap<>();
                parameters.put(Constants.PARAM_TOKEN, PreferencesUtils.get(
                        context,
                        Constants.PREF_TOKEN)
                );

                Map<String, String> planning = new HashMap<>();
                planning.put(PARAM_PLANNING, ((Planning) p).getTitle());

                String body = GsonUtils.getInstance().toJson(planning);

                final APICall call = new APICall(
                        context,
                        Constants.INTENT_EVENTS_BIND,
                        Constants.PATCH,
                        Constants.API_EVENT_BIND + '/' + id,
                        body,
                        headers,
                        parameters
                );
                if (!call.isLoading()) call.execute();

                bindUnbindButton.setText(context.getString(R.string.btnUnbindContains));
            }
        }

        @Override
        public void onTextDialogDismissOk(Object id) {
            if (!TextUtils.isEmpty(id.toString())) {
                Map<String, String> headers = new HashMap<>();
                headers.put(Constants.HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
                headers.put(Constants.HEADER_ACCEPT, Constants.APPLICATION_JSON);

                Map<String, String> parameters = new HashMap<>();
                parameters.put(Constants.PARAM_TOKEN, PreferencesUtils.get(
                        context,
                        Constants.PREF_TOKEN)
                );

                final String[] info = id.toString().split("_");
                final String prefix = info[0];
                final String eventId = info[1];

                String action = null, method = null, url = null;
                if (prefix.equals(PREFIX_DELETE)) {
                    action = Constants.INTENT_EVENTS_DELETE;
                    method = Constants.DELETE;
                    url = Constants.API_EVENTS + '/' + eventId;
                }
                if (prefix.equals(PREFIX_UNBIND)) {
                    action = Constants.INTENT_EVENTS_UNBIND;
                    method = Constants.PATCH;
                    url = Constants.API_EVENT_UNBIND + '/' + eventId;

                    bindUnbindButton.setText(context.getString(R.string.btnBind));
                }

                final APICall call = new APICall(
                        context,
                        action,
                        method,
                        url,
                        headers,
                        parameters
                );
                if (!call.isLoading()) call.execute();
            }
        }
    }
}

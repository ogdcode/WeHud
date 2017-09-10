package com.wehud.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.wehud.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventsVH> {

    private static final String KEY_USER_ID = "key_user_id";
    private static final String PREFIX_DELETE = "delete";
    private static final String PREFIX_UNBIND = "unbind";

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event, parent, false);
        return new EventsVH(view);
    }

    @Override
    public void onBindViewHolder(final EventsVH holder, int position) {
        final Event event = mEvents.get(position);

        String title = event.getTitle();
        String description = event.getDescription();
        String startDate = Utils.timestampToLocalDateString(event.getStartDateTime());
        String endDate = Utils.timestampToLocalDateString(event.getEndDateTime());
        Tag tag = Utils.getTag(event.getTag());

        holder.title.setText(title);
        holder.description.setText(description);
        holder.startDate.setText(startDate);
        holder.endDate.setText(endDate);
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

        holder.bindUnbindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.bindUnbindButton.getText().equals(
                        holder.context.getString(R.string.btnBind)
                )) {
                    PlanningsAdapter planningsAdapter = new PlanningsAdapter(mPlannings);
                    planningsAdapter.setViewResourceId(0);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(holder.context);
                    ListDialogFragment.generate(
                            mManager,
                            holder,
                            holder.context.getString(R.string.dialogTitle_choosePlanning),
                            (ArrayList<Planning>) mPlannings,
                            planningsAdapter,
                            layoutManager,
                            null
                    );
                }

                if (holder.bindUnbindButton.getText().equals(
                        holder.context.getString(R.string.btnUnbind)
                ))
                    TextDialogFragment.generate(
                            mManager,
                            holder,
                            holder.context.getString(R.string.dialogTitle_eventUnbind),
                            holder.context.getResources().getString(
                                    R.string.dialogText_eventUnbind,
                                    event.getPlanning()
                            ),
                            PREFIX_UNBIND + '_' + event.getId()
                    );
            }
        });

        if (mPlannings.isEmpty()) holder.bindUnbindButton.setVisibility(View.GONE);
        else holder.bindUnbindButton.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    static class EventsVH extends RecyclerView.ViewHolder
            implements ListDialogFragment.OnListDialogDismissOkListener,
            TextDialogFragment.OnTextDialogDismissOkListener {
        private Context context;
        private TextView title;
        private TextView description;
        private TextView startDate;
        private TextView endDate;
        private ImageView tag;
        private ImageButton deleteButton;
        private Button bindUnbindButton;


        EventsVH(View view) {
            super(view);
            context = view.getContext();
            title = (TextView) view.findViewById(R.id.title);
            description = (TextView) view.findViewById(R.id.description);
            startDate = (TextView) view.findViewById(R.id.startDateTime);
            endDate = (TextView) view.findViewById(R.id.endDateTime);
            tag = (ImageView) view.findViewById(R.id.tag);
            deleteButton = (ImageButton) view.findViewById(R.id.btnDelete);
            bindUnbindButton = (Button) view.findViewById(R.id.btnBindUnbind);
        }

        @Override
        public void onListDialogDismissOk(Parcelable p) {
            if (p instanceof Planning) {
                Map<String, String> headers = new HashMap<>();
                headers.put(Constants.HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
                headers.put(Constants.HEADER_ACCEPT, Constants.APPLICATION_JSON);

                APICall call = new APICall(
                        context,
                        Constants.INTENT_EVENTS_BIND,
                        Constants.PATCH,
                        Constants.API_EVENT_BIND + '/' + ((Planning) p).getId(),
                        headers
                );
                if (!call.isLoading()) call.execute();

                bindUnbindButton.setText(context.getString(R.string.btnUnbind));
            }
        }

        @Override
        public void onTextDialogDismissOk(Object id) {
            Map<String, String> headers = new HashMap<>();
            headers.put(Constants.HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
            headers.put(Constants.HEADER_ACCEPT, Constants.APPLICATION_JSON);

            String[] info = (((String) id).split("_"));
            String prefix = info[0];
            String eventId = info[1];

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

            APICall call = new APICall(
                    context,
                    action,
                    method,
                    url,
                    headers
            );
            if (!call.isLoading()) call.execute();
        }
    }
}

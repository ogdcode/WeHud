package com.wehud.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.wehud.R;
import com.wehud.activity.EventsActivity;
import com.wehud.dialog.TextDialogFragment;
import com.wehud.model.Event;
import com.wehud.model.Planning;
import com.wehud.network.APICall;
import com.wehud.util.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PlanningsAdapter extends RecyclerView.Adapter<PlanningsAdapter.PlanningsVH> {

    private static final String KEY_USER_ID = "key_user_id";

    private List<Planning> mPlannings;

    private FragmentManager mManager;

    public PlanningsAdapter(List<Planning> plannings) {
        mPlannings = plannings;
    }

    public void setFragmentManager(FragmentManager manager) {
        mManager = manager;
    }

    @Override
    public PlanningsVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.planning, parent, false
        );
        return new PlanningsVH(view);
    }

    @Override
    public void onBindViewHolder(final PlanningsVH holder, int position) {
        final Planning planning = mPlannings.get(position);
        final List<Event> events = planning.getEvents();

        String title = planning.getTitle();
        int numEvents = events.size();

        holder.title.setText(title);
        holder.numEvents.setText(numEvents + "\t" + holder.context.getString(R.string.numEvents));

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               TextDialogFragment.generate(
                       mManager,
                       holder,
                       holder.context.getString(R.string.dialogTitle_deletePlanning),
                       holder.context.getString(R.string.message_deletePlanning),
                       planning.getId()
               );
            }
        });

        holder.unbindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unbindPlanning(holder.context, planning);
            }
        });

        if (numEvents == 0) holder.unbindButton.setVisibility(View.GONE);
        else holder.unbindButton.setVisibility(View.VISIBLE);

        final String userId = planning.getCreator().getId();
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.context, EventsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(KEY_USER_ID, userId);
                intent.putExtras(bundle);
                holder.context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPlannings.size();
    }

    private void unbindPlanning(Context context, Planning planning) {
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
        headers.put(Constants.HEADER_ACCEPT, Constants.APPLICATION_JSON);

        String planningId = planning.getId();

        APICall call = new APICall(
                context,
                Constants.INTENT_PLANNINGS_UNBIND,
                Constants.PATCH,
                Constants.API_PLANNINGS + '/' + planningId,
                headers
        );
        if (!call.isLoading()) call.execute();
    }

    static class PlanningsVH extends RecyclerView.ViewHolder
            implements TextDialogFragment.OnTextDialogDismissOkListener {
        private View view;
        private Context context;
        private TextView title;
        private ImageButton deleteButton;
        private TextView numEvents;
        private Button unbindButton;

        PlanningsVH(View itemView) {
            super(itemView);
            view = itemView;
            context = view.getContext();
            title = (TextView) view.findViewById(R.id.title);
            deleteButton = (ImageButton) view.findViewById(R.id.btnDelete);
            numEvents = (TextView) view.findViewById(R.id.numEvents);
            unbindButton = (Button) view.findViewById(R.id.btnUnbind);
        }

        @Override
        public void onTextDialogDismissOk(Object id) {
            Map<String, String> headers = new HashMap<>();
            headers.put(Constants.HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
            headers.put(Constants.HEADER_ACCEPT, Constants.APPLICATION_JSON);

            APICall call = new APICall(
                    context,
                    Constants.INTENT_PLANNINGS_DELETE,
                    Constants.DELETE,
                    Constants.API_PLANNINGS + '/' + id,
                    headers
            );
            if (!call.isLoading()) call.execute();
        }
    }
}

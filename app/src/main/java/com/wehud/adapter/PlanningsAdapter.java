package com.wehud.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PlanningsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String KEY_USER_ID = "key_user_id";
    private static final String KEY_PLANNINGS = "key_plannings";
    private static final String KEY_EVENTS = "key_events";

    private List<Planning> mPlannings;

    private FragmentManager mManager;

    private View mSelectedView;
    private static int mSelectedPosition = -1;

    private int mViewResourceId;

    public PlanningsAdapter(List<Planning> plannings) {
        mPlannings = plannings;
        setHasStableIds(true);
    }

    public void setFragmentManager(FragmentManager manager) {
        mManager = manager;
    }

    public void setViewResourceId(int viewResourceId) {
        mViewResourceId = viewResourceId;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (mViewResourceId == 0) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new PlanningTitlesVH(view);
        }

        if (mViewResourceId == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.planning, parent, false
            );
            return new PlanningsVH(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final Planning planning = mPlannings.get(position);
        String title = planning.getTitle();

        if (holder instanceof PlanningTitlesVH) {
            final PlanningTitlesVH titlesHolder = (PlanningTitlesVH) holder;

            if (position == mSelectedPosition) {
                holder.itemView.setSelected(true);
                mSelectedView = holder.itemView;
            } else holder.itemView.setSelected(false);

            titlesHolder.title.setText(title);
        }

        if (holder instanceof PlanningsVH) {
            final PlanningsVH planningsHolder = (PlanningsVH) holder;
            final List<Event> events = planning.getEvents();

            int numEvents = events.size();

            planningsHolder.title.setText(title);
            planningsHolder.numEvents.setText(numEvents + "\t" +
                    planningsHolder.context.getString(R.string.numEvents));

            planningsHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextDialogFragment.generate(
                            mManager,
                            planningsHolder,
                            planningsHolder.context.getString(R.string.dialogTitle_deletePlanning),
                            planningsHolder.context.getString(R.string.message_deletePlanning),
                            planning.getId()
                    );
                }
            });

            planningsHolder.unbindButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    unbindPlanning(planningsHolder.context, planning);
                }
            });

            if (numEvents == 0) planningsHolder.unbindButton.setVisibility(View.GONE);
            else planningsHolder.unbindButton.setVisibility(View.VISIBLE);

            final String userId = planning.getCreator().getId();
            planningsHolder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(planningsHolder.context, EventsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(KEY_USER_ID, userId);
                    bundle.putParcelableArrayList(KEY_PLANNINGS, (ArrayList<Planning>) mPlannings);
                    bundle.putParcelableArrayList(
                            KEY_EVENTS, (ArrayList<Event>) planning.getEvents()
                    );
                    intent.putExtras(bundle);
                    planningsHolder.context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mPlannings.size();
    }

    @Override
    public long getItemId(int position) {
        if (position == -1) return mSelectedPosition;
        return super.getItemId(position);
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

    private class PlanningTitlesVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView title;

        PlanningTitlesVH(View view) {
            super(view);
            view.setClickable(true);
            view.setOnClickListener(this);
            title = (TextView) view.findViewById(android.R.id.text1);
            title.setBackgroundResource(R.drawable.list_item_selector);
        }

        @Override
        public void onClick(View view) {
            if (!view.isSelected()) {
                if (mSelectedView != null)
                    mSelectedView.setSelected(false);

                mSelectedPosition = getAdapterPosition();
                mSelectedView = view;
            } else {
                mSelectedPosition = -1;
                mSelectedView = null;
            }

            view.setSelected(!view.isSelected());
        }
    }

    private class PlanningsVH extends RecyclerView.ViewHolder
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

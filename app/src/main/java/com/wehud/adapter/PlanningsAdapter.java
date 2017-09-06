package com.wehud.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wehud.R;
import com.wehud.activity.EventsActivity;
import com.wehud.model.Planning;

import java.util.ArrayList;
import java.util.List;

public final class PlanningsAdapter extends RecyclerView.Adapter<PlanningsAdapter.PlanningsVH> {

    private static final String KEY_USER_ID = "key_user_id";

    private List<Planning> mPlannings;

    public PlanningsAdapter(List<Planning> plannings) {
        mPlannings = plannings;
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
        Planning planning = mPlannings.get(position);

        String title = planning.getTitle();
        String numEvents = planning.getEvents().size()
                + ' ' + holder.context.getString(R.string.numEvents);

        holder.title.setText(title);
        holder.numEvents.setText(numEvents);

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

    static class PlanningsVH extends RecyclerView.ViewHolder {
        private View view;
        private Context context;
        private TextView title;
        private TextView numEvents;

        PlanningsVH(View itemView) {
            super(itemView);
            view = itemView;
            context = view.getContext();
            title = (TextView) view.findViewById(R.id.title);
            numEvents = (TextView) view.findViewById(R.id.numEvents);
        }
    }
}

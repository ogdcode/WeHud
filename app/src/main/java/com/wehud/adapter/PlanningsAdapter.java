package com.wehud.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wehud.R;
import com.wehud.model.Planning;

import java.util.List;

public final class PlanningsAdapter extends RecyclerView.Adapter<PlanningsAdapter.PlanningsVH> {

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
    public void onBindViewHolder(PlanningsVH holder, int position) {
        Planning planning = mPlannings.get(position);

        String title = planning.getTitle();
        String numEvents = planning.getEvents().size()
                + ' ' + holder.context.getString(R.string.numEvents);

        holder.title.setText(title);
        holder.numEvents.setText(numEvents);
    }

    @Override
    public int getItemCount() {
        return mPlannings.size();
    }

    static class PlanningsVH extends RecyclerView.ViewHolder {
        private Context context;
        private TextView title;
        private TextView numEvents;

        PlanningsVH(View view) {
            super(view);
            context = view.getContext();
            title = (TextView) view.findViewById(R.id.title);
            numEvents = (TextView) view.findViewById(R.id.numEvents);
        }
    }
}

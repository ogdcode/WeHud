package com.wehud.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wehud.R;
import com.wehud.model.Event;
import com.wehud.util.Utils;

import java.util.List;

public final class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventsVH> {

    private List<Event> mEvents;

    public EventsAdapter(List<Event> events) {
        mEvents = events;
    }

    @Override
    public EventsVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event, parent, false);
        return new EventsVH(view);
    }

    @Override
    public void onBindViewHolder(EventsVH holder, int position) {
        Event event = mEvents.get(position);

        String title = event.getTitle();
        String description = event.getDescription();
        String startDate = Utils.timestampToLocalDateString(event.getStartDate());
        String endDate = Utils.timestampToLocalDateString(event.getEndDate());

        holder.title.setText(title);
        holder.description.setText(description);
        holder.startDate.setText(startDate);
        holder.endDate.setText(endDate);
    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    static class EventsVH extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView description;
        private TextView startDate;
        private TextView endDate;
        private TextView tag;

        EventsVH(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            description = (TextView) view.findViewById(R.id.description);
            startDate = (TextView) view.findViewById(R.id.startDate);
            endDate = (TextView) view.findViewById(R.id.endDate);
            tag = (TextView) view.findViewById(R.id.tag);
        }
    }
}

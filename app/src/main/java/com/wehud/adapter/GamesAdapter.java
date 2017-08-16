package com.wehud.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wehud.R;
import com.wehud.model.Game;

import java.util.List;

public final class GamesAdapter extends RecyclerView.Adapter<GamesAdapter.GamesVH> {

    private List<Game> mGames;

    private View mSelectedView;
    private static int mSelectedPosition = -1;

    public GamesAdapter(List<Game> games) {
        mGames = games;
    }

    @Override
    public GamesVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new GamesVH(view);
    }

    @Override
    public void onBindViewHolder(GamesVH holder, int position) {
        if (position == mSelectedPosition) {
            holder.itemView.setSelected(true);
            mSelectedView = holder.itemView;
        } else holder.itemView.setSelected(false);

        Game game = mGames.get(position);
        String name = game.getName();
        holder.name.setText(name);
    }

    @Override
    public int getItemCount() {
        return mGames.size();
    }

    @Override
    public long getItemId(int position) {
        if (position == -1) return mSelectedPosition;
        return super.getItemId(position);
    }

    class GamesVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        private View itemView;
        private TextView name;

        GamesVH(View view) {
            super(view);
            view.setClickable(true);
            view.setOnClickListener(this);
            itemView = view;
            name = (TextView) view.findViewById(android.R.id.text1);
            name.setBackgroundResource(R.drawable.list_item_selector);
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
}

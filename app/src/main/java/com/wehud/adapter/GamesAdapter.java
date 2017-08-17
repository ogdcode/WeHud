package com.wehud.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wehud.R;
import com.wehud.model.Game;

import java.util.List;

public final class GamesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Game> mGames;

    private View mSelectedView;
    private static int mSelectedPosition = -1;

    private int mViewResourceId;

    public GamesAdapter(List<Game> games) {
        mGames = games;
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
            return new GameTextsVH(view);
        }

        if (mViewResourceId == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.game, parent, false);
            return new GameCoversVH(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Game game = mGames.get(position);

        if (holder instanceof GameTextsVH) {
            GameTextsVH textsHolder = (GameTextsVH) holder;
            if (position == mSelectedPosition) {
                textsHolder.itemView.setSelected(true);
                mSelectedView = textsHolder.itemView;
            } else textsHolder.itemView.setSelected(false);

            String name = game.getName();
            textsHolder.name.setText(name);
        }

        if (holder instanceof GameCoversVH) {
            GameCoversVH coversHolder = (GameCoversVH) holder;

            String cover = "https://" + game.getCover();
            if (!TextUtils.isEmpty(cover)) Picasso.with(coversHolder.context).load(cover).resize(512, 512).into(coversHolder.cover);
            else coversHolder.cover.setImageResource(R.mipmap.ic_launcher);
        }
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

    private class GameCoversVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Context context;
        private ImageView cover;

        GameCoversVH(View view) {
            super(view);
            context = view.getContext();
            cover = (ImageView) view.findViewById(R.id.game_cover);
            cover.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
    }

    private class GameTextsVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        private View itemView;
        private TextView name;

        GameTextsVH(View view) {
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

package com.wehud.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wehud.R;
import com.wehud.model.Page;

import java.util.List;

public final class PagesAdapter extends RecyclerView.Adapter<PagesAdapter.PagesVH> {

    private List<Page> mPages;

    private View mSelectedView;
    private static int mSelectedPosition = -1;

    public PagesAdapter(List<Page> games) {
        mPages = games;
    }

    @Override
    public PagesVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);

        return new PagesVH(view);
    }

    @Override
    public void onBindViewHolder(PagesVH holder, int position) {
        Page page = mPages.get(position);
        holder.title.setText(page.getTitle());
    }

    @Override
    public int getItemCount() {
        return mPages.size();
    }

    @Override
    public long getItemId(int position) {
        if (position == -1) return mSelectedPosition;
        return super.getItemId(position);
    }

    class PagesVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView title;

        PagesVH(View view) {
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
}

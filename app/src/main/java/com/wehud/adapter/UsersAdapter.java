package com.wehud.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wehud.R;
import com.wehud.model.User;

import java.util.List;

public final class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersVH> {

    private List<User> mUsers;
    private static int mSelectedPosition = -1;
    private static View mSelectedView;

    public UsersAdapter(List<User> users) {
        mUsers = users;
        setHasStableIds(true);
    }

    @Override
    public UsersVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new UsersVH(view);
    }

    @Override
    public void onBindViewHolder(UsersVH holder, int position) {
        if (position == mSelectedPosition) {
            holder.itemView.setSelected(true);
            mSelectedView = holder.itemView;
        } else holder.itemView.setSelected(false);

        User user = mUsers.get(position);

        String username = user.getUsername();

        holder.username.setText(username);
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    @Override
    public long getItemId(int position) {
        if (position == -1) return mSelectedPosition;
        return super.getItemId(position);
    }

    static class UsersVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        private View itemView;
        private TextView username;

        UsersVH(View view) {
            super(view);
            view.setClickable(true);
            view.setOnClickListener(this);
            itemView = view;
            username = (TextView) view.findViewById(android.R.id.text1);
            username.setBackgroundResource(R.drawable.list_item_selector);
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

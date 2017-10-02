package com.wehud.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wehud.R;
import com.wehud.activity.UserActivity;
import com.wehud.model.User;
import com.wehud.util.Constants;
import com.wehud.util.Utils;

import java.util.List;

/**
 * This {@link android.support.v7.widget.RecyclerView.Adapter} subclass
 * is used to handle displaying {@link User} objects using different holders.
 *
 * @author Olivier Gonçalves, 2017
 */
public final class UsersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<User> mUsers;

    private View mSelectedView;
    private static int mSelectedPosition = -1;

    private int mViewResourceId;

    public UsersAdapter(List<User> users) {
        mUsers = users;
        setHasStableIds(true);
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
            return new UsersVH(view);
        }

        if (mViewResourceId == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact, parent, false);
            return new ContactsVH(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final User user = mUsers.get(position);

        if (holder instanceof UsersVH) {
            final UsersVH usersHolder = (UsersVH) holder;
            if (position == mSelectedPosition) {
                holder.itemView.setSelected(true);
                mSelectedView = holder.itemView;
            } else holder.itemView.setSelected(false);

            String username = user.getUsername();
            usersHolder.username.setText(username);
        }

        if (holder instanceof ContactsVH) {
            final ContactsVH contactsHolder = (ContactsVH) holder;

            final String avatar = user.getAvatar();
            final String username = user.getUsername();
            final boolean connected = user.isConnected();

            if (!TextUtils.isEmpty(avatar))
                Utils.loadImage(contactsHolder.context, avatar, contactsHolder.avatar);
            else contactsHolder.avatar.setImageResource(R.mipmap.ic_launcher_round);

            contactsHolder.username.setText(username);
            contactsHolder.username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(contactsHolder.context, UserActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.PREF_USER_ID, user.getId());
                    intent.putExtras(bundle);
                    contactsHolder.context.startActivity(intent);
                }
            });

            if (connected) contactsHolder.status.setImageResource(R.drawable.ic_connected);
            else contactsHolder.status.setImageResource(R.drawable.ic_not_connected);
        }
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

    /**
     * This {@link android.support.v7.widget.RecyclerView.ViewHolder} subclass
     * is used to display just a {@link User}'s username.
     */
    private class UsersVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView username;

        UsersVH(View view) {
            super(view);
            view.setClickable(true);
            view.setOnClickListener(this);
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

    /**
     * This {@link android.support.v7.widget.RecyclerView.ViewHolder} subclass
     * is used to hold references to a {@link User} object. It is used in
     * {@link com.wehud.activity.ContactsActivity}.
     */
    private class ContactsVH extends RecyclerView.ViewHolder {
        private Context context;
        private ImageView avatar;
        private TextView username;
        private ImageView status;

        ContactsVH(View view) {
            super(view);
            context = view.getContext();
            avatar = (ImageView) view.findViewById(R.id.contact_avatar);
            username = (TextView) view.findViewById(R.id.contact_username);
            status = (ImageView) view.findViewById(R.id.contact_status);
        }
    }
}

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
import com.wehud.model.Post;
import com.wehud.model.User;

import java.util.List;

public final class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostsVH> {

    private List<Post> mPosts;

    public PostsAdapter(List<Post> posts) {
        mPosts = posts;
    }

    @Override
    public PostsVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post, parent, false);
        return new PostsVH(view);
    }

    @Override
    public void onBindViewHolder(PostsVH holder, int position) {
        Post post = mPosts.get(position);
        User publisher = post.getPublisher();

        String avatar = publisher.getAvatar();
        String username = publisher.getUsername();
        String datetimeCreated = post.getDatetimeCreated();
        String text = post.getText();
        String likes = String.valueOf(post.getLikes().size());

        if (TextUtils.isEmpty(avatar))
            holder.postAvatar.setImageResource(R.mipmap.ic_launcher_round);
        else
            Picasso.with(holder.context).load(avatar).into(holder.postAvatar);

        holder.postUsername.setText(username);
        holder.postCreatedAt.setText(datetimeCreated);
        holder.postText.setText(text);
        holder.postLikes.setText(likes);
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    static class PostsVH extends RecyclerView.ViewHolder {
        private Context context;
        private ImageView postAvatar;
        private TextView postUsername;
        private TextView postCreatedAt;
        private TextView postText;
        private ViewGroup mediaContent;
        private TextView postLikes;

        PostsVH(View view) {
            super(view);
            context = view.getContext();
            postAvatar = (ImageView) view.findViewById(R.id.post_avatar);
            postUsername = (TextView) view.findViewById(R.id.post_username);
            postCreatedAt = (TextView) view.findViewById(R.id.post_createdAt);
            postText = (TextView) view.findViewById(R.id.post_text);
            mediaContent = (ViewGroup) view.findViewById(R.id.content_media);
            postLikes = (TextView) view.findViewById(R.id.post_likes);
        }
    }
}

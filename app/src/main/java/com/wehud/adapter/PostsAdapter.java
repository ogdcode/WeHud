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
import android.widget.RatingBar;
import android.widget.TextView;

import com.wehud.R;
import com.wehud.activity.UserActivity;
import com.wehud.model.Game;
import com.wehud.model.Post;
import com.wehud.model.User;
import com.wehud.util.Utils;
import com.wehud.util.YouTubeUtils;

import java.util.List;

public final class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostsVH> {

    private static final String KEY_USER_ID = "key_user_id";

    private List<Post> mPosts;
    private boolean mItemsClickable;

    public PostsAdapter(List<Post> posts, boolean itemsClickable) {
        mPosts = posts;
        mItemsClickable = itemsClickable;
    }

    @Override
    public PostsVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post, parent, false);
        return new PostsVH(view);
    }

    @Override
    public void onBindViewHolder(final PostsVH holder, int position) {
        Post post = mPosts.get(position);
        final User publisher = post.getPublisher();

        String avatar = publisher.getAvatar();
        String username = publisher.getUsername();
        String datetimeCreated = post.getDatetimeCreated();
        String text = post.getText();
        String likes = String.valueOf(post.getLikes().size());

        if (TextUtils.isEmpty(avatar))
            holder.postAvatar.setImageResource(R.mipmap.ic_launcher_round);
        else Utils.loadImage(holder.context, avatar, holder.postAvatar);

        holder.postUsername.setText(username);
        holder.postCreatedAt.setText(datetimeCreated);
        holder.postText.setText(text);
        holder.postLikes.setText(likes);

        if (mItemsClickable)
            holder.postUsername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(holder.context, UserActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(KEY_USER_ID, publisher.getId());
                    intent.putExtras(bundle);
                    holder.context.startActivity(intent);
                }
            });

        this.setPostMedia(holder, post);
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    private void setPostMedia(final PostsVH holder, final Post post) {
        holder.postMedia.setVisibility(View.GONE);

        String videoUrl = post.getVideoUrl();

        if (post.isMessage() || post.isOpinion() || !TextUtils.isEmpty(videoUrl)) {
            TextView postReceiver = (TextView) holder.postMedia.findViewById(R.id.post_receiver);
            TextView postGame = (TextView) holder.postMedia.findViewById(R.id.post_game);
            RatingBar postGameRating = (RatingBar) holder.postMedia.findViewById(R.id.post_gameRating);
            ImageView postVideo = (ImageView) holder.postMedia.findViewById(R.id.post_video);

            postReceiver.setVisibility(View.GONE);
            postGame.setVisibility(View.GONE);
            postGameRating.setVisibility(View.GONE);
            postVideo.setVisibility(View.GONE);

            if (post.isMessage()) {
                User receiver = post.getReceiver();
                String receiverUsername = receiver.getUsername();

                postReceiver.setText(receiverUsername);

                postReceiver.setVisibility(View.VISIBLE);
            }

            if (post.isOpinion()) {
                Game game = post.getGame();
                String gameName = game.getName();
                double gameRating = post.getRating();

                postGame.setText(gameName);
                postGameRating.setRating(Double.valueOf(gameRating).floatValue());

                postGame.setVisibility(View.VISIBLE);
                postGameRating.setVisibility(View.VISIBLE);
            }

            if (!TextUtils.isEmpty(videoUrl)) {
                YouTubeUtils.configureVideo(holder.context, videoUrl, postVideo);

                postVideo.setVisibility(View.VISIBLE);
            }

            holder.postMedia.setVisibility(View.VISIBLE);
        }
    }

    static class PostsVH extends RecyclerView.ViewHolder {
        private Context context;
        private ImageView postAvatar;
        private TextView postUsername;
        private TextView postCreatedAt;
        private TextView postText;
        private ViewGroup postMedia;
        private TextView postLikes;

        PostsVH(View view) {
            super(view);
            context = view.getContext();
            postAvatar = (ImageView) view.findViewById(R.id.post_avatar);
            postUsername = (TextView) view.findViewById(R.id.post_username);
            postCreatedAt = (TextView) view.findViewById(R.id.post_datetimeCreated);
            postText = (TextView) view.findViewById(R.id.post_text);
            postMedia = (ViewGroup) view.findViewById(R.id.post_media);
            postLikes = (TextView) view.findViewById(R.id.post_likes);
        }
    }
}

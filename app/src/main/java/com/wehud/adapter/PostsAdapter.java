package com.wehud.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wehud.R;
import com.wehud.model.Game;
import com.wehud.model.Post;
import com.wehud.model.User;
import com.wehud.util.Utils;
import com.wehud.util.YouTubeUtils;

import java.util.List;

public final class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostsVH> {

    private List<Post> mPosts;
    private static int mSelectedItem = -1;

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
            Utils.loadImage(holder.context, avatar, holder.postAvatar);

        holder.postUsername.setText(username);
        holder.postCreatedAt.setText(datetimeCreated);
        holder.postText.setText(text);
        holder.postLikes.setText(likes);

        this.setPostMedia(holder, post);
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    @Override
    public long getItemId(int position) {
        if (position == -1) return mSelectedItem;
        return super.getItemId(position);
    }

    private void setPostMedia(final PostsVH holder, Post post) {
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

    static class PostsVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Context context;
        private ImageView postAvatar;
        private TextView postUsername;
        private TextView postCreatedAt;
        private TextView postText;
        private ViewGroup postMedia;
        private TextView postLikes;

        PostsVH(View view) {
            super(view);
            view.setOnClickListener(this);
            context = view.getContext();
            postAvatar = (ImageView) view.findViewById(R.id.post_avatar);
            postUsername = (TextView) view.findViewById(R.id.post_username);
            postCreatedAt = (TextView) view.findViewById(R.id.post_createdAt);
            postText = (TextView) view.findViewById(R.id.post_text);
            postMedia = (ViewGroup) view.findViewById(R.id.post_media);
            postLikes = (TextView) view.findViewById(R.id.post_likes);
        }

        @Override
        public void onClick(View view) {
            mSelectedItem = getAdapterPosition();
        }
    }
}

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
import com.wehud.network.APICall;
import com.wehud.util.Constants;
import com.wehud.util.PreferencesUtils;
import com.wehud.util.Utils;
import com.wehud.util.YouTubeUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostsVH> {

    private List<Post> mPosts;
    private boolean mItemsClickable;

    public PostsAdapter(List<Post> posts, boolean itemsClickable) {
        mPosts = posts;
        mItemsClickable = itemsClickable;
    }

    @Override
    public PostsVH onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post, parent, false);
        return new PostsVH(view);
    }

    @Override
    public void onBindViewHolder(final PostsVH holder, int position) {
        final Post post = mPosts.get(position);
        final User publisher = post.getPublisher();

        final String avatar = publisher.getAvatar();
        final String username = publisher.getUsername();
        final String datetimeCreated = post.getDatetimeCreated();
        final String text = post.getText();

        final List<String> userIds = post.getLikes();
        final String likes = String.valueOf(userIds.size());

        if (TextUtils.isEmpty(avatar))
            holder.postAvatar.setImageResource(R.mipmap.ic_launcher_round);
        else Utils.loadImage(holder.context, avatar, holder.postAvatar);

        holder.postUsername.setText(username);
        holder.postCreatedAt.setText(datetimeCreated);
        holder.postText.setText(text);

        if (mItemsClickable) {
            holder.postUsername.setClickable(true);
            holder.postUsername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Intent intent = new Intent(holder.context, UserActivity.class);
                    final Bundle bundle = new Bundle();
                    bundle.putString(Constants.PREF_USER_ID, publisher.getId());
                    intent.putExtras(bundle);
                    holder.context.startActivity(intent);
                }
            });

            final String connectedUserId = PreferencesUtils.get(
                    holder.context, Constants.PREF_USER_ID
            );
            final boolean connectedUserLiked = userIds.contains(connectedUserId);
            if (connectedUserLiked)
                holder.postLikes.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_liked, 0, 0, 0
                );
            else holder.postLikes.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_notliked, 0, 0, 0
            );
            holder.postLikes.setText(likes);
            holder.postLikes.setClickable(true);
            holder.postLikes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Map<String, String> headers = new HashMap<>();
                    headers.put(Constants.HEADER_CONTENT_TYPE, Constants.APPLICATION_JSON);
                    headers.put(Constants.HEADER_ACCEPT, Constants.APPLICATION_JSON);

                    Map<String, String> parameters = new HashMap<>();
                    parameters.put(
                            Constants.PARAM_TOKEN,
                            PreferencesUtils.get(
                                    holder.context,
                                    PreferencesUtils.get(holder.context, Constants.PREF_TOKEN)
                            )
                    );

                    final String postId = post.getId();
                    String action, url;
                    if (connectedUserLiked) {
                        action = Constants.INTENT_POST_DISLIKE;
                        url = Constants.API_DISLIKE + '/' + postId;
                    } else {
                        action = Constants.INTENT_POST_LIKE;
                        url = Constants.API_LIKE + '/' + postId;
                    }

                    final APICall call = new APICall(
                            holder.context,
                            action,
                            Constants.PATCH,
                            url,
                            headers,
                            parameters
                    );
                    if (!call.isLoading()) call.execute();
                }
            });

            holder.postFooter.setVisibility(View.VISIBLE);
        } else holder.postFooter.setVisibility(View.GONE);

        this.setPostMedia(holder, post);
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    private void setPostMedia(final PostsVH holder, final Post post) {
        holder.postMedia.setVisibility(View.GONE);

        final String videoUrl = post.getVideoUrl();

        if (post.isMessage() || post.isOpinion() || !TextUtils.isEmpty(videoUrl)) {
            final TextView postReceiver = (TextView) holder.postMedia.findViewById(R.id.post_receiver);
            final TextView postGame = (TextView) holder.postMedia.findViewById(R.id.post_game);
            final RatingBar postGameRating = (RatingBar) holder.postMedia.findViewById(R.id.post_gameRating);
            final ImageView postVideo = (ImageView) holder.postMedia.findViewById(R.id.post_video);

            postReceiver.setVisibility(View.GONE);
            postGame.setVisibility(View.GONE);
            postGameRating.setVisibility(View.GONE);
            postVideo.setVisibility(View.GONE);

            if (post.isMessage()) {
                final User receiver = post.getReceiver();
                final String receiverUsername = receiver.getUsername();

                postReceiver.setText(receiverUsername);
                postReceiver.setVisibility(View.VISIBLE);
            }

            if (post.isOpinion()) {
                final Game game = post.getGame();
                final String gameName = game.getName();
                final double gameRating = post.getRating();

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
        private TextView postLikes;
        private ViewGroup postFooter;
        private ViewGroup postMedia;

        PostsVH(View view) {
            super(view);
            context = view.getContext();
            postAvatar = (ImageView) view.findViewById(R.id.post_avatar);
            postUsername = (TextView) view.findViewById(R.id.post_username);
            postCreatedAt = (TextView) view.findViewById(R.id.post_datetimeCreated);
            postText = (TextView) view.findViewById(R.id.post_text);
            postLikes = (TextView) view.findViewById(R.id.post_likes);
            postFooter = (ViewGroup) view.findViewById(R.id.post_footer);
            postMedia = (ViewGroup) view.findViewById(R.id.post_media);
        }
    }
}

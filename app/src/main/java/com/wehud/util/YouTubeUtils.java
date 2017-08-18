package com.wehud.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.squareup.picasso.Picasso;

public final class YouTubeUtils {
    private static final String API_KEY = "AIzaSyBXI_qrIr0gRD0xuGfIj7Q03yRz0g2ReHk";

    private static final String VID_URL_HEAD = "https://youtube.com/watch?v=";
    private static final String IMG_URL_HEAD = "https://img.youtube.com/vi/";
    private static final String IMG_URL_FOOT = "/0.jpg";

    /**
     * Makes a full YouTube video URL.
     *
     * @param id the video ID of the YouTube video
     * @return the full YouTube URL of the video
     */
    private static String buildVideoUrl(String id) {
        return VID_URL_HEAD + id;
    }

    /**
     * Build up a YouTube thumbnail URL from a video ID.
     *
     * @param id the unique identifier of the YouTube video
     * @return the full URL of the thumbnail image
     */
    private static String buildThumbnailUrl(String id) {
        return IMG_URL_HEAD + id + IMG_URL_FOOT;
    }

    /**
     * Configures a YouTube video in the application.
     *
     * @param context the context of the application
     * @param id the ID of the YouTube video
     * @param iv an {@link ImageView} that will contain a thumbnail of the video
     */
    public static void configureVideo(final Context context, final String id, final ImageView iv) {
        final String thumbnailUrl = buildThumbnailUrl(id);
        Picasso.with(context).load(thumbnailUrl).resize(1280, 720).into(iv);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = YouTubeStandalonePlayer.createVideoIntent(
                        (Activity) context,
                        API_KEY,
                        id,
                        0,
                        true,
                        true
                );
                context.startActivity(intent);
            }
        });
    }
}

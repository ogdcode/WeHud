package com.wehud.util;

import android.content.Context;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class Utils {

    private static final String ISO_8601_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final String LOCAL_PATTERN = "dd/MM/yyyy HH:mm";

    private static final String YOUTUBE_VID_URL_HEAD = "https://youtube.com/watch?v=";

    /**
     * Displays a {@link Toast} on the screen.
     *
     * @param context the {@link Context} of the application
     * @param message the {@link String} to display as a message in the Toast.
     */
    public static void toast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static String isoDateStringToLocalDateString(String iso) {
        SimpleDateFormat sdf = new SimpleDateFormat(ISO_8601_PATTERN, Locale.getDefault());
        Date d = null;
        try {
            d = sdf.parse(iso);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sdf.applyPattern(LOCAL_PATTERN);

        return sdf.format(d);
    }

    /**
     * Helper method to call a Picasso static instance to load images and resize them.
     * @param context the {@link Context} of the application
     * @param imgUrl a {@link String} object representing the URL of the image
     * @param iv an {@link ImageView} in which to load the image
     * @param s a value in pixels used to resize the image
     */
    public static void loadImageWithResizing(Context context, String imgUrl, ImageView iv, int s) {
        Picasso.with(context).load(imgUrl).resize(s, s).into(iv);
    }

    /**
     * Helper method to call a Picasso static instance to load images.
     * @param context the {@link Context} of the application
     * @param imgUrl a {@link String} object representing the URL of the image
     * @param iv an {@link ImageView} in which to load the image
     */
    public static void loadImage(Context context, String imgUrl, ImageView iv) {
        Picasso.with(context).load(imgUrl).into(iv);
    }

    /**
     * Makes a full YouTube video URL.
     *
     * @param id the video ID of the YouTube video
     * @return the full YouTube URL of the video
     */
    public static String buildYouTubeVideoURL(String id) {
        return YOUTUBE_VID_URL_HEAD + id;
    }

}

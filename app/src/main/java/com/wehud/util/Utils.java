package com.wehud.util;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.wehud.R;
import com.wehud.model.Status;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public final class Utils {

    private static final String ISO_8601_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final String LOCAL_PATTERN_DATETIME = "dd/MM/yyyy HH:mm";
    private static final String LOCAL_PATTERN_DATE = "dd/MM/yyyy";

    /**
     * Displays a {@link Toast} on the screen.
     *
     * @param context the {@link Context} of the application
     * @param message the {@link String} to display as a message in the Toast.
     */
    public static void toast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static String isoDateTimeStringToLocalDateTimeString(String iso) {
        SimpleDateFormat sdf = new SimpleDateFormat(ISO_8601_PATTERN, Locale.getDefault());
        Date d = null;
        try {
            d = sdf.parse(iso);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sdf.applyPattern(LOCAL_PATTERN_DATETIME);

        return sdf.format(d);
    }

    public static String localDateTimeStringToIsoDateTimeString(String local) {
        SimpleDateFormat sdf = new SimpleDateFormat(LOCAL_PATTERN_DATETIME, Locale.getDefault());
        Date d = null;
        try {
            d = sdf.parse(local);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sdf.applyPattern(ISO_8601_PATTERN);

        return sdf.format(d);
    }

    public static String timestampToLocalDateString(long timestamp) {
        Timestamp tstp = new Timestamp(timestamp);
        DateFormat df = new SimpleDateFormat(LOCAL_PATTERN_DATE, Locale.getDefault());
        return df.format(new Date(tstp.getTime()));
    }

    /**
     * Helper method to call a Picasso static instance to load images and resize them.
     * @param context the {@link Context} of the application
     * @param imgUrl a {@link String} object representing the URL of the image
     * @param iv an {@link ImageView} in which to load the image
     * @param i a value in pixels used as the width of the image
     * @param i2 a value in pixels used as the height of the image
     */
    public static void loadImage(Context context, String imgUrl, ImageView iv, int i, int i2) {
        Picasso.with(context).load(imgUrl).resize(i, i2).into(iv);
    }

    /**
     * Helper method to call a Picasso static instance to load images and resize them.
     * @param context the {@link Context} of the application
     * @param imgUrl a {@link String} object representing the URL of the image
     * @param iv an {@link ImageView} in which to load the image
     * @param i a value in pixels used to resize the image
     */
    public static void loadImage(Context context, String imgUrl, ImageView iv, int i) {
        loadImage(context, imgUrl, iv, i, i);
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

    public static Status getStatus(Context context, int status) {
        StringBuilder sb = new StringBuilder();
        int resId;

        switch (status) {
            case 0:
                resId = R.drawable.ic_status_released;
                sb.append(context.getString(R.string.status_released));
                break;
            case 2:
                resId = R.drawable.ic_status_alpha;
                sb.append(context.getString(R.string.status_alpha));
                break;
            case 3:
                resId = R.drawable.ic_status_beta;
                sb.append(context.getString(R.string.status_beta));
                break;
            case 4:
                resId = R.drawable.ic_status_early_access;
                sb.append(context.getString(R.string.status_earlyAccess));
                break;
            case 5:
                resId = R.drawable.ic_status_offline;
                sb.append(context.getString(R.string.status_offline));
                break;
            case 6:
                resId = R.drawable.ic_status_cancelled;
                sb.append(context.getString(R.string.status_cancelled));
                break;
            default:
                resId = R.drawable.ic_status_unknown;
                sb.append(context.getString(R.string.status_unknown));
                break;
        }

        return new Status(resId, sb.toString());
    }

    public static TextView getFirstInvalidField(TextView... views) {
        TextView focusView = null;
        for (TextView view : views) {
            view.setError(null);
            if (!TextUtils.isEmpty(view.getText().toString())) {
                focusView = view;
                break;
            }
        }

        return focusView;
    }

    public static void putStringListIntoTextView(TextView txt, List<String> strings) {
        int len = strings.size();
        for (int i = 0; i < len; ++i) {
            if (i < len - 1) txt.append(strings.get(i) + ", ");
            else txt.append(strings.get(i));
        }
    }

    public static boolean isConnectedUser(Context context, String userId) {
        String connectedId = PreferencesUtils.getPreference(context, Constants.PREF_USER_ID);
        return connectedId.equals(userId);
    }
}

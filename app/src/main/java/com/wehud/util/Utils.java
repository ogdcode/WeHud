package com.wehud.util;

import android.content.Context;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class Utils {

    private static final String ISO_8601_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final String LOCAL_PATTERN = "dd/MM/yyyy HH:mm";

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

}

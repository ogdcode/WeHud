package com.wehud.util;

import android.text.format.DateUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * This singleton class is used to call a {@link com.google.gson.Gson} instance.
 *
 * @author Olivier Gonçalves, WeHud, 2017
 */

public final class GsonUtils {

    private static Gson mInstance;

    private GsonUtils() {}

    public static synchronized Gson getInstance() {
        if (mInstance == null)
            mInstance = new GsonBuilder().create();
        return mInstance;
    }

}

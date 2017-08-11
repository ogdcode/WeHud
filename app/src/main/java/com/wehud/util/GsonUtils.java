package com.wehud.util;

import com.google.gson.Gson;

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
            mInstance = new Gson();
        return mInstance;
    }

}

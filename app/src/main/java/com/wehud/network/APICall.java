package com.wehud.network;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.wehud.util.Constants;
import com.wehud.util.GsonUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to make requests to the API.
 *
 * @author Olivier Gonçalves, WeHud, 2017
 */

public final class APICall extends AsyncTask<Void, Void, Response> {
    private static final String PARAM_CODE = "code";
    private static final String PARAM_CONTENT = "content";

    private Context mContext;
    private String mAction;
    private String mMethod;
    private String mUrl;
    private String mBody;
    private Map<String, String> mHeaders;
    private Map<String, String> mParameters;

    private boolean mLoading;

    // This class should not be called without parameters.
    private APICall() {
    }

    public APICall(Context context, String action, String method, String url, String body,
                   Map<String, String> headers) {
        this(context, action, method, url, body, headers, null);
    }

    public APICall(Context context, String action, String method, String url,
                   Map<String, String> headers) {
        this(context, action, method, url, headers, null);
    }

    public APICall(Context context, String action, String method, String url,
                   Map<String, String> headers, Map<String, String> parameters) {
        this(context, action, method, url, null, headers, parameters);
    }

    public APICall(Context context, String action, String method, String url, String body,
                   Map<String, String> headers, Map<String, String> parameters) {
        mContext = context;
        mAction = action;
        mMethod = method;
        mUrl = url;
        mBody = body;
        mHeaders = headers;
        mParameters = parameters;
    }

    public boolean isLoading() {
        return mLoading;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mLoading = true;
    }

    @Override
    protected final Response doInBackground(Void... params) {
        Request request = new Request(mMethod, mUrl, mBody, mHeaders, mParameters);
        return request.send();
    }

    @Override
    protected void onPostExecute(Response response) {
        /*
        mLoading = false;
        String content = response.getContent();
        Intent intent = new Intent(mAction);
        intent.putExtra(Constants.EXTRA_BROADCAST, content);
        mContext.sendBroadcast(intent);
        */

        mLoading = false;
        String payload = this.buildPayload(response);
        Intent intent = new Intent(mAction);
        intent.putExtra(Constants.EXTRA_BROADCAST, payload);
        mContext.sendBroadcast(intent);
    }

    private String buildPayload(Response response) {
        Map<String, String> payload = new HashMap<>();
        payload.put(PARAM_CODE, String.valueOf(response.getCode()));
        payload.put(PARAM_CONTENT, response.getContent());

        return GsonUtils.getInstance().toJson(payload);
    }
}

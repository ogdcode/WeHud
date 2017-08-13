package com.wehud.network;

import android.text.TextUtils;
import android.util.Log;

import com.wehud.util.Constants;

import org.apache.commons.collections4.MapUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a request made to the API.
 *
 * @author Olivier Gonçalves, WeHud, 2017
 */

public final class Request {
    private Map<String, String> mHeaders;
    private Map<String, String> mParameters;
    private String mMethod;
    private String mUrl;
    private String mBody;

    // Cannot initialize a request without at least a method and URI.
    private Request() {
    }

    public Request(String method, String url) {
        this(method, url, null);
    }

    public Request(String method, String url, String body) {
        this(method, url, body, null);
    }

    public Request(String method, String url, String body, Map<String, String> headers) {
        this(method, url, body, headers, null);
    }

    public Request(String method, String url, String body, Map<String, String> headers,
                   Map<String, String> parameters) {
        mMethod = method;
        mUrl = url;
        mBody = body;
        mHeaders = headers;
        mParameters = parameters;
    }

    public Response send() {

        // Build complete request URL.
        Response response = null;
        String baseUrl = mUrl;
        try {
            // If parameters are present, add them to the request.
            if (MapUtils.isNotEmpty(mParameters)) {
                int i = 0;
                for (Map.Entry<String, String> entry : mParameters.entrySet()) {
                    baseUrl += (i == 0) ? '?' : '&';
                    baseUrl += URLEncoder.encode(entry.getKey(), "utf-8") + '=' +
                            URLEncoder.encode(entry.getValue(), "utf-8");
                    i++;
                }
            }
            URL formattedUri = new URL(baseUrl);

            // Configure the connection.
            HttpURLConnection connection = (HttpURLConnection) formattedUri.openConnection();
            connection.setRequestMethod(mMethod);
            connection.setConnectTimeout(Constants.CONNECT_TIMEOUT);
            connection.setReadTimeout(Constants.READ_TIMEOUT);

            // If headers are present, add them to the connection object.
            if (MapUtils.isNotEmpty(mHeaders))
                for (Map.Entry<String, String> entry : mHeaders.entrySet())
                    connection.setRequestProperty(entry.getKey(), entry.getValue());

            // If this is a POST method, there must be a body.
            if (mMethod.equals(Constants.POST) || mMethod.equals(Constants.PUT) ||
                    (mMethod.equals(Constants.PATCH) && !TextUtils.isEmpty(mBody))) {
                connection.setDoOutput(true);
                DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
                dos.writeBytes(mBody);
                dos.flush();
                dos.close();
            }

            // Retrieve response when it arrives, whether it is a success or a failure.
            try {
                InputStream is = connection.getInputStream();
                if (is != null)
                    response = new Response(connection.getResponseCode(), this.readResponse(is));
            } catch (IOException x) {
                InputStream es = connection.getErrorStream();
                if (es != null)
                    response = new Response(connection.getResponseCode(), this.readResponse(es));
                else
                    Log.e(getClass().getSimpleName(), "ERROR", x);
            }
        } catch (IOException x) {
            Log.e(getClass().getSimpleName(), "ERROR", x);
        }

        return response;
    }

    private String readResponse(InputStream input) throws IOException {
        if (input == null) return null;

        StringBuilder builder = new StringBuilder();

        BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
        try {
            String line;
            while ((line = reader.readLine()) != null)
                builder.append(line);
        } finally {
            reader.close();
            input.close();
        }

        return builder.toString();
    }
}

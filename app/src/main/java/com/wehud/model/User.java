package com.wehud.model;

import com.google.gson.annotations.SerializedName;
import com.wehud.util.Utils;

import java.util.List;

/**
 * This class represents a user of the application.
 *
 * @author Olivier Gonçalves, WeHud, 2017
 */

public final class User {

    @SerializedName("_id")
    private String mId;

    @SerializedName("avatar")
    private String mAvatar;

    @SerializedName("username")
    private String mUsername;

    @SerializedName("password")
    private String mPassword;

    @SerializedName("email")
    private String mEmail;

    @SerializedName("followers")
    private List<User> mFollowers;

    @SerializedName("connected")
    private boolean mIsConnected;

    @SerializedName("score")
    private int mScore;

    @SerializedName("createdAt")
    private String mDatetimeCreated;

    public String getId() {
        return mId;
    }

    public String getAvatar() {
        return mAvatar;
    }

    public String getUsername() {
        return mUsername;
    }

    public String getPassword() {
        return mPassword;
    }

    public String getEmail() {
        return mEmail;
    }

    public List<User> getFollowers() {
        return mFollowers;
    }

    public boolean isConnected() {
        return mIsConnected;
    }

    public int getScore() {
        return mScore;
    }

    public String getDatetimeCreated() {
        return Utils.isoDateStringToLocalDateString(mDatetimeCreated);
    }
}

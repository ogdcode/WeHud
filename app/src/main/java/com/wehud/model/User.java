package com.wehud.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * This class represents a user of the application.
 *
 * @author Olivier Gonçalves, WeHud, 2017
 */

public final class User {

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

}

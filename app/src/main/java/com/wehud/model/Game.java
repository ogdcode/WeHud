package com.wehud.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * This class represents a game stored in the application.
 *
 * @author Olivier Gonçalves, WeHud, 2017.
 */

public final class Game {

    @SerializedName("_id")
    private String mId;

    @SerializedName("name")
    private String mName;

    @SerializedName("synopsis")
    private String mSynopsis;

    @SerializedName("franchise")
    private String mFranchise;

    @SerializedName("followers")
    private List<User> mFollowers;

    @SerializedName("game")
    private String mGame;

    @SerializedName("developers")
    private List<String> mDevelopers;

    @SerializedName("publishers")
    private List<String> mPublishers;

    @SerializedName("dlcOrExpansion")
    private boolean mIsDlcOrExpansion;

    @SerializedName("modes")
    private List<String> mModes;

    @SerializedName("genres")
    private List<String> mGenres;

    @SerializedName("firstReleaseDate")
    private long mFirstReleaseDate;

    @SerializedName("status")
    private int mStatus;

    @SerializedName("cover")
    private String mCover;

    @SerializedName("pegi")
    private String mPegi;

    @SerializedName("esrb")
    private String mEsrb;

    @SerializedName("website")
    private String mWebsite;
}

package com.wehud.model;

import com.google.gson.annotations.SerializedName;
import com.wehud.util.Utils;

import java.util.List;

public final class Post {

    @SerializedName("_id")
    private String mId;

    @SerializedName("publisher")
    private User mPublisher;

    @SerializedName("game")
    private Game mGame;

    @SerializedName("receiver")
    private User mReceiver;

    @SerializedName("text")
    private String mText;

    @SerializedName("opinion")
    private boolean mIsOpinion;

    @SerializedName("message")
    private boolean mIsMessage;

    @SerializedName("rating")
    private int mRating;

    @SerializedName("videoUri")
    private String mVideoUri;

    @SerializedName("likes")
    private List<String> mLikes;

    @SerializedName("createdAt")
    private String mDatetimeCreated;

    public String getId() {
        return mId;
    }

    public User getPublisher() {
        return mPublisher;
    }

    public Game getGame() {
        return mGame;
    }

    public User getReceiver() {
        return mReceiver;
    }

    public String getText() {
        return mText;
    }

    public boolean isOpinion() {
        return mIsOpinion;
    }

    public boolean isMessage() {
        return mIsMessage;
    }

    public int getRating() {
        return mRating;
    }

    public String getVideoUri() {
        return mVideoUri;
    }

    public List<String> getLikes() {
        return mLikes;
    }

    public String getDatetimeCreated() {
        return Utils.isoDateStringToLocalDateString(mDatetimeCreated);
    }
}

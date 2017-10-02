package com.wehud.model;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.wehud.util.Utils;

import java.util.List;

public final class Post implements Parcelable, Comparable<Post> {

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
    private double mRating;

    @SerializedName("videoUri")
    private String mVideoUrl;

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

    public double getRating() {
        return mRating;
    }

    public String getVideoUrl() {
        return mVideoUrl;
    }

    public List<String> getLikes() {
        return mLikes;
    }

    public String getDatetimeCreated() {
        return Utils.isoDateTimeStringToLocalDateTimeString(mDatetimeCreated);
    }

    protected Post(Parcel in) {
        mId = in.readString();
        mPublisher = in.readParcelable(User.class.getClassLoader());
        mGame = in.readParcelable(Game.class.getClassLoader());
        mReceiver = in.readParcelable(User.class.getClassLoader());
        mText = in.readString();
        mIsOpinion = in.readByte() != 0;
        mIsMessage = in.readByte() != 0;
        mRating = in.readDouble();
        mVideoUrl = in.readString();
        mLikes = in.createStringArrayList();
        mDatetimeCreated = in.readString();
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mId);
        parcel.writeParcelable(mPublisher, i);
        parcel.writeParcelable(mGame, i);
        parcel.writeParcelable(mReceiver, i);
        parcel.writeString(mText);
        parcel.writeByte((byte) (mIsOpinion ? 1 : 0));
        parcel.writeByte((byte) (mIsMessage ? 1 : 0));
        parcel.writeDouble(mRating);
        parcel.writeString(mVideoUrl);
        parcel.writeStringList(mLikes);
        parcel.writeString(mDatetimeCreated);
    }

    @Override
    public String toString() {
        return "Post{" +
                "id='" + mId + '\'' +
                ", publisher=" + mPublisher +
                ", game=" + mGame +
                ", receiver=" + mReceiver +
                ", text='" + mText + '\'' +
                ", isOpinion=" + mIsOpinion +
                ", isMessage=" + mIsMessage +
                ", rating=" + mRating +
                ", videoUri='" + mVideoUrl + '\'' +
                ", likes=" + mLikes +
                ", datetimeCreated='" + mDatetimeCreated + '\'' +
                '}';
    }

    @Override
    public int compareTo(@NonNull Post post) {
        long tstp1 = Utils.isoDateTimeStringToTimestamp(mDatetimeCreated);
        long tstp2 = Utils.isoDateTimeStringToTimestamp(post.mDatetimeCreated);

        if (Build.VERSION.SDK_INT >= 19) return Long.compare(tstp1, tstp2);
        else return Long.valueOf(tstp1).compareTo(tstp2);
    }
}

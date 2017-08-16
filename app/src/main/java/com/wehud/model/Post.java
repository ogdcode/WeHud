package com.wehud.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.wehud.util.Utils;

import java.util.List;

public final class Post implements Parcelable {

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

    protected Post(Parcel in) {
        mId = in.readString();
        mPublisher = in.readParcelable(User.class.getClassLoader());
        mGame = in.readParcelable(Game.class.getClassLoader());
        mReceiver = in.readParcelable(User.class.getClassLoader());
        mText = in.readString();
        mIsOpinion = in.readByte() != 0;
        mIsMessage = in.readByte() != 0;
        mRating = in.readInt();
        mVideoUri = in.readString();
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
        parcel.writeInt(mRating);
        parcel.writeString(mVideoUri);
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
                ", videoUri='" + mVideoUri + '\'' +
                ", likes=" + mLikes +
                ", datetimeCreated='" + mDatetimeCreated + '\'' +
                '}';
    }
}

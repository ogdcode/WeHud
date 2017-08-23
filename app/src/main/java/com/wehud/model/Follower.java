package com.wehud.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public final class Follower implements Parcelable {

    @SerializedName("follower")
    private User mUser;

    @SerializedName("following")
    private String mFollowing;

    public User getUser() {
        return mUser;
    }

    public String getFollowing() {
        return mFollowing;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(mUser, i);
        parcel.writeString(mFollowing);
    }

    protected Follower(Parcel in) {
        mUser = in.readParcelable(User.class.getClassLoader());
        mFollowing = in.readString();
    }

    public static final Creator<Follower> CREATOR = new Creator<Follower>() {
        @Override
        public Follower createFromParcel(Parcel in) {
            return new Follower(in);
        }

        @Override
        public Follower[] newArray(int size) {
            return new Follower[size];
        }
    };
}

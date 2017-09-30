package com.wehud.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.wehud.util.Utils;

import java.util.Iterator;
import java.util.List;

/**
 * This class represents a user of the application.
 *
 * @author Olivier Gonçalves, WeHud, 2017
 */

public final class User implements Parcelable {

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

    public User() {}

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
        return Utils.isoDateTimeStringToLocalDateTimeString(mDatetimeCreated);
    }

    public void setId(String id) {
        mId = id;
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public void follow(User user) {
        mFollowers.add(user);
    }

    public void unfollow(User user) {
        Iterator<User> it = mFollowers.iterator();
        while (it.hasNext()) {
            User u = it.next();
            if (u.getId().equals(user.getId())) it.remove();
        }
    }

    protected User(Parcel in) {
        mId = in.readString();
        mAvatar = in.readString();
        mUsername = in.readString();
        mPassword = in.readString();
        mEmail = in.readString();
        mFollowers = in.createTypedArrayList(User.CREATOR);
        mIsConnected = in.readByte() != 0;
        mScore = in.readInt();
        mDatetimeCreated = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mId);
        parcel.writeString(mAvatar);
        parcel.writeString(mUsername);
        parcel.writeString(mPassword);
        parcel.writeString(mEmail);
        parcel.writeTypedList(mFollowers);
        parcel.writeByte((byte) (mIsConnected ? 1 : 0));
        parcel.writeInt(mScore);
        parcel.writeString(mDatetimeCreated);
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + mId + '\'' +
                ", avatar='" + mAvatar + '\'' +
                ", username='" + mUsername + '\'' +
                ", password='" + mPassword + '\'' +
                ", email='" + mEmail + '\'' +
                ", followers=" + mFollowers +
                ", isConnected=" + mIsConnected +
                ", score=" + mScore +
                ", datetimeCreated='" + mDatetimeCreated + '\'' +
                '}';
    }
}

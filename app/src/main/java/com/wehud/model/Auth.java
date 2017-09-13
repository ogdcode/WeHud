package com.wehud.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public final class Auth implements Parcelable {

    @SerializedName("_id")
    private String mId;

    @SerializedName("token")
    private String mToken;

    public String getId() {
        return mId;
    }

    public String getToken() {
        return mToken;
    }

    protected Auth(Parcel in) {
        mId = in.readString();
        mToken = in.readString();
    }

    public static final Creator<Auth> CREATOR = new Creator<Auth>() {
        @Override
        public Auth createFromParcel(Parcel in) {
            return new Auth(in);
        }

        @Override
        public Auth[] newArray(int size) {
            return new Auth[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mId);
        parcel.writeString(mToken);
    }
}

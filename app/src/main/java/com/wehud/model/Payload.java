package com.wehud.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public final class Payload implements Parcelable {

    @SerializedName("code")
    private String mCode;

    @SerializedName("content")
    private String mContent;

    public String getCode() {
        return mCode;
    }

    public String getContent() {
        return mContent;
    }

    protected Payload(Parcel in) {
        mCode = in.readString();
        mContent = in.readString();
    }

    public static final Creator<Payload> CREATOR = new Creator<Payload>() {
        @Override
        public Payload createFromParcel(Parcel in) {
            return new Payload(in);
        }

        @Override
        public Payload[] newArray(int size) {
            return new Payload[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mCode);
        parcel.writeString(mContent);
    }
}

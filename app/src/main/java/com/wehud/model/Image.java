package com.wehud.model;

import android.os.Parcel;
import android.os.Parcelable;

public final class Image implements Parcelable {
    private String mUrl;
    private int mResId;

    public Image(String url, int resId) {
        mUrl = url;
        mResId = resId;
    }

    public String getUrl() {
        return mUrl;
    }

    public int getResId() {
        return mResId;
    }

    protected Image(Parcel in) {
        mUrl = in.readString();
        mResId = in.readInt();
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mUrl);
        parcel.writeInt(mResId);
    }

    @Override
    public String toString() {
        return "Image{" +
                "mUrl='" + mUrl + '\'' +
                ", mResId=" + mResId +
                '}';
    }
}

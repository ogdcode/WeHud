package com.wehud.model;

import android.os.Parcel;
import android.os.Parcelable;

public final class Status implements Parcelable {
    private int mIcon;
    private String mDescription;

    public Status(int icon, String description) {
        mIcon = icon;
        mDescription = description;
    }

    public int getIcon() {
        return mIcon;
    }

    public String getDescription() {
        return mDescription;
    }

    protected Status(Parcel in) {
        mIcon = in.readInt();
        mDescription = in.readString();
    }

    public static final Creator<Status> CREATOR = new Creator<Status>() {
        @Override
        public Status createFromParcel(Parcel in) {
            return new Status(in);
        }

        @Override
        public Status[] newArray(int size) {
            return new Status[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mIcon);
        parcel.writeString(mDescription);
    }
}

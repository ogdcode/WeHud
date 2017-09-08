package com.wehud.model;

import android.os.Parcel;
import android.os.Parcelable;

public final class Tag implements Parcelable {
    private int mCode;
    private int mIcon;

    public Tag(int code, int icon) {
        mCode = code;
        mIcon = icon;
    }

    public int getCode() { return mCode; }

    public int getIcon() {
        return mIcon;
    }

    protected Tag(Parcel in) {
        mCode = in.readInt();
        mIcon = in.readInt();
    }

    public static final Creator<Tag> CREATOR = new Creator<Tag>() {
        @Override
        public Tag createFromParcel(Parcel in) {
            return new Tag(in);
        }

        @Override
        public Tag[] newArray(int size) {
            return new Tag[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(mCode);
        parcel.writeInt(mIcon);
    }
}

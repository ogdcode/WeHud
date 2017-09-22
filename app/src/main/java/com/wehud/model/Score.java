package com.wehud.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public final class Score implements Parcelable {

    @SerializedName("level")
    private int mLevel;

    @SerializedName("rank")
    private String mRank;

    public Score(int level, String rank) {
        mLevel = level;
        mRank = rank;
    }

    public int getLevel() {
        return mLevel;
    }

    public char getRank() {
        return mRank.charAt(0);
    }

    protected Score(Parcel in) {
        mLevel = in.readInt();
        mRank = in.readString();
    }

    public static final Creator<Score> CREATOR = new Creator<Score>() {
        @Override
        public Score createFromParcel(Parcel in) {
            return new Score(in);
        }

        @Override
        public Score[] newArray(int size) {
            return new Score[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(mLevel);
        parcel.writeString(mRank);
    }
}

package com.wehud.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public final class Reward implements Parcelable {

    @SerializedName("score")
    private int mScore;

    @SerializedName("action")
    private int mAction;

    @SerializedName("entities")
    private List<String> mEntities;

    @SerializedName("points")
    private int mBonus;

    public int getScore() {
        return mScore;
    }

    public int getAction() {
        return mAction;
    }

    public List<String> getEntities() {
        return mEntities;
    }

    public int getBonus() {
        return mBonus;
    }

    protected Reward(Parcel in) {
        mScore = in.readInt();
        mAction = in.readInt();
        mEntities = in.createStringArrayList();
        mBonus = in.readInt();
    }

    public static final Creator<Reward> CREATOR = new Creator<Reward>() {
        @Override
        public Reward createFromParcel(Parcel in) {
            return new Reward(in);
        }

        @Override
        public Reward[] newArray(int size) {
            return new Reward[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(mScore);
        parcel.writeInt(mAction);
        parcel.writeStringList(mEntities);
        parcel.writeInt(mBonus);
    }
}

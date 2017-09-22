package com.wehud.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public final class CreatedResponse implements Parcelable {

    @SerializedName("_id")
    private String mId;

    @SerializedName("title")
    private String mTitle;

    @SerializedName("reward")
    private Reward mReward;

    public String getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public Reward getReward() {
        return mReward;
    }

    protected CreatedResponse(Parcel in) {
        mId = in.readString();
        mTitle = in.readString();
        mReward = in.readParcelable(Reward.class.getClassLoader());
    }

    public static final Creator<CreatedResponse> CREATOR = new Creator<CreatedResponse>() {
        @Override
        public CreatedResponse createFromParcel(Parcel in) {
            return new CreatedResponse(in);
        }

        @Override
        public CreatedResponse[] newArray(int size) {
            return new CreatedResponse[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mId);
        parcel.writeString(mTitle);
        parcel.writeParcelable(mReward, i);
    }
}

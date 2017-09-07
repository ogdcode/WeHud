package com.wehud.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public final class Event implements Parcelable {

    @SerializedName("_id")
    private String mId;

    @SerializedName("creator")
    private User mCreator;

    @SerializedName("title")
    private String mTitle;

    @SerializedName("description")
    private String mDescription;

    @SerializedName("startDateTime")
    private long mStartDateTime;

    @SerializedName("endDateTime")
    private long mEndDateTime;

    @SerializedName("tag")
    private int mTag;

    @SerializedName("planning")
    private String mPlanning;

    public String getId() {
        return mId;
    }

    public User getCreator() {
        return mCreator;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public long getStartDateTime() {
        return mStartDateTime;
    }

    public long getEndDateTime() {
        return mEndDateTime;
    }

    public int getTag() {
        return mTag;
    }

    public String getPlanning() {
        return mPlanning;
    }

    protected Event(Parcel in) {
        mId = in.readString();
        mCreator = in.readParcelable(User.class.getClassLoader());
        mTitle = in.readString();
        mDescription = in.readString();
        mStartDateTime = in.readLong();
        mEndDateTime = in.readLong();
        mTag = in.readInt();
        mPlanning = in.readString();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(mId);
        parcel.writeParcelable(mCreator, flags);
        parcel.writeString(mTitle);
        parcel.writeString(mDescription);
        parcel.writeLong(mStartDateTime);
        parcel.writeLong(mEndDateTime);
        parcel.writeInt(mTag);
        parcel.writeString(mPlanning);
    }
}

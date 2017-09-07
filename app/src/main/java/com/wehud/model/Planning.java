package com.wehud.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public final class Planning implements Parcelable {

    @SerializedName("_id")
    private String mId;

    @SerializedName("title")
    private String mTitle;

    @SerializedName("creator")
    private User mCreator;

    @SerializedName("events")
    private List<Event> mEvents;

    public String getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public User getCreator() {
        return mCreator;
    }

    public List<Event> getEvents() {
        return mEvents;
    }

    protected Planning(Parcel in) {
        mId = in.readString();
        mTitle = in.readString();
        mCreator = in.readParcelable(User.class.getClassLoader());
        mEvents = in.createTypedArrayList(Event.CREATOR);
    }

    public static final Creator<Planning> CREATOR = new Creator<Planning>() {
        @Override
        public Planning createFromParcel(Parcel in) {
            return new Planning(in);
        }

        @Override
        public Planning[] newArray(int size) {
            return new Planning[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(mId);
        parcel.writeString(mTitle);
        parcel.writeParcelable(mCreator, flags);
        parcel.writeTypedList(mEvents);
    }

    @Override
    public String toString() {
        return mTitle + " [" + mEvents.size() + " event(s)]";
    }
}

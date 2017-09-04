package com.wehud.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public final class Event implements Parcelable {

    @SerializedName("creator")
    private User creator;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("startDate")
    private long startDate;

    @SerializedName("endDate")
    private long endDate;

    @SerializedName("tag")
    private int tag;

    @SerializedName("planning")
    private String planning;

    public User getCreator() {
        return creator;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public long getStartDate() {
        return startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public int getTag() {
        return tag;
    }

    public String getPlanning() {
        return planning;
    }

    protected Event(Parcel in) {
        creator = in.readParcelable(User.class.getClassLoader());
        title = in.readString();
        description = in.readString();
        startDate = in.readLong();
        endDate = in.readLong();
        tag = in.readInt();
        planning = in.readString();
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
        parcel.writeParcelable(creator, flags);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeLong(startDate);
        parcel.writeLong(endDate);
        parcel.writeInt(tag);
        parcel.writeString(planning);
    }
}

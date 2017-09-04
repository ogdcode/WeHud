package com.wehud.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public final class Planning implements Parcelable {

    @SerializedName("title")
    private String title;

    @SerializedName("creator")
    private User creator;

    @SerializedName("events")
    private List<Event> events;

    public String getTitle() {
        return title;
    }

    public User getCreator() {
        return creator;
    }

    public List<Event> getEvents() {
        return events;
    }

    protected Planning(Parcel in) {
        title = in.readString();
        creator = in.readParcelable(User.class.getClassLoader());
        events = in.createTypedArrayList(Event.CREATOR);
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
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeParcelable(creator, i);
        parcel.writeTypedList(events);
    }
}

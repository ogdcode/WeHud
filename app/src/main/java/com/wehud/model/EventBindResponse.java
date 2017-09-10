package com.wehud.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public final class EventBindResponse implements Parcelable {

    @SerializedName("planning")
    private String mPlanning;

    @SerializedName("events")
    private List<Event> mEvents;

    public String getPlanning() {
        return mPlanning;
    }

    public List<Event> getEvents() {
        return mEvents;
    }

    protected EventBindResponse(Parcel in) {
        mPlanning = in.readString();
        mEvents = in.createTypedArrayList(Event.CREATOR);
    }

    public static final Creator<EventBindResponse> CREATOR = new Creator<EventBindResponse>() {
        @Override
        public EventBindResponse createFromParcel(Parcel in) {
            return new EventBindResponse(in);
        }

        @Override
        public EventBindResponse[] newArray(int size) {
            return new EventBindResponse[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(mPlanning);
        parcel.writeTypedList(mEvents);
    }
}

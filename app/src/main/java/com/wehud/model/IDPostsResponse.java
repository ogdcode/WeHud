package com.wehud.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public final class IDPostsResponse implements Parcelable {

    @SerializedName("_id")
    private String mId;

    @SerializedName("posts")
    private List<Post> mPosts;

    public String getId() {
        return mId;
    }

    public List<Post> getPosts() {
        return mPosts;
    }

    protected IDPostsResponse(Parcel in) {
        mId = in.readString();
        mPosts = in.createTypedArrayList(Post.CREATOR);
    }

    public static final Creator<IDPostsResponse> CREATOR = new Creator<IDPostsResponse>() {
        @Override
        public IDPostsResponse createFromParcel(Parcel in) {
            return new IDPostsResponse(in);
        }

        @Override
        public IDPostsResponse[] newArray(int size) {
            return new IDPostsResponse[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mId);
        parcel.writeTypedList(mPosts);
    }
}

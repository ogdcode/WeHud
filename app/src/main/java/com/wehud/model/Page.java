package com.wehud.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public final class Page {

    @SerializedName("_id")
    private String mId;

    @SerializedName("title")
    private String mTitle;

    @SerializedName("mOwner")
    private User mOwner;

    @SerializedName("users")
    private List<User> mUsers;

    @SerializedName("posts")
    private List<Post> mPosts;

    public String getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public User getOwner() {
        return mOwner;
    }

    public List<User> getUsers() {
        return mUsers;
    }

    public List<Post> getPosts() {
        return mPosts;
    }
}

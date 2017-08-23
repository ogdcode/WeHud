package com.wehud.model;

public final class Status {
    private int mIcon;
    private String mDescription;

    public Status(int icon, String description) {
        mIcon = icon;
        mDescription = description;
    }

    public int getIcon() {
        return mIcon;
    }

    public String getDescription() {
        return mDescription;
    }
}

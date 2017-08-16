package com.wehud.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * This class represents a game stored in the application.
 *
 * @author Olivier Gonçalves, WeHud, 2017.
 */

public final class Game implements Parcelable {

    @SerializedName("_id")
    private String mId;

    @SerializedName("name")
    private String mName;

    @SerializedName("synopsis")
    private String mSynopsis;

    @SerializedName("franchise")
    private String mFranchise;

    @SerializedName("followers")
    private List<User> mFollowers;

    @SerializedName("game")
    private String mGame;

    @SerializedName("developers")
    private List<String> mDevelopers;

    @SerializedName("publishers")
    private List<String> mPublishers;

    @SerializedName("dlcOrExpansion")
    private boolean mIsDlcOrExpansion;

    @SerializedName("modes")
    private List<String> mModes;

    @SerializedName("genres")
    private List<String> mGenres;

    @SerializedName("firstReleaseDate")
    private long mFirstReleaseDate;

    @SerializedName("status")
    private int mStatus;

    @SerializedName("cover")
    private String mCover;

    @SerializedName("pegi")
    private String mPegi;

    @SerializedName("esrb")
    private String mEsrb;

    @SerializedName("website")
    private String mWebsite;

    protected Game(Parcel in) {
        mId = in.readString();
        mName = in.readString();
        mSynopsis = in.readString();
        mFranchise = in.readString();
        mFollowers = in.createTypedArrayList(User.CREATOR);
        mGame = in.readString();
        mDevelopers = in.createStringArrayList();
        mPublishers = in.createStringArrayList();
        mIsDlcOrExpansion = in.readByte() != 0;
        mModes = in.createStringArrayList();
        mGenres = in.createStringArrayList();
        mFirstReleaseDate = in.readLong();
        mStatus = in.readInt();
        mCover = in.readString();
        mPegi = in.readString();
        mEsrb = in.readString();
        mWebsite = in.readString();
    }

    public static final Creator<Game> CREATOR = new Creator<Game>() {
        @Override
        public Game createFromParcel(Parcel in) {
            return new Game(in);
        }

        @Override
        public Game[] newArray(int size) {
            return new Game[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mId);
        parcel.writeString(mName);
        parcel.writeString(mSynopsis);
        parcel.writeString(mFranchise);
        parcel.writeTypedList(mFollowers);
        parcel.writeString(mGame);
        parcel.writeStringList(mDevelopers);
        parcel.writeStringList(mPublishers);
        parcel.writeByte((byte) (mIsDlcOrExpansion ? 1 : 0));
        parcel.writeStringList(mModes);
        parcel.writeStringList(mGenres);
        parcel.writeLong(mFirstReleaseDate);
        parcel.writeInt(mStatus);
        parcel.writeString(mCover);
        parcel.writeString(mPegi);
        parcel.writeString(mEsrb);
        parcel.writeString(mWebsite);
    }

    @Override
    public String toString() {
        return "Game{" +
                "id='" + mId + '\'' +
                ", name='" + mName + '\'' +
                ", synopsis='" + mSynopsis + '\'' +
                ", franchise='" + mFranchise + '\'' +
                ", followers=" + mFollowers +
                ", game='" + mGame + '\'' +
                ", developers=" + mDevelopers +
                ", publishers=" + mPublishers +
                ", isDlcOrExpansion=" + mIsDlcOrExpansion +
                ", modes=" + mModes +
                ", genres=" + mGenres +
                ", firstReleaseDate=" + mFirstReleaseDate +
                ", status=" + mStatus +
                ", cover='" + mCover + '\'' +
                ", pegi='" + mPegi + '\'' +
                ", esrb='" + mEsrb + '\'' +
                ", website='" + mWebsite + '\'' +
                '}';
    }
}

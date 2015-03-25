package com.tep.android.taphold.beans;

import com.google.gson.annotations.SerializedName;

public class Gamer {

    @SerializedName("id")
    private String mId;

    @SerializedName("username")
    private String mUsername;

    @SerializedName("points")
    private long mPoints;

    @SerializedName("__version")
    private String mVersion;

    public Gamer() {
    }

    public Gamer(String username, long points) {
        setUsername(username);
        setPoints(points);
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    public long getPoints() {
        return mPoints;
    }

    public void setPoints(long mPoints) {
        this.mPoints = mPoints;
    }
}

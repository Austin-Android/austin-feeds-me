package com.austindroids.austinfeedsme.data;

import com.google.gson.annotations.SerializedName;

public class GroupPhoto {
    @SerializedName("highres_link")
    private String photoUrl;

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}

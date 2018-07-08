package com.austindroids.austinfeedsme.data.eventbrite;

import com.google.gson.annotations.SerializedName;

public class OriginalLogo {
    @SerializedName("url")
    private String logoUrl;

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }
}

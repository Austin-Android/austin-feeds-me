package com.austindroids.austinfeedsme.data.eventbrite;

import com.google.gson.annotations.SerializedName;

public class Logo {
    @SerializedName("original") private OriginalLogo originalLogo;

    public OriginalLogo getOriginalLogo() {
        return originalLogo;
    }

    public void setOriginalLogo(OriginalLogo originalLogo) {
        this.originalLogo = originalLogo;
    }
}

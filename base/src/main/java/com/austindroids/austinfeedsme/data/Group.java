package com.austindroids.austinfeedsme.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by darrankelinske on 8/4/16.
 */
public class Group {
    private String name;
    @SerializedName("group_photo")
    private GroupPhoto groupPhoto;

    public Group() {
    }

    public Group(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public GroupPhoto getGroupPhoto() {
        return groupPhoto;
    }

    public void setGroupPhoto(GroupPhoto groupPhoto) {
        this.groupPhoto = groupPhoto;
    }
}
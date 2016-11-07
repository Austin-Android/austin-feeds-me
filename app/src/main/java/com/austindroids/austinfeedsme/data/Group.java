package com.austindroids.austinfeedsme.data;

import org.parceler.Parcel;

/**
 * Created by darrankelinske on 8/4/16.
 */
@Parcel
public class Group {
    private String name;

    public Group() {
    }

    public Group(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
package com.austindroids.austinfeedsme.data;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by daz on 8/5/16.
 */
@Parcel
public class Venue {

    private String address_1;
    private String address_2;
    private String address_3;
    @SerializedName(value = "lat", alternate = {"latitude"})
    private String lat;
    @SerializedName(value = "lon", alternate = {"longitude"})
    private String lon;
    private String zip;
    private String country;
    private String city;
    private String state;
    private String phone;
    private String name;

    public String getState() {
        return state;
    }

    public String getAddress_1() {
        return address_1;
    }

    public String getAddress_2() {
        return address_2;
    }

    public String getAddress_3() {
        return address_3;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }

    public String getZip() {
        return zip;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }

}

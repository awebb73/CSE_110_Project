package com.group9.cse110project;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by art on 11/16/15.
 */
public class LocationHolder {


    private String sent;
    private int rating;
    public LatLng loc;

    // 3 arg constructor
    // int to hold the star rating
    // lat-long object
    // string
    public LocationHolder(String e, int rating, LatLng loc)
    {
        this.sent = sent;
        this.rating = rating;
        this.loc = loc;
    }

    // getter for the string
    public String getSent() {
        return sent;
    }
    // setter for the string
    public void setSent(String sent) {
        this.sent = sent;
    }
    // getter for the rating
    public int getRating() {
        return rating;
    }
    // setter for the rating
    public void setRating(int rating) {
        this.rating = rating;
    }
    // getter for the location
    public LatLng getLoc() {
        return loc;
    }
    // setter for the location
    public void setLoc(LatLng loc) {
        this.loc = loc;
    }
}
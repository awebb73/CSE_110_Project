package com.group9.cse110project;

import android.location.LocationManager;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;


/**
 * Created by art on 11/16/15.
 */
public class LocationHolder implements Parcelable {
    private String sent;
    private double rating;
    private LatLng loc;
    private int count;



    // 3 arg constructor
    // int to hold the star rating
    // lat-long object
    // string
    public LocationHolder(String e, double rating, LatLng loc)
    {
        this.sent = e;
        this.rating = rating;
        this.loc = loc;
        this.count = 1;
    }

    private LocationHolder(Parcel in){
        readFromParcel(in);
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
    public double getRating() {
        return rating;
    }
    // setter for the rating
    public void setRating(double rating) {
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
    public void incrementCount(){
        this.count++;
    }
    public int getCount(){
        return this.count;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sent);
        dest.writeDouble(rating);
        dest.writeParcelable(loc, flags);
        dest.writeInt(count);
    }

    private void readFromParcel(Parcel source){
        this.sent = source.readString();
        this.rating = source.readDouble();
        this.loc = source.readParcelable(LatLng.class.getClassLoader());
        this.count = source.readInt();

    }

    public static final Parcelable.Creator<LocationHolder> CREATOR = new Parcelable.Creator<LocationHolder>(){
        @Override
        public LocationHolder createFromParcel(Parcel in){
            return new LocationHolder(in);
        }
        @Override
        public LocationHolder[] newArray(int size){
            return new LocationHolder[size];
        }
    };
}
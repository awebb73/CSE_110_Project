package com.group9.cse110project;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;


/**
 * Created by art on 11/16/15.
 */
public class LocationHolder implements Parcelable {
    private String sent;
    private float rating;
    // private LatLng loc;
    private int count;
    private double lat, lng;



    // 4 arg constructor
    // float to hold the star rating
    // doubles for the lat and long
    // string for message
    // removing the string since it is out of scope
    // if we decide to add it back it is no big deal
    public LocationHolder(String e, float rating, double lat, double lng)
    {
        this.sent = e;
        this.rating = rating;
        this.lat = lat;
        this.lng = lng;
        this.count = 1;
    }

    public LocationHolder(Parcel source)
    {
        this.rating = source.readInt();
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
    public float getRating() {
        return rating;
    }
    // setter for the rating
    public void setRating(float rating) {
        this.rating = rating;
    }
    /*
    // getter for the location
    public LatLng getLoc() {
        return loc;
    }
    // setter for the location
    public void setLoc(LatLng loc) {
        this.loc = loc;
    }
    */

    // incrementer
    public void incrementCount(){
        this.count++;
    }
    // getter for the count
    public int getCount(){
        return this.count;
    }


    //getter for the lat
    public double getLat()
    {
        return lat;
    }
    // getter for the lng
    public double getLng()
    {
        return lng;
    }
    // setter for the location
    public void setLat (double lat) {
        this.lat = lat;
    }
    // setter for the location
    public void setLng(double lng) {
        this.lng = lng;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // dest.writeString(sent);
        dest.writeFloat(rating);
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lng);
        dest.writeInt(count);
    }

    private void readFromParcel(Parcel source){
        this.sent = source.readString();
        this.rating = source.readFloat();
        this.lat = source.readDouble();
        this.lng = source.readDouble();
        // this.loc = source.readParcelable(LatLng.class.getClassLoader());
        this.count = source.readInt();

    }

    public static final Parcelable.Creator<LocationHolder> CREATOR = new Parcelable.Creator<LocationHolder>(){
        @Override
        public LocationHolder createFromParcel(Parcel in){
            return new LocationHolder(in);  // RECREATE LOCATIONHOLDER GIVEN COURSE
        }
        @Override
        public LocationHolder[] newArray(int size){
            return new LocationHolder[size];  // CREATING AN ARRAY OF LOCATIONHOLDERS
        }
    };
}
package com.example.roystonbehzhiyang.parkr.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by roystonbehzhiyang on 30/10/17.
 */

public class ShoppingParking extends RealmObject implements Parcelable{

    @PrimaryKey
    @SerializedName("carpark no.")
    private String mCarpark_no;

    @SerializedName("lat")
    private double mLat;

    @SerializedName("lon")
    private double mLon;

    @SerializedName("lots_available")
    private String mLots_available;

    @SerializedName("area")
    private String mArea;

    @SerializedName("development")
    private String mDevelopment;

    public ShoppingParking(){}
    public ShoppingParking(String car_park_no, double lat, double lon, String lots_available,String area, String development) {
        mCarpark_no = car_park_no;
        mLat = lat;
        mLon = lon;
        mLots_available = lots_available;
        mArea = area;
        mDevelopment = development;
    }

    public String getmArea() {
        return mArea;
    }

    public void setmArea(String mArea) {
        this.mArea = mArea;
    }

    public String getmDevelopment() {
        return mDevelopment;
    }

    public void setmDevelopment(String mDevelopment) {
        this.mDevelopment = mDevelopment;
    }

    public String getmCarpark_no() {
        return mCarpark_no;
    }

    public void setmCarpark_no(String mCarpark_no) {
        this.mCarpark_no = mCarpark_no;
    }
    
    public double getmLat() {
        return mLat;
    }

    public void setmLat(double mLat) {
        this.mLat = mLat;
    }

    public double getmLon() {
        return mLon;
    }

    public void setmLon(double mLon) {
        this.mLon = mLon;
    }

    public String getmLots_available() {
        return mLots_available;
    }

    public void setmLots_available(String mLots_available) {
        this.mLots_available = mLots_available;
    }

    private ShoppingParking(Parcel in) {
        mCarpark_no = in.readString();
        mLat = in.readDouble();
        mLon = in.readDouble();
        mLots_available = in.readString();
        mArea = in.readString();
        mDevelopment = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mCarpark_no);
        parcel.writeDouble(mLat);
        parcel.writeDouble(mLon);
        parcel.writeString(mLots_available);
        parcel.writeString(mArea);
        parcel.writeString(mDevelopment);
    }

    public final static Creator<ShoppingParking> CREATOR = new Creator<ShoppingParking>() {
        @Override
        public ShoppingParking createFromParcel(Parcel parcel) {
            return new ShoppingParking(parcel);
        }

        @Override
        public ShoppingParking[] newArray(int i) {
            return new ShoppingParking[i];
        }

    };





}

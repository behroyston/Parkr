package com.example.roystonbehzhiyang.parkr.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by roystonbehzhiyang on 18/10/17.
 */


public class ParkingLot extends RealmObject implements Parcelable {

    @PrimaryKey
    @SerializedName("carpark no.")
    private String mCarpark_no;

    @SerializedName("address")
    private String mAddress;

    @SerializedName("lat")
    private double mLat;

    @SerializedName("lon")
    private double mLon;

    @SerializedName("car_park_type")
    private String mCar_park_type;

    @SerializedName("type_of_parking")
    private String mType_of_parking;

    @SerializedName("short_term_parking")
    private String mShort_term_parking;

    @SerializedName("free_parking")
    private String mFree_parking;

    @SerializedName("night_parking")
    private String mNight_parking;

    @SerializedName("total_lots_available")
    private String mTotal_lots_available;

    @SerializedName("lots_available")
    private String mLots_available;

    @SerializedName("lots_type")
    private String mLots_type;

    public ParkingLot(){}

    public ParkingLot(String carpark_no, String address, double lat, double lon, String car_park_type, String type_of_parking,
                      String short_term_parking, String free_parking, String night_parking, String total_lots_available,
                      String lots_available, String lots_type) {
        mCarpark_no = carpark_no;
        mAddress = address;
        mLat = lat;
        mLon = lon;
        mCar_park_type = car_park_type;
        mType_of_parking = type_of_parking;
        mShort_term_parking = short_term_parking;
        mFree_parking = free_parking;
        mNight_parking = night_parking;
        mTotal_lots_available = total_lots_available;
        mLots_available = lots_available;
        mLots_type = lots_type;
    }


    public String getmCarpark_no() {
        return mCarpark_no;
    }

    public void setmCarpark_no(String mCarpark_no) {
        this.mCarpark_no = mCarpark_no;
    }

    public String getmAddress() {
        return mAddress;
    }

    public void setmAddress(String mAddress) {
        this.mAddress = mAddress;
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

    public String getmCar_park_type() {
        return mCar_park_type;
    }

    public void setmCar_park_type(String mCar_park_type) {
        this.mCar_park_type = mCar_park_type;
    }

    public String getmType_of_parking() {
        return mType_of_parking;
    }

    public void setmType_of_parking(String mType_of_parking) {
        this.mType_of_parking = mType_of_parking;
    }

    public String getmShort_term_parking() {
        return mShort_term_parking;
    }

    public void setmShort_term_parking(String mShort_term_parking) {
        this.mShort_term_parking = mShort_term_parking;
    }

    public String getmFree_parking() {
        return mFree_parking;
    }

    public void setmFree_parking(String mFree_parking) {
        this.mFree_parking = mFree_parking;
    }

    public String getmNight_parking() {
        return mNight_parking;
    }

    public void setmNight_parking(String mNight_parking) {
        this.mNight_parking = mNight_parking;
    }

    public String getmTotal_lots_available() {
        return mTotal_lots_available;
    }

    public void setmTotal_lots_available(String mTotal_lots_available) {
        this.mTotal_lots_available = mTotal_lots_available;
    }

    public String getmLots_available() {
        return mLots_available;
    }

    public void setmLots_available(String mLots_available) {
        this.mLots_available = mLots_available;
    }

    public String getmLots_type() {
        return mLots_type;
    }

    public void setmLots_type(String mLots_type) {
        this.mLots_type = mLots_type;
    }

    private ParkingLot(Parcel in) {
        mCarpark_no = in.readString();
        mAddress = in.readString();
        mLat = in.readDouble();
        mLon = in.readDouble();
        mCar_park_type = in.readString();
        mType_of_parking = in.readString();
        mShort_term_parking = in.readString();
        mFree_parking = in.readString();
        mNight_parking = in.readString();
        mTotal_lots_available = in.readString();
        mLots_available = in.readString();
        mLots_type = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mCarpark_no);
        parcel.writeString(mAddress);
        parcel.writeDouble(mLat);
        parcel.writeDouble(mLon);
        parcel.writeString(mCar_park_type);
        parcel.writeString(mType_of_parking);
        parcel.writeString(mShort_term_parking);
        parcel.writeString(mFree_parking);
        parcel.writeString(mNight_parking);
        parcel.writeString(mTotal_lots_available);
        parcel.writeString(mLots_available);
        parcel.writeString(mLots_type);
    }

    public final static Creator<ParkingLot> CREATOR = new Creator<ParkingLot>() {
        @Override
        public ParkingLot createFromParcel(Parcel parcel) {
            return new ParkingLot(parcel);
        }

        @Override
        public ParkingLot[] newArray(int i) {
            return new ParkingLot[i];
        }

    };
}


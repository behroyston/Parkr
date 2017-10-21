package com.example.roystonbehzhiyang.parkr.pojo;

/**
 * Created by roystonbehzhiyang on 18/10/17.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("short_term_parking")
    @Expose
    private String shortTermParking;
    @SerializedName("night_parking")
    @Expose
    private String nightParking;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("lat")
    @Expose
    private Double lat;
    @SerializedName("total_lots_available")
    @Expose
    private Integer totalLotsAvailable;
    @SerializedName("lots_available")
    @Expose
    private Integer lotsAvailable;
    @SerializedName("free_parking")
    @Expose
    private String freeParking;
    @SerializedName("carpark no.")
    @Expose
    private String carparkNo;
    @SerializedName("car_park_type")
    @Expose
    private String carParkType;
    @SerializedName("lon")
    @Expose
    private Double lon;
    @SerializedName("type_of_parking")
    @Expose
    private String typeOfParking;
    @SerializedName("lots_type")
    @Expose
    private String lotsType;

    public String getShortTermParking() {
        return shortTermParking;
    }

    public void setShortTermParking(String shortTermParking) {
        this.shortTermParking = shortTermParking;
    }

    public String getNightParking() {
        return nightParking;
    }

    public void setNightParking(String nightParking) {
        this.nightParking = nightParking;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Integer getTotalLotsAvailable() {
        return totalLotsAvailable;
    }

    public void setTotalLotsAvailable(Integer totalLotsAvailable) {
        this.totalLotsAvailable = totalLotsAvailable;
    }

    public Integer getLotsAvailable() {
        return lotsAvailable;
    }

    public void setLotsAvailable(Integer lotsAvailable) {
        this.lotsAvailable = lotsAvailable;
    }

    public String getFreeParking() {
        return freeParking;
    }

    public void setFreeParking(String freeParking) {
        this.freeParking = freeParking;
    }

    public String getCarparkNo() {
        return carparkNo;
    }

    public void setCarparkNo(String carparkNo) {
        this.carparkNo = carparkNo;
    }

    public String getCarParkType() {
        return carParkType;
    }

    public void setCarParkType(String carParkType) {
        this.carParkType = carParkType;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public String getTypeOfParking() {
        return typeOfParking;
    }

    public void setTypeOfParking(String typeOfParking) {
        this.typeOfParking = typeOfParking;
    }

    public String getLotsType() {
        return lotsType;
    }

    public void setLotsType(String lotsType) {
        this.lotsType = lotsType;
    }

}


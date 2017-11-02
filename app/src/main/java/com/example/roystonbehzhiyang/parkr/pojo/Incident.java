package com.example.roystonbehzhiyang.parkr.pojo;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

/**
 * Created by shixuanteng on 24/10/17.
 */
@IgnoreExtraProperties
public class Incident {

    public String uid;
    public double latitude;
    public double longitude;
    public String incidentType;
    public String reportedUID;
    public Object reportedDateTime;

    public Incident(){}

    public Incident(String uid, double latitude, double longitude, String incidentType, String reportedUID){
        this.uid = uid;
        this.latitude = latitude;
        this.longitude = longitude;
        this.incidentType = incidentType;
        this.reportedUID = reportedUID;
        this.reportedDateTime = ServerValue.TIMESTAMP;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getIncidentType() {
        return incidentType;
    }

    public void setIncidentType(String incidentType) {
        this.incidentType = incidentType;
    }
}

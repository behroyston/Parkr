package com.example.roystonbehzhiyang.parkr.pojo;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Users {

    public String uid;
    public String displayName;
    public String email;
    public String status;
    public String photoURI;

    public Users(){}

    public Users(String uid, String displayName, String email, String status, String photoURI){
        this.uid = uid;
        this.displayName = displayName;
        this.email = email;
        this.status = status;
        this.photoURI = photoURI;
    }

}
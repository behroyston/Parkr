package com.example.roystonbehzhiyang.parkr.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by roystonbehzhiyang on 18/10/17.
 */

public class HDBParkingLotResult {
    @SerializedName("result")
    public ArrayList<HDBParking> parkingLots = new ArrayList<>();
}

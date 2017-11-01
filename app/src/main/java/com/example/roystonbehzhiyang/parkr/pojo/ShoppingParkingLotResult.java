package com.example.roystonbehzhiyang.parkr.pojo;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by roystonbehzhiyang on 1/11/17.
 */

public class ShoppingParkingLotResult {
    @SerializedName("result")
    public ArrayList<ShoppingParking> parkingLots = new ArrayList<>();
}

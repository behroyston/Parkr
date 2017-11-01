package com.example.roystonbehzhiyang.parkr.data;

import com.example.roystonbehzhiyang.parkr.pojo.HDBParkingLotResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by roystonbehzhiyang on 18/10/17.
 */

public interface ParkrAPIInterface {

    @GET(".")
    Call<HDBParkingLotResult> getParkingLots(@Query("lat") double lat, @Query("lon") double lon);

}

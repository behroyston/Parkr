package com.example.roystonbehzhiyang.parkr.data;

import com.example.roystonbehzhiyang.parkr.pojo.ParkingLotResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by roystonbehzhiyang on 18/10/17.
 */

public interface ParkrAPIInterface {

    @GET(".")
    Call<ParkingLotResult> getParkingLots(@Query("lat") double lat, @Query("lon") double lon);

}

package com.example.roystonbehzhiyang.parkr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.roystonbehzhiyang.parkr.pojo.ParkingLot;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ParkingLotDetails extends AppCompatActivity {

    private ParkingLot mParkingLot;
    @BindView(R.id.address)
    TextView address;

    @BindView(R.id.car_park_type)
    TextView car_park_type;

    @BindView(R.id.parking_type)
    TextView parking_type;

    @BindView(R.id.short_term_parking)
    TextView short_term_parking;

    @BindView(R.id.free_parking)
    TextView free_parking;

    @BindView(R.id.night_parking)
    TextView night_parking;

    @BindView(R.id.total_lots_available)
    TextView total_lots_available;

    @BindView(R.id.lots_available)
    TextView lots_available;

    @BindView(R.id.lots_type)
    TextView lots_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_lot_details);
        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        mParkingLot = extras.getParcelable(MainActivity.PARKING_LOT_DETAILS);

        address.setText(mParkingLot.getmAddress());
        car_park_type.setText(mParkingLot.getmCar_park_type());
        parking_type.setText(mParkingLot.getmType_of_parking());
        free_parking.setText(mParkingLot.getmFree_parking());
        night_parking.setText(mParkingLot.getmNight_parking());
        short_term_parking.setText(mParkingLot.getmShort_term_parking());
        total_lots_available.setText(mParkingLot.getmTotal_lots_available());
        lots_available.setText(mParkingLot.getmLots_available());
        lots_type.setText(mParkingLot.getmLots_type());


    }
}

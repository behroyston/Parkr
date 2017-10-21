package com.example.roystonbehzhiyang.parkr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.roystonbehzhiyang.parkr.pojo.ParkingLot;

import io.realm.Realm;
import io.realm.RealmResults;

public class FavouriteActivity extends AppCompatActivity implements FavouriteAdapter.FavouriteAdapterOnClickHandler{

    private Realm realm;

    private final String TAG = FavouriteActivity.class.getSimpleName();

    private RealmResults<ParkingLot> myParkingLots;

    public final static String PARKING_LOT_FAVOURITE = "parking_lot_favourite";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        realm = Realm.getDefaultInstance();

        myParkingLots = realm.where(ParkingLot.class).findAll();

        FavouriteAdapter mAdapter = new FavouriteAdapter(this,this, myParkingLots);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.carpark_favourite_recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onClick(int id) {
        ParkingLot mParkingLot = myParkingLots.get(id);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(PARKING_LOT_FAVOURITE, mParkingLot);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}

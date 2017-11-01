package com.example.roystonbehzhiyang.parkr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.roystonbehzhiyang.parkr.pojo.HDBParking;
import com.example.roystonbehzhiyang.parkr.pojo.ShoppingParking;

import io.realm.Realm;
import io.realm.RealmResults;

public class FavouriteActivity extends AppCompatActivity implements FavouriteHDBAdapter.FavouriteAdapterOnClickHandler,
        FavouriteShoppingAdapter.FavouriteShoppingAdapterOnClickHandler{

    private Realm realm;

    private final String TAG = FavouriteActivity.class.getSimpleName();

    private RealmResults<HDBParking> myHDBParking;

    private RealmResults<ShoppingParking> mShoppingParking;

    public final static String HDB_PARKING_LOT_FAVOURITE = "HDB_PARKING_LOT_FAVOURITE";

    public final static String SHOPPING_PARKING_LOT_FAVOURITE = "SHOPPING_PARKING_LOT_FAVOURITE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        realm = Realm.getDefaultInstance();

        myHDBParking = realm.where(HDBParking.class).findAll();

        FavouriteHDBAdapter mAdapter = new FavouriteHDBAdapter(this,this, myHDBParking);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.carpark_favourite_recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        mShoppingParking = realm.where(ShoppingParking.class).findAll();

        FavouriteShoppingAdapter mShoppingAdapter = new FavouriteShoppingAdapter(this,this, mShoppingParking);

        RecyclerView shoppingRecyclerView = (RecyclerView) findViewById(R.id.shopping_favourite_recycler_view);

        shoppingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        shoppingRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        shoppingRecyclerView.setAdapter(mShoppingAdapter);

    }

    @Override
    public void onClick(int id) {
        HDBParking mParkingLot = myHDBParking.get(id);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(HDB_PARKING_LOT_FAVOURITE, mParkingLot);
        startActivity(intent);
    }

    @Override
    public void onClickShopping(int id) {
        ShoppingParking mParkingLot = mShoppingParking.get(id);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(SHOPPING_PARKING_LOT_FAVOURITE, mParkingLot);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}

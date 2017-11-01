package com.example.roystonbehzhiyang.parkr;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.roystonbehzhiyang.parkr.pojo.ShoppingParking;

import io.realm.RealmResults;

/**
 * Created by roystonbehzhiyang on 22/10/17.
 */

public class FavouriteShoppingAdapter extends RecyclerView.Adapter<FavouriteShoppingAdapter.FavouriteShoppingViewHolder> {

    final private FavouriteShoppingAdapterOnClickHandler mClickHandler;
    private Context mContext;
    private RealmResults<ShoppingParking> mParkingLot;
    private final String TAG = FavouriteShoppingAdapter.class.getSimpleName();

    public interface FavouriteShoppingAdapterOnClickHandler {
        void onClickShopping(int id);
    }

    public FavouriteShoppingAdapter(Context context, FavouriteShoppingAdapterOnClickHandler clickHandler, RealmResults<ShoppingParking> parkingLots) {
        mContext = context;
        mClickHandler = clickHandler;
        mParkingLot = parkingLots;
    }

    @Override
    public FavouriteShoppingViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater
                .from(mContext)
                .inflate(R.layout.favourite_shopping_parking, viewGroup, false);

        view.setFocusable(true);

        return new FavouriteShoppingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavouriteShoppingViewHolder holder, int position) {
        ShoppingParking parkingLot = mParkingLot.get(position);
        holder.favouriteArea.setText("Area: " + parkingLot.getmArea());
        holder.favouriteDevelopment.setText("Shopping Centre: " + parkingLot.getmDevelopment());
        holder.favouriteAvailableLots.setText("Lots Available: " + parkingLot.getmLots_available());
        //Log.d(TAG,"Position: " + position);
    }

    @Override
    public int getItemCount() {
        //Log.d(TAG,"Size: " + mParkingLot.size());
        return mParkingLot.size();
    }


    class FavouriteShoppingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView favouriteArea;

        final TextView favouriteDevelopment;

        final TextView favouriteAvailableLots;


        FavouriteShoppingViewHolder(View view) {
            super(view);

            favouriteArea = (TextView) view.findViewById(R.id.favourite_shopping_area);

            favouriteDevelopment = (TextView) view.findViewById(R.id.favourite_shopping_name);

            favouriteAvailableLots = (TextView) view.findViewById(R.id.favourite_shopping_lots_available);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getLayoutPosition();
            mClickHandler.onClickShopping(position);
            Log.d(TAG,"Clicked Position: " + position);
        }
    }

}

package com.example.roystonbehzhiyang.parkr;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.roystonbehzhiyang.parkr.pojo.HDBParking;

import io.realm.RealmResults;

/**
 * Created by roystonbehzhiyang on 22/10/17.
 */

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.FavouriteViewHolder> {

    final private FavouriteAdapterOnClickHandler mClickHandler;
    private Context mContext;
    private RealmResults<HDBParking> mParkingLot;
    private final String TAG = FavouriteAdapter.class.getSimpleName();

    public interface FavouriteAdapterOnClickHandler {
        void onClick(int id);
    }

    public FavouriteAdapter(Context context, FavouriteAdapterOnClickHandler clickHandler, RealmResults<HDBParking> parkingLots) {
        mContext = context;
        mClickHandler = clickHandler;
        mParkingLot = parkingLots;
    }

    @Override
    public FavouriteViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater
                .from(mContext)
                .inflate(R.layout.favourite_hdb_parking, viewGroup, false);

        view.setFocusable(true);

        return new FavouriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavouriteViewHolder holder, int position) {
        HDBParking parkingLot = mParkingLot.get(position);
        holder.favouriteAddress.setText("Address: " + parkingLot.getmAddress());
        holder.favouriteTotalLots.setText("Total Lots: " + parkingLot.getmTotal_lots_available());
        holder.favouriteAvailableLots.setText("Lots Available: " + parkingLot.getmLots_available());
        //Log.d(TAG,"Position: " + position);
    }

    @Override
    public int getItemCount() {
        //Log.d(TAG,"Size: " + mParkingLot.size());
        return mParkingLot.size();
    }


    class FavouriteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView favouriteAddress;

        final TextView favouriteTotalLots;

        final TextView favouriteAvailableLots;


        FavouriteViewHolder(View view) {
            super(view);

            favouriteAddress = (TextView) view.findViewById(R.id.favourite_address);

            favouriteTotalLots = (TextView) view.findViewById(R.id.favourite_total_lots);

            favouriteAvailableLots = (TextView) view.findViewById(R.id.favourite_lots_available);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getLayoutPosition();
            mClickHandler.onClick(position);
            Log.d(TAG,"Clicked Position: " + position);
        }
    }

}

package com.example.roystonbehzhiyang.parkr;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.roystonbehzhiyang.parkr.pojo.Incident;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ReportIncidentFragment extends Fragment implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private DatabaseReference mDatabase;
    private Location mLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    SharedPreferences pref;
    String loggedInUserUID;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mDatabase = FirebaseDatabase.getInstance().getReference();

        pref = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        loggedInUserUID = pref.getString("uuid","");

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(super.getContext()).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        }

        return inflater.inflate(R.layout.fragment_report_incident, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mGoogleApiClient.connect();
        ImageButton roadworks = (ImageButton) getActivity().findViewById(R.id.btnRoadWorks);
        roadworks.setOnClickListener(this);
        ImageButton roadBlock = (ImageButton) getActivity().findViewById(R.id.btnRoadBlock);
        roadBlock.setOnClickListener(this);
        ImageButton accident = (ImageButton) getActivity().findViewById(R.id.btnAccident);
        accident.setOnClickListener(this);
        ImageButton chainAccident = (ImageButton) getActivity().findViewById(R.id.btnChainAccident);
        chainAccident.setOnClickListener(this);
        ImageButton trafficJam = (ImageButton) getActivity().findViewById(R.id.btnTrafficJam);
        trafficJam.setOnClickListener(this);
        ImageButton treeFall = (ImageButton) getActivity().findViewById(R.id.btnTreeFall);
        treeFall.setOnClickListener(this);
        ImageButton oilSpill = (ImageButton) getActivity().findViewById(R.id.btnOilSpill);
        oilSpill.setOnClickListener(this);
        ImageButton flood = (ImageButton) getActivity().findViewById(R.id.btnFlood);
        flood.setOnClickListener(this);
    }

    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.btnRoadWorks:
                sendIncident("Road Works");
                break;
            case R.id.btnRoadBlock:
                sendIncident("Road Block");
                break;
            case R.id.btnAccident:
                sendIncident("Accident");
                break;
            case R.id.btnChainAccident:
                sendIncident("Chain Accident");
                break;
            case R.id.btnTrafficJam:
                sendIncident("Traffic Jam");
                break;
            case R.id.btnTreeFall:
                sendIncident("Tree Fall");
                break;
            case R.id.btnOilSpill:
                sendIncident("Oil Spill");
                break;
            case R.id.btnFlood:
                sendIncident("Flood");
                break;
        }
    }

    private void sendIncident(String incidentType) {
        if (ActivityCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLocation != null) {
            String key = mDatabase.child("Incident").push().getKey();
            Incident incident;
            switch(incidentType){
                case "Road Block":
                    incident = new Incident(key,mLocation.getLatitude(),mLocation.getLongitude(),"Road Block",loggedInUserUID);
                    mDatabase.child("Incident").child(key).setValue(incident);
                    break;
                case "Heavy Traffic":
                    incident = new Incident(key,mLocation.getLatitude(),mLocation.getLongitude(),"Heavy Traffic",loggedInUserUID);
                    mDatabase.child("Incident").child(key).setValue(incident);
                    break;
                case "Road Works":
                    incident = new Incident(key,mLocation.getLatitude(),mLocation.getLongitude(),"Road Works",loggedInUserUID);
                    mDatabase.child("Incident").child(key).setValue(incident);
                    break;
                case "Accident":
                    incident = new Incident(key,mLocation.getLatitude(),mLocation.getLongitude(),"Accident",loggedInUserUID);
                    mDatabase.child("Incident").child(key).setValue(incident);
                    break;
                case "Tree Fall":
                    incident = new Incident(key,mLocation.getLatitude(),mLocation.getLongitude(),"Tree Fall",loggedInUserUID);
                    mDatabase.child("Incident").child(key).setValue(incident);
                    break;
                case "Chain Accident":
                    incident = new Incident(key,mLocation.getLatitude(),mLocation.getLongitude(),"Chain Accident",loggedInUserUID);
                    mDatabase.child("Incident").child(key).setValue(incident);
                    break;
                case "Flood":
                    incident = new Incident(key,mLocation.getLatitude(),mLocation.getLongitude(),"Flood",loggedInUserUID);
                    mDatabase.child("Incident").child(key).setValue(incident);
                    break;
                case "Oil Spill":
                    incident = new Incident(key,mLocation.getLatitude(),mLocation.getLongitude(),"Oil Spill",loggedInUserUID);
                    mDatabase.child("Incident").child(key).setValue(incident);
                    break;
            }

        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;
    }
}
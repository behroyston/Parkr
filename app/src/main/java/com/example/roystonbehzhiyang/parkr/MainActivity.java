package com.example.roystonbehzhiyang.parkr;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.roystonbehzhiyang.parkr.data.ParkrAPIInterface;
import com.example.roystonbehzhiyang.parkr.data.RetrofitClient;
import com.example.roystonbehzhiyang.parkr.pojo.AsyncCompletedEvent;
import com.example.roystonbehzhiyang.parkr.pojo.ParkingLot;
import com.example.roystonbehzhiyang.parkr.pojo.ParkingLotResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * An activity that displays a map showing the place at the device's current location.
 */
public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;

    // The entry points to the Places API.
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location (Singapore) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(1.3521, 103.8198);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // The place which the user selects through the AutoComplete form.
    private Place mSelectedPlace;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    // Key for passing data to details.
    public static final String PARKING_LOT_DETAILS = "parking_lot_details";

    // Context
    private Context mContext;

    // ArrayList to store dummy data.
    private ArrayList<ParkingLot> myParkingLots = new ArrayList<>();

    // Hashmap to contain the data and map to my parking lots.
    private HashMap<Marker, ParkingLot> hashMap = new HashMap<Marker, ParkingLot>();

    // APIInterface
    private ParkrAPIInterface myAPIInterface;

    // URL
    private String myURL = "http://5994b7f7.ngrok.io/";

    private BottomSheetBehavior bottomSheetBehavior;
    @BindView (R.id.bottom_sheet_title)
    TextView bottom_sheet_title;
    @BindView (R.id.bottom_sheet_lot_type)
    TextView bottom_sheet_lot_type;
    @BindView (R.id.bottom_sheet_total)
    TextView bottom_sheet_total;
    @BindView (R.id.bottom_sheet_lots)
    TextView bottom_sheet_lots;
    @BindView (R.id.favourite)
    ImageButton favourite;

    // Realm Instance
    private Realm realm;
    
    // Check if started from FavouriteActivity to mark out that favourited parking spot.
    private boolean fromFavourite;
    // ParkingLot from favourite
    private ParkingLot mFavouriteParkingLot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Registering this activity to listen for asynctask call.
        EventBus.getDefault().register(this);

        mContext = this;

        // Get a Realm instance for this thread.
        realm = Realm.getDefaultInstance();

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // fromFavourite is always false at start unless started from Favouriteactivity.
        fromFavourite = false;
        
        // Retrieve parcel from FavouriteActivity, if any
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mFavouriteParkingLot = extras.getParcelable(FavouriteActivity.PARKING_LOT_FAVOURITE);
            if (mFavouriteParkingLot != null)
                fromFavourite = true;
        }

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet));
        bottomSheetBehavior.setHideable(true);//Important to add
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        setBottomSheet();

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName());
                mSelectedPlace = place;

                // LatLng of selected place.
                LatLng mLatLng = mSelectedPlace.getLatLng();

                // Name of selected place.
                String mName = mSelectedPlace.getName().toString();

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        mLatLng, DEFAULT_ZOOM));
                mMap.addMarker(new MarkerOptions().position(mLatLng)
                        .title(mName));
                Log.i(TAG, "Lat: " + mLatLng.latitude + "\nLong: " + mLatLng.longitude);
                setDummyData();
                setDataPoints();
                //apiCall(mLatLng.latitude, mLatLng.longitude);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

    /**
     * makes apicall and set the markers to the point and adding the data to the hashmap
     */
    private void setDataPoints() {
        // apicall
        // always clear the data in the hashmap so it only contains the most recent 5 datapoints.
        hashMap.clear();
        for (ParkingLot mParkingLot : myParkingLots) {
            LatLng parkingLatLng = new LatLng(mParkingLot.getmLat(),mParkingLot.getmLon());
            Marker marker = setMarkers((parkingLatLng),(mParkingLot.getmCarpark_no()));
            Log.d(TAG, mParkingLot.getmCarpark_no());
            // each marker is mapped to the parking lot to show the data later on.
            hashMap.put(marker,mParkingLot);
        }
    }

    private Marker setMarkers(LatLng latlng, String title){
        if (mMap != null) {
            Marker marker = mMap.addMarker(new MarkerOptions().position(latlng).title(title));
            return marker;
        }
        return null;
    }

    /**
     * apicall to retrieve the top 5 nearest locations to the user
     */
    private void apiCall(double lat, double lon) {
        myAPIInterface = RetrofitClient.getClient(myURL).create(ParkrAPIInterface.class);
        Call<ParkingLotResult> call = myAPIInterface.getParkingLots(lat,lon);
        call.enqueue(new Callback<ParkingLotResult>() {
            @Override
            public void onResponse(Call<ParkingLotResult> call, Response<ParkingLotResult> response) {
                ParkingLotResult results = response.body();
                if (results != null) {
                    Log.i(TAG, "IT WORKED!");
                    ArrayList<ParkingLot> parkingLots = results.parkingLots;
                    myParkingLots = parkingLots;
                    EventBus.getDefault().post(new AsyncCompletedEvent());
                    for (ParkingLot mParkingLot : myParkingLots) {
                        Log.i(TAG,"Lat: " + mParkingLot.getmLat() + "\nLon: " + mParkingLot.getmLon());
                    }
                }
            }
            @Override
            public void onFailure(Call<ParkingLotResult> call, Throwable t) {
                
                Log.e(TAG,"FAILED!");
            }
        });
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    /**
     * Sets up the options menu.
     * @param menu The options menu.
     * @return Boolean.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        /* Use AppCompatActivity's method getMenuInflater to get a handle on the main_menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our main_menu layout to this main_menu */
        inflater.inflate(R.menu.main_menu, menu);
        /* Return true so that the main_menu is displayed in the Toolbar */
        return true;

    }

    /**
     * Handles a click on the menu option to get a place.
     * @param item The menu item to handle.
     * @return Boolean.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_view_favourite)
        {
            Intent intent = new Intent(this, FavouriteActivity.class);
            startActivity(intent);
        }else if(id == R.id.action_viewProfile){
            Intent intent = new Intent(this, UserProfileActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap map) {

        mMap = map;

        if (mMap != null) {
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    ParkingLot parkingLot = hashMap.get(marker);
                    Log.e(TAG,"Clicked!");
                    if (parkingLot != null) {
                        Log.e(TAG,"Not Null!");
                        Intent intent = new Intent(mContext, ParkingLotDetails.class);
                        intent.putExtra(PARKING_LOT_DETAILS, parkingLot);
                        mContext.startActivity(intent);
                    }
                }
            });
        }

        mMap.setOnMarkerClickListener(this);

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map, if !fromFavourite
        getLocation();
    }


    /**
     * Gets the current location of the device or the Favourite ParkingLot's Location, and positions the map's camera.
     */
    private void getLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && !fromFavourite) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        } 
                        else if (fromFavourite) {
                            LatLng latLng = new LatLng(mFavouriteParkingLot.getmLat(),mFavouriteParkingLot.getmLon());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    latLng, DEFAULT_ZOOM));
                            Marker marker = setMarkers(latLng, mFavouriteParkingLot.getmCarpark_no());
                            hashMap.put(marker,mFavouriteParkingLot);
                        }
                        else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void setDummyData() {
            myParkingLots.add(new ParkingLot("Y13", "BLK 201/202, 207/212 YISHUN STREET 21", 1.431208,103.837865, "SURFACE CAR PARK",
            "ELECTRONIC PARKING","WHOLE DAY","NO","YES", "200", "10", "C"));
            myParkingLots.add(new ParkingLot("Y14", "BLK 203/206, 213/226 YISHUN STREET 21", 1.432667,103.835661, "SURFACE CAR PARK",
                    "COUPON PARKING","WHOLE DAY","SUN & PH FR 7AM-10.30PM","YES", "150", "20", "C"));
            myParkingLots.add(new ParkingLot("Y5", "BLK 144/199 YISHUN STREET 11", 1.431108,103.832108, "SURFACE CAR PARK",
                    "ELECTRONIC PARKING","7AM-10.30PM","SUN & PH FR 7AM-10.30PM","NO", "100", "20", "C"));
            myParkingLots.add(new ParkingLot("Y6", "BLK 150/161 YISHUN STREET 11", 1.432516 ,103.834194, "SURFACE CARPARK",
                    "ELECTRONIC PARKING","7AM-10.30PM","SUN & PH FR 7AM-10.30PM","NO", "50", "30", "C"));
            myParkingLots.add(new ParkingLot("Y9", " BLK 747/752 YISHUN STREET 72", 1.427824,103.834013, "SURFACE CAR PARK",
                    "ELECTRONIC PARKING","WHOLE DAY","SUN & PH FR 7AM-10.30PM","YES", "50", "50", "C"));
    }


    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    // This method will be called when a AsyncCompletedEvent is posted
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(AsyncCompletedEvent event){
        // your implementation
        setDataPoints();
        //Toast.makeText(this, "Hello!", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        final ParkingLot mParkingLot = hashMap.get(marker);
        if (mParkingLot != null) {
            bottom_sheet_title.setText("Address: " + mParkingLot.getmAddress());
            bottom_sheet_lots.setText("Total Lots: " + mParkingLot.getmLots_available());
            bottom_sheet_total.setText("Current Lots Available: " + mParkingLot.getmTotal_lots_available());
            bottom_sheet_lot_type.setText("Lots Type: " + mParkingLot.getmLots_type());
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

            // check if it has already been favourited(if it is, set the full heartshape)
            if (favouriteExists(mParkingLot)) {
                favourite.setImageResource(R.drawable.ic_favorite_black_24dp);
            }
            else
            {
                favourite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            }

            favourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!favouriteExists(mParkingLot)){
                        addFavourite(mParkingLot);
                        favourite.setImageResource(R.drawable.ic_favorite_black_24dp);
                    }
                    else
                    {
                        removeFavourite(mParkingLot);
                        favourite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    }
                }
            });
        }
        marker.showInfoWindow();

        return true;
    }

    private boolean favouriteExists(ParkingLot parkingLot)
    {
//        // Query Realm for all dogs younger than 2 years old
//        final RealmResults<ParkingLot> parkingLots = realm.where(ParkingLot.class).findAll();
//        if (parkingLots.contains(parkingLot)) {
//            return true;
//        }
        ParkingLot mParkingLot = realm.where(ParkingLot.class).equalTo("mCarpark_no", parkingLot.getmCarpark_no()).findFirst();

        return (mParkingLot!=null);
    }

    private void addFavourite(ParkingLot parkingLot){
        realm.beginTransaction();
        realm.insert(parkingLot);
        realm.commitTransaction();
        Log.d(TAG,"Favourite added!");
    }

    private void removeFavourite(ParkingLot parkingLot){
        realm.beginTransaction();
        ParkingLot mParkingLot = realm.where(ParkingLot.class).equalTo("mCarpark_no", parkingLot.getmCarpark_no()).findFirst();
        mParkingLot.deleteFromRealm();
        realm.commitTransaction();
        Log.d(TAG,"Favourite removed!");
    }

    private void setBottomSheet() {
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        Log.e("Bottom Sheet Behaviour", "STATE_COLLAPSED");
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        Log.e("Bottom Sheet Behaviour", "STATE_DRAGGING");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        Log.e("Bottom Sheet Behaviour", "STATE_EXPANDED");
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        Log.e("Bottom Sheet Behaviour", "STATE_HIDDEN");
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        Log.e("Bottom Sheet Behaviour", "STATE_SETTLING");
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}

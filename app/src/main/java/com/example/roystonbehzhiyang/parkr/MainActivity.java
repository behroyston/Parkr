package com.example.roystonbehzhiyang.parkr;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.roystonbehzhiyang.parkr.data.ParkrAPIInterface;
import com.example.roystonbehzhiyang.parkr.data.RetrofitClient;
import com.example.roystonbehzhiyang.parkr.eventbus.AsyncCompletedEvent;
import com.example.roystonbehzhiyang.parkr.eventbus.ShoppingAsyncCompletedEvent;
import com.example.roystonbehzhiyang.parkr.pojo.HDBParking;
import com.example.roystonbehzhiyang.parkr.pojo.HDBParkingLotResult;
import com.example.roystonbehzhiyang.parkr.pojo.Incident;
import com.example.roystonbehzhiyang.parkr.pojo.ShoppingParking;
import com.example.roystonbehzhiyang.parkr.pojo.ShoppingParkingLotResult;
import com.facebook.login.Login;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    public static final String HDB_PARKING_LOT_DETAILS = "HDB_PARKING_LOT_DETAILS";

    // Key for passing data to details.
    public static final String SHOPPING_PARKING_LOT_DETAILS = "SHOPPING_PARKING_LOT_DETAILS";

    // Context
    private Context mContext;

    // ArrayList to store dummy data.
    private ArrayList<HDBParking> myParkingLots = new ArrayList<>();

    private ArrayList<ShoppingParking> myShoppingLots = new ArrayList<>();

    // Hashmap to contain the HDBParking and map to my parking lots.
    private HashMap<Marker, HDBParking> hashMap = new HashMap<Marker, HDBParking>();

    // Hashmap to contain ShoppingParking and map to the shopping lots;
    private HashMap<Marker, ShoppingParking> shoppingMap = new HashMap<Marker,ShoppingParking>();

    // APIInterface
    private ParkrAPIInterface myAPIInterface;

    // URL
    private String myURL = "http://4bf95009.ngrok.io/";

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
    @BindView(R.id.view_details)
    Button view_details;

    // Realm Instance
    private Realm realm;

    // Check if started from FavouriteActivity to mark out that favourited parking spot.
    private boolean fromFavourite;
    // HDBParking from favourite
    private HDBParking mFavouriteHDBParkingLot;

    private ShoppingParking mFavouriteShoppingParkingLot;

    private DatabaseReference incidentRef;
    private HashMap<String, Marker> mHashMap = new HashMap<String,Marker>();
    SharedPreferences pref;
    SharedPreferences.Editor editor;

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
            mFavouriteHDBParkingLot = extras.getParcelable(FavouriteActivity.HDB_PARKING_LOT_FAVOURITE);
            if (mFavouriteHDBParkingLot != null)
                fromFavourite = true;
            else {
                mFavouriteShoppingParkingLot = extras.getParcelable(FavouriteActivity.SHOPPING_PARKING_LOT_FAVOURITE);
                if (mFavouriteShoppingParkingLot != null)
                    fromFavourite = true;
            }
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

        incidentRef = FirebaseDatabase.getInstance().getReference().child("Incident");

        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setCountry("SG")
                .build();
        autocompleteFragment.setFilter(typeFilter);
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
                setDataPointsForHDB();
                setDataPointsForShopping();
                //apiCall(mLatLng.latitude, mLatLng.longitude);
                //apiShoppingCall(mLatLng.latitude, mLatLng.longitude);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
        FloatingActionButton btnIncident = (FloatingActionButton)findViewById(R.id.fab);
        btnIncident.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                ReportIncidentFragment incidentFragment = new ReportIncidentFragment();
                fragmentTransaction.add(R.id.incidentOverlay,incidentFragment);
                fragmentTransaction.commit();
            }
        });

    }

    protected void onStart(){
        super.onStart();

        ChildEventListener incidentChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("Child:Incident Added", dataSnapshot.getKey());


                Incident incident = dataSnapshot.getValue(Incident.class);
                LatLng incidentLatLng = new LatLng(incident.latitude, incident.longitude);
                Marker marker;

                android.app.FragmentManager manager = getFragmentManager();
                manager.popBackStack();

                /*LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.custom_toast, (ViewGroup)findViewById(R.id.custom_toast_container));
                TextView incidentType = (TextView)layout.findViewById(R.id.lblRoadIncident);
                TextView icidentLct = (TextView)layout.findViewById(R.id.lblIncidentLocation);
                ImageView incidentIcon = (ImageView)layout.findViewById(R.id.incidentIcon);

                Toast incidentToast = new Toast(getApplicationContext());
                incidentToast.setGravity(Gravity.CENTER_VERTICAL,0,0);
                incidentToast.setDuration(Toast.LENGTH_SHORT);
                incidentToast.setView(layout);*/

                switch(incident.incidentType){
                    case "Road Block":
                        marker = mMap.addMarker(new MarkerOptions().position(incidentLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_roadblock)));
                        mHashMap.put(incident.uid,marker);
                        break;
                    case "Heavy Traffic":
                        marker = mMap.addMarker(new MarkerOptions().position(incidentLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_trafficjam)));
                        mHashMap.put(incident.uid,marker);
                        break;
                    case "Road Works":
                        marker = mMap.addMarker(new MarkerOptions().position(incidentLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_roadworks)));
                        mHashMap.put(incident.uid,marker);
                        break;
                    case "Accident":
                        marker = mMap.addMarker(new MarkerOptions().position(incidentLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_accident)));
                        mHashMap.put(incident.uid,marker);
                        break;
                    case "Tree Fall":
                        marker = mMap.addMarker(new MarkerOptions().position(incidentLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_treefall)));
                        mHashMap.put(incident.uid,marker);
                        break;
                    case "Chain Accident":
                        marker = mMap.addMarker(new MarkerOptions().position(incidentLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_chainaccident)));
                        mHashMap.put(incident.uid,marker);
                        break;
                    case "Flood":
                        marker = mMap.addMarker(new MarkerOptions().position(incidentLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_flood)));
                        mHashMap.put(incident.uid,marker);
                        break;
                    case "Oil Spill":
                        marker = mMap.addMarker(new MarkerOptions().position(incidentLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_oilspill)));
                        mHashMap.put(incident.uid,marker);
                        break;
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("Child:Incident Removed", dataSnapshot.getKey());
                Marker marker = mHashMap.get(dataSnapshot.getKey());
                if(marker != null){
                    mHashMap.remove(dataSnapshot.getKey());
                    marker.remove();
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        //incidentRef.addListenerForSingleValueEvent(incidentChangeListerner);
        incidentRef.addChildEventListener(incidentChildListener);
    }

    /**
     * makes apicall and set the markers to the point and adding the data to the hashmap
     */
    private void setDataPointsForHDB() {
        if (myParkingLots!= null) {
            // always clear the data in the hashmap so it only contains the most recent 5 datapoints.
            hashMap.clear();
            for (HDBParking mParkingLot : myParkingLots) {
                LatLng parkingLatLng = new LatLng(mParkingLot.getmLat(), mParkingLot.getmLon());
                Marker marker = setMarkers((parkingLatLng), (mParkingLot.getmCarpark_no()));
                marker.setIcon(bitmapDescriptorFromVector(this, R.drawable.ic_local_parking_black_24dp));
                Log.d(TAG, mParkingLot.getmCarpark_no());
                // each marker is mapped to the parking lot to show the data later on.
                hashMap.put(marker, mParkingLot);
            }
        }
    }

    private void setDataPointsForShopping(){
        if (myShoppingLots != null) {
            // always clear the data in the hashmap so it only contains the most recent shopingPoints.
            shoppingMap.clear();
            for (ShoppingParking mParkingLot : myShoppingLots) {
                LatLng parkingLatLng = new LatLng(mParkingLot.getmLat(), mParkingLot.getmLon());
                Marker marker = setMarkers((parkingLatLng), (mParkingLot.getmCarpark_no()));
                //marker.setIcon(bitmapDescriptorFromVector(this, R.drawable.ic_local_parking_black_24dp));
                Log.d(TAG, mParkingLot.getmCarpark_no());
                // each marker is mapped to the parking lot to show the data later on.
                shoppingMap.put(marker, mParkingLot);
            }
        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
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
        Call<HDBParkingLotResult> call = myAPIInterface.getParkingLots(lat,lon);
        call.enqueue(new Callback<HDBParkingLotResult>() {
            @Override
            public void onResponse(Call<HDBParkingLotResult> call, Response<HDBParkingLotResult> response) {
                HDBParkingLotResult results = response.body();
                if (results != null) {
                    Log.i(TAG, "IT WORKED!");
                    ArrayList<HDBParking> parkingLots = results.parkingLots;
                    myParkingLots = parkingLots;
                    EventBus.getDefault().post(new AsyncCompletedEvent());
                    for (HDBParking mParkingLot : myParkingLots) {
                        Log.i(TAG,"Lat: " + mParkingLot.getmLat() + "\nLon: " + mParkingLot.getmLon());
                    }
                }
            }
            @Override
            public void onFailure(Call<HDBParkingLotResult> call, Throwable t) {

                Log.e(TAG,"FAILED!");
            }
        });
    }

    private void apiShoppingCall(double lat, double lon) {
        myAPIInterface = RetrofitClient.getClient(myURL).create(ParkrAPIInterface.class);
        Call<ShoppingParkingLotResult> call = myAPIInterface.getShoppingParkingLots(lat,lon);
        call.enqueue(new Callback<ShoppingParkingLotResult>() {
            @Override
            public void onResponse(Call<ShoppingParkingLotResult> call, Response<ShoppingParkingLotResult> response) {
                ShoppingParkingLotResult results = response.body();
                if (results != null) {
                    Log.i(TAG, "Shopping Worked");
                    ArrayList<ShoppingParking> parkingLots = results.parkingLots;
                    myShoppingLots = parkingLots;
                    EventBus.getDefault().post(new ShoppingAsyncCompletedEvent());
                    for (ShoppingParking mParkingLot : myShoppingLots) {
                        Log.i(TAG,"Lat: " + mParkingLot.getmLat() + "\nLon: " + mParkingLot.getmLon());
                    }
                }
            }
            @Override
            public void onFailure(Call<ShoppingParkingLotResult> call, Throwable t) {

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
        }else if(id == R.id.action_logout){
            FirebaseAuth.getInstance().signOut();
            pref = pref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            editor = pref.edit();
            editor.remove("uuid");
            editor.commit();
            Intent intent = new Intent(this, LoginActivity.class);
        }
        return super.onOptionsItemSelected(item);
    }

    private void viewDetails(Marker marker){
        HDBParking parkingLot = hashMap.get(marker);
        Log.e(TAG,"Clicked!");
        if (parkingLot != null) {
            Log.e(TAG,"Not Null!");
            Intent intent = new Intent(mContext, ParkingLotDetails.class);
            intent.putExtra(HDB_PARKING_LOT_DETAILS, parkingLot);
            mContext.startActivity(intent);
        }
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
                    viewDetails(marker);
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
                            if (mFavouriteHDBParkingLot != null) {
                                LatLng latLng = new LatLng(mFavouriteHDBParkingLot.getmLat(), mFavouriteHDBParkingLot.getmLon());
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        latLng, DEFAULT_ZOOM));
                                Marker marker = setMarkers(latLng, mFavouriteHDBParkingLot.getmCarpark_no());
                                hashMap.put(marker,mFavouriteHDBParkingLot);
                            }
                            else if (mFavouriteShoppingParkingLot != null)
                            {
                                LatLng latLng = new LatLng(mFavouriteShoppingParkingLot.getmLat(), mFavouriteShoppingParkingLot.getmLon());
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        latLng, DEFAULT_ZOOM));
                                Marker marker = setMarkers(latLng, mFavouriteShoppingParkingLot.getmCarpark_no());
                                shoppingMap.put(marker,mFavouriteShoppingParkingLot);
                            }
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
            myParkingLots.add(new HDBParking("Y13",  1.431208,103.837865, "10", "BLK 201/202, 207/212 YISHUN STREET 21","SURFACE CAR PARK",
            "ELECTRONIC PARKING","WHOLE DAY","NO","YES", "200", "C"));
            myParkingLots.add(new HDBParking("Y14",  1.432667,103.835661, "20","BLK 203/206, 213/226 YISHUN STREET 21","SURFACE CAR PARK",
                    "COUPON PARKING","WHOLE DAY","SUN & PH FR 7AM-10.30PM","YES", "150", "C"));
            myParkingLots.add(new HDBParking("Y5", 1.431108,103.832108, "20","BLK 144/199 YISHUN STREET 11","SURFACE CAR PARK",
                    "ELECTRONIC PARKING","7AM-10.30PM","SUN & PH FR 7AM-10.30PM","NO", "100", "C"));
            myParkingLots.add(new HDBParking("Y6", 1.432516 ,103.834194, "30","BLK 150/161 YISHUN STREET 11","SURFACE CARPARK",
                    "ELECTRONIC PARKING","7AM-10.30PM","SUN & PH FR 7AM-10.30PM","NO", "50", "C"));
            myParkingLots.add(new HDBParking("Y9", 1.427824,103.834013,  "50","BLK 747/752 YISHUN STREET 72", "SURFACE CAR PARK",
                    "ELECTRONIC PARKING","WHOLE DAY","SUN & PH FR 7AM-10.30PM","YES", "50", "C"));
            myShoppingLots.add(new ShoppingParking("N1", 1.443824,103.834013, "50", "Northpoint", "Frasers"));
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
        if (myParkingLots!= null) {
            setDataPointsForHDB();
        }
        //Toast.makeText(this, "Hello!", Toast.LENGTH_LONG).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ShoppingAsyncCompletedEvent event){
        // your implementation
        if (myShoppingLots!= null) {
            setDataPointsForShopping();
        }
        //Toast.makeText(this, "Hello!", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

        if (hashMap.containsKey(marker)){
            HDBParking mParkingLot = hashMap.get(marker);
            setBottomViewHDB(mParkingLot);
        }
        else {
            ShoppingParking mParkingLot = shoppingMap.get(marker);
            setBottomViewHDB(mParkingLot);
        }
        marker.showInfoWindow();
        // setButton

        return true;
    }

    private void setBottomViewHDB(final HDBParking parkingLot){
        if (parkingLot != null) {
            bottom_sheet_total.setVisibility(View.VISIBLE);
            bottom_sheet_lot_type.setVisibility(View.VISIBLE);
            bottom_sheet_title.setText("Address: " + parkingLot.getmAddress());
            bottom_sheet_lots.setText("Total Lots: " + parkingLot.getmLots_available());
            bottom_sheet_total.setText("Current Lots Available: " + parkingLot.getmTotal_lots_available());
            bottom_sheet_lot_type.setText("Lots Type: " + parkingLot.getmLots_type());
            view_details.setVisibility(View.VISIBLE);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

            // check if it has already been favourited(if it is, set the full heartshape)
            if (favouriteHDBExists(parkingLot)) {
                favourite.setImageResource(R.drawable.ic_favorite_black_24dp);
            }
            else
            {
                favourite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            }

            favourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!favouriteHDBExists(parkingLot)){
                        addFavourite(parkingLot);
                        favourite.setImageResource(R.drawable.ic_favorite_black_24dp);
                    }
                    else
                    {
                        removeFavourite(parkingLot);
                        favourite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    }
                }
            });
            view_details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ParkingLotDetails.class);
                    intent.putExtra(HDB_PARKING_LOT_DETAILS, parkingLot);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    private void setBottomViewHDB(final ShoppingParking parkingLot){
        if (parkingLot != null) {
            bottom_sheet_total.setVisibility(View.INVISIBLE);
            bottom_sheet_lot_type.setVisibility(View.INVISIBLE);
            view_details.setVisibility(View.INVISIBLE);
            bottom_sheet_title.setText("Shopping Mall: " + parkingLot.getmDevelopment());
            bottom_sheet_lots.setText("Total Lots: " + parkingLot.getmLots_available());
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

            // check if it has already been favourited(if it is, set the full heartshape)
            if (favouriteHDBExists(parkingLot)) {
                favourite.setImageResource(R.drawable.ic_favorite_black_24dp);
            }
            else
            {
                favourite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            }

            favourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!favouriteHDBExists(parkingLot)){
                        addFavourite(parkingLot);
                        favourite.setImageResource(R.drawable.ic_favorite_black_24dp);
                    }
                    else
                    {
                        removeFavourite(parkingLot);
                        favourite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    }
                }
            });
        }
    }

    private boolean favouriteHDBExists(Object o)
    {
        if (o instanceof HDBParking) {
            HDBParking mParkingLot = (HDBParking) o;
            return (realm.where(HDBParking.class).equalTo("mCarpark_no", mParkingLot.getmCarpark_no()).findFirst() != null);
        }
        else if (o instanceof ShoppingParking) {
            ShoppingParking mShoppingLot = (ShoppingParking) o;
            return (realm.where(ShoppingParking.class).equalTo("mCarpark_no", mShoppingLot.getmCarpark_no()).findFirst() != null);
        }
        else
            return false;
    }

    private void addFavourite(Object o){
        realm.beginTransaction();

        if (o instanceof HDBParking) {
            HDBParking mParkingLot = (HDBParking) o;
            realm.insert(mParkingLot);
            Log.d(TAG,"Favourite Shopping added!");
        }
        else if (o instanceof ShoppingParking) {
            ShoppingParking mShoppingLot = (ShoppingParking) o;
            realm.insert(mShoppingLot);
            Log.d(TAG,"Favourite Shopping added!");

        }
        realm.commitTransaction();
    }

    private void removeFavourite(Object o){
        realm.beginTransaction();
        if (o instanceof HDBParking) {
            HDBParking mParkingLot = realm.where(HDBParking.class).equalTo("mCarpark_no", ((HDBParking)o).getmCarpark_no()).findFirst();
            mParkingLot.deleteFromRealm();
            Log.d(TAG,"Favourite HDB removed!");
        }
        else if (o instanceof ShoppingParking) {
            ShoppingParking mParkingLot = realm.where(ShoppingParking.class).equalTo("mCarpark_no", ((ShoppingParking)o).getmCarpark_no()).findFirst();
            mParkingLot.deleteFromRealm();
            Log.d(TAG,"Favourite Shopping removed!");

        }
        realm.commitTransaction();
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
}

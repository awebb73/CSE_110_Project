package com.group9.cse110project;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;
import java.util.ArrayList;

public class MapAdder extends AppCompatActivity implements Serializable, OnMapReadyCallback,
        GoogleMap.OnMarkerDragListener, LocationListener {

    private GoogleMap mMap;
    private ArrayList<LocationHolder> locationKeeper;
    private RatingBar ratingBar;
    //private Button fin;
    private static double lastLat =0;
    private static double lastLng =0;
    private static float stars = 0;
    private LatLng mark;
    private Marker pin;
    private static LocationManager locationManager;
    private static String bestProvider;
    private static Criteria criteria;
    private static Location location;
    private static LocationHolder holder = new LocationHolder(stars,lastLat,lastLng);

    //MyLocation
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //show error dialog if GooglePlayServices not available
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        setContentView(R.layout.activity_map_adder);

        // getting the array list from the original intent
        Intent i = getIntent();
        Log.d("awebb", "2 locationKeeper.size() value: " + locationKeeper.size());

        locationKeeper = i.getParcelableArrayListExtra("array_list");
        Log.d("awebb", "locationKeeper.size() value: " + locationKeeper.size());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // initializing the map
        mMap = mapFragment.getMap();

        Button fin = (Button) findViewById(R.id.button);
        fin.setEnabled(false);
        addListenerOnRatingBar();

        // onMapReady(mMap);

    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerDragListener(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        criteria = new Criteria();
        bestProvider = locationManager.getBestProvider(criteria, true);

        enableMyLocation();


        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);


        mMap.addMarker(new MarkerOptions().position(latLng).draggable(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

        // getting the intent here
        // look at this method after we push
		/*
		// Intent intent = getIntent();
        // LatLng current = intent.get("
        // Bundle tmp = intent.getBundleExtra("adder");
        // locationKeeper = tmp.getParcelableArrayList("object");
        // mark = intent.getParcelableExtra("current");
        mMap.addMarker(new MarkerOptions().locationKeeper(mark).draggable(true));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mark, 17));
        // ART -- Add a marker in Sydney and move the camera
        // LatLng ucsd = new LatLng(32.881145, -117.2374);
        // mMap.addMarker(new MarkerOptions().locationKeeper(ucsd).title("UCSD"));
        // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ucsd, 17));
		*/
    }

    @Override
    public void onMarkerDrag(Marker arg0){
        arg0.setDraggable(true);
    }

    @Override
    public void onMarkerDragEnd(Marker marker){
        mark = marker.getPosition();
        double lat = mark.latitude;
        double longi = mark.longitude;
        mark = marker.getPosition();
    }

    @Override
    public void onMarkerDragStart(Marker marker){}

    // ART --gathers the stars num data and can be pushed to the first intent
    public void fin(View view)
    {
        // getting the current location
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        // creating the location holder objects
        LocationHolder hold = new LocationHolder(stars, lat, lng);
        // pushing the object onto the vector list
        locationKeeper.add(hold);

        Intent i = new Intent(MapAdder.this, MapsActivity.class);
        i.putParcelableArrayListExtra("array_list", locationKeeper);
        Log.d("awebb", "4 locationKeeper.size() value: " + locationKeeper.size());
        startActivity(i);
		/* bundle implementation
        Intent back = new Intent(this, MapsActivity.class);
        Bundle a = new Bundle();
        //a.putSerializable("objects", locationKeeper);
        a.putParcelableArrayList("objects", locationKeeper);
        back.putExtra("bundle", a);
        startActivity(back);
		*/
    }

    // ART -- listens to the rating bar and actives he confirnation screen
    public void addListenerOnRatingBar() {
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        // ART -- if the value is changed
        // ART -- we allow the confirmation button to become activated
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                // this is the global stars variable which is a float
                stars = ratingBar.getRating();
                // this star variable is simpely for the
                // threshold check
                int star = (int)ratingBar.getRating();
                // ART -- command to see logcat output
                // Log.d("Checking Stars value: ", String.valueOf(stars));
                // ART -- if else block to only turn on the confirmation button as required
                if (star == 0) {
                    Button fin = (Button) findViewById(R.id.button);
                    fin.setEnabled(false);
                }
                else if (star > 0) {
                    Button fin = (Button) findViewById(R.id.button);
                    fin.setEnabled(true);
                }
            }
        });
    }
    // has the ability to change the map
    // when the GPS updates
    // same as the first activity
    @Override
    public void onLocationChanged(Location location) {
        // double latitude = location.getLatitude();
        // double longitude = location.getLongitude();
        // LatLng latLng = new LatLng(latitude, longitude);
        // mMap.addMarker(new MarkerOptions().position(latLng));
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        // mMap.animateCamera(CameraUpdateFactory.zoomTo(20));
        // if (location == null) return;
    }
    // location listener crap
    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

    private boolean isGooglePlayServicesAvailable()
    {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }
    // dont touch any of this stuff
    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            location = locationManager.getLastKnownLocation(bestProvider);
            locationManager.requestLocationUpdates(bestProvider, 2000, 0, this);
        }

    }

    //deals with incorrect permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE)
        {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION))
        {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        }
        else
        {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }



}
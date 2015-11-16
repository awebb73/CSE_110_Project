package com.group9.cse110project;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
//handle adding a pin on pin drop
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
            GoogleMap.OnMyLocationButtonClickListener{

    public final static String EXTRA_MESSAGE = "com.group9.cse110project.MESSAGE";
    private GoogleMap mMap;
    private MarkerOptions mark;
    ArrayList<LatLng> locationKeeper;
    //Location Coordinates
    GoogleApiClient mGoogleApiClient;
    double lastLat = 48.858207;
    double lastLng = 2.294386;


    //MyLocation
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;/**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        locationKeeper = new ArrayList<LatLng>();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //Location Coordinates
        buildGoogleApiClient();
    }

    //set up the location handler
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)                                                   //Location Coordinates
                .addOnConnectionFailedListener(this)                                            //Location Coordinates
                .build();
    }

    private void setUpMap(ArrayList<LatLng> a){
        int position = 0;
        for(;position < a.size(); position++){
            mMap.addMarker(new MarkerOptions().position(a.get(position)));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(a.get(position-1), 17));
    }

    //adding in a dialog box new MarkerOptions().position(latlng).title("").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEn/RED/BLUE).snippet("NameOfBathroom, Rating, Review)));
    //the snippet does not create a dialog box just data to see.android

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        enableMyLocation();
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        LocationManager locate = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        System.out.println(locate);
        Criteria crit = new Criteria();
        String provider = locate.getBestProvider(crit, true);
        mMap.setOnMapClickListener(this);
        // NULL check to see if intent is null
        // LatLng geisel = new LatLng(32.881145, -117.237394);

        if(getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey("bundle")) {
                Intent restore = getIntent();
                Bundle bun = new Bundle();
                bun = restore.getBundleExtra("bundle");
                locationKeeper = (ArrayList<LatLng>) bun.getSerializable("objects");
                setUpMap(locationKeeper);
            }
        }
        else {
            LatLng latLng = new LatLng(32.8800604, -117.2361968);
            mark = new MarkerOptions().position(latLng).title("geisel");
            //mMap.addMarker(mark);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        }
        // mMap.addMarker(new MarkerOptions().position(geisel).title("Geisel Library"));
       //  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(geisel, 17));
    }

    //MyLocation
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
            mMap.setMyLocationEnabled(true);
        }

    }

    @Override
    //deals with incorrect permissions
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    //Location Coordinates
    @Override
    public void onConnected(Bundle bundle) {

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            lastLat = mLastLocation.getLatitude();
            lastLng = mLastLocation.getLongitude();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    //My Location
    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }


    @Override
    public boolean onMarkerClick(Marker arg0) {
        arg0.showInfoWindow();
        return true;
    }
    @Override
    //handles adding in markers on the map
    public void onMapClick(LatLng latLng) {
        float zoomLevel = 16;
        this.mMap.addMarker(new MarkerOptions().position(latLng).title("restroom"));
        this.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
        locationKeeper.add(latLng);
    }
    public void sendMessage(View view){
        Intent intent = new Intent(this, MapAdder.class);
        //EditText tex = (EditText) findViewById(R.id.text);
        //String message = tex.getText().toString();
        //intent.putExtra(EXTRA_MESSAGE, message);

        Bundle bundle = new Bundle();
        bundle.putSerializable("object", locationKeeper);
        intent.putExtra("adder", bundle);
        startActivity(intent);
    }

    @Override
    public void onStart(){
        super.onStart();
    }
    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);

    }

    //this will handle restoring the google maps
    //doesn't work yet
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
    }

}

package com.group9.cse110project;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
//handle adding a pin on pin drop
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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


    private GoogleMap mMap;
    private MarkerOptions mark;
    private ArrayList<LocationHolder> locationKeeper;
    //Location Coordinates
    private GoogleApiClient mGoogleApiClient;
    private double lastLat = 48.858207;
    private double lastLng = 2.294386;
    private LatLng warrenLecture= new LatLng(32.8807907, -117.2343316);
    private LatLng physicsEBU = new LatLng(32.8811838, -117.2332927);
    private LatLng ebu2 = new LatLng(32.8814126, -117.2339695);
    private double threshold = 0.000656;
    private LocationManager locate;
    //comment to delete later........


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
        locationKeeper = new ArrayList<LocationHolder>();
        locate = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //Location Coordinates
        buildGoogleApiClient();
        onConnected(savedInstanceState);
    }


    //set up the location handler
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)                                                   //Location Coordinates
                .addOnConnectionFailedListener(this)                                            //Location Coordinates
                .build();
    }

    private double distanceFormula(double x1, double x2, double y1, double y2){
        return Math.sqrt(Math.abs(Math.pow((x1-x2), 2)) + Math.abs(Math.pow((y1-y2),2)));
    }

    //this has the geobox and the rating system setup.
    private void setUpMap(ArrayList<LocationHolder> a) {
        int avg = 500000;
        int star;
        int locate = -3;
        double value;
        for (int position = 0; position < a.size() - 1; position++) {
            value = distanceFormula(a.get(position).getLoc().latitude, a.get(a.size() - 1).getLoc().latitude,
                    a.get(position).getLoc().longitude, a.get(a.size() - 1).getLoc().longitude);
            if (value < avg && value <= threshold) {
                locate = position;
            }
        }

        if (locate !=-3) {
            a.get(locate).incrementCount();
            //divide by two because I avg on two counts.
            a.get(locate).setRating((a.get(locate).getRating() + a.get(a.size() - 1).getRating()) / 2);
            }

            a.remove(a.size() - 1);
            int z;
            for(z = 0; z < a.size(); z++) {
                if(a.get(z).getRating() >= 2.75){
                    star = 3;
                }
                else{
                    star = (int) Math.floor(a.get(z).getRating());
                }
                //adfsd

                switch (star) {
                    case 1:mMap.addMarker(new MarkerOptions().position(a.get(z).getLoc())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).snippet(a.get(z).getSent()));
                            break;
                    case 2:mMap.addMarker(new MarkerOptions().position(a.get(z).getLoc())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)).snippet(a.get(z).getSent()));
                            break;
                    case 3:mMap.addMarker(new MarkerOptions().position(a.get(z).getLoc())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).snippet(a.get(z).getSent()));
                            break;
                    default: mMap.addMarker(new MarkerOptions().position(a.get(z).getLoc())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).snippet(a.get(z).getSent()));
                        break;
                }
            }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(a.get(z-1).getLoc(), 17));
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

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ebu2, 18));
        enableMyLocation();
        mMap.setOnMyLocationButtonClickListener(this);

        //System.out.println(locate);
        Criteria crit = new Criteria();
        String provider = locate.getBestProvider(crit, true);
        mMap.setOnMapClickListener(this);
        // NULL check to see if intent is null
        // LatLng geisel = new LatLng(32.881145, -117.237394);

        if(getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey("bundle")) {
                Intent restore = getIntent();
                Bundle bun = restore.getBundleExtra("bundle");
                locationKeeper = bun.getParcelableArrayList("objects");
                setUpMap(locationKeeper);
            }
        }
            else {
                locationKeeper.add(new LocationHolder("Warren Lecture Hall", 3, warrenLecture));
                locationKeeper.add(new LocationHolder("Physics Building", 3, physicsEBU));
                locationKeeper.add(new LocationHolder("CSE Building", 3, ebu2));
                mMap.addMarker(new MarkerOptions().position(ebu2)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).snippet("CSE Building"));
                mMap.addMarker(new MarkerOptions().position(physicsEBU)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).snippet("Physics Building"));
                mMap.addMarker(new MarkerOptions().position(warrenLecture)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).snippet("Warren Lecture Hall"));
            }
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
        Toast.makeText(this, "Connections is suspended", Toast.LENGTH_SHORT);

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "Sorry gps connection failed", Toast.LENGTH_SHORT);
    }

    //My Locationge
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
        LocationHolder pass = new LocationHolder("restroom", 3 , latLng);
        locationKeeper.add(pass);
    }
    public void sendMessage(View view){
        Intent intent = new Intent(this, MapAdder.class);
        Bundle bundle = new Bundle();
        LatLng pass = new LatLng(32.8814126, -117.2339695);
        bundle.putParcelableArrayList("object", locationKeeper);
        intent.putExtra("adder", bundle);
        intent.putExtra("current", pass);
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

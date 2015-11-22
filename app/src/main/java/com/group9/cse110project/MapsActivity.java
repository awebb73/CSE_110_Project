package com.group9.cse110project;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
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
import com.google.android.gms.maps.UiSettings;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMyLocationButtonClickListener,
        LocationListener {


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
    private LocationManager locationManager;
    private static Location location;
    // private LocationHolder holder;
    private static String bestProvider;
    //comment to delete later........


    //MyLocation
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;/**
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
        //show error dialog if GoolglePlayServices not available
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        setContentView(R.layout.activity_maps);

        locationKeeper = new ArrayList<LocationHolder>();
        // this is not needed i dont think
        // will comment out and if things all go bad
        // i will put it back in
        // locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // initializing the map
        mMap = mapFragment.getMap();

        // puts the blue dot on the screen
        mMap.setMyLocationEnabled(true);

        // starting up the map
        onMapReady(mMap);

        // implementing the button
        // and makign it clickable
        Button send = (Button) findViewById(R.id.button);
        send.setEnabled(true);
        /*
        // need an if block that only checks after the first intenet has passed
        if(locationKeeper.size() > 0)
        {
            // getting the intent passed from the second activity
            Intent i = getIntent(); //.getExtra("array_list")

            locationKeeper = i.getParcelableArrayListExtra("array_list");
            // Log.d("awebb", "Activity 1  value: " + locationKeeper.size());

            // need to repopulate the map after we recieve the
            // info from the second activity
            setUpMap(locationKeeper);
        }
        */

        // can use this to recenter the marker
        // this will also reset the map on the first activity
        // need a conditional that only allows during the first Activity and
        // not received any information from the second activity
        if (location != null)
        {
            onLocationChanged(location);
        }

        //Location Coordinates
        buildGoogleApiClient();
        onConnected(savedInstanceState);

        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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
    // this is the method to repopulate the map
    // when we receive the information from the second activity
    // will catch the intent here and unpack it
    // this has the geobox and the rating system setup.
    private void setUpMap(ArrayList<LocationHolder> a) {
        int avg = 500000;
        int star;
        int locationManager = -3;
        double value;
        for (int position = 0; position < a.size() - 1; position++) {
            value = distanceFormula(a.get(position).getLat(), a.get(a.size() - 1).getLat(),
                    a.get(position).getLng(), a.get(a.size() - 1).getLng());
            if (value < avg && value <= threshold) {
                locationManager = position;
            }
        }

        if (locationManager !=-3) {
            a.get(locationManager).incrementCount();
            // divide by two because I avg on two counts.
            a.get(locationManager).setRating((a.get(locationManager).getRating() + a.get(a.size() - 1).getRating()) / 2);
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
            //variable to unpack the location data
            double lat = a.get(z).getLat();
            double lng = a.get(z).getLng();
            LatLng loc = new LatLng(lat, lng);
            switch (star) {
                case 1:mMap.addMarker(new MarkerOptions().position(loc)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).snippet(a.get(z).getSent()));
                    break;
                case 2:mMap.addMarker(new MarkerOptions().position(loc)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)).snippet(a.get(z).getSent()));
                    break;
                case 3:mMap.addMarker(new MarkerOptions().position(loc)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).snippet(a.get(z).getSent()));
                    break;
                default: mMap.addMarker(new MarkerOptions().position(loc)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).snippet(a.get(z).getSent()));
                    break;
            }
        }

        double lat = a.get(z-1).getLat();
        double lng = a.get(z-1).getLng();
        LatLng loc = new LatLng(lat, lng);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 17));
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
        UiSettings mMapSettings;
        Criteria criteria;
        mMap = googleMap;

        mMapSettings = mMap.getUiSettings();
        // turns off the snap button
        mMapSettings.setMyLocationButtonEnabled(false);
        // responds to a click on the location button
        mMap.setOnMyLocationButtonClickListener(this);


        // makes the map clickable
        // mMap.setOnMapClickListener(this);
        // Some more useful settings before i forget
        /*
        mMapSettings.setRotateGesturesEnabled(false);
        mMapSettings.setScrollGesturesEnabled(false);
        mMapSettings.setTiltGesturesEnabled(false);
        mMapSettings.setZoomControlsEnabled(false);
        mMapSettings.setZoomGesturesEnabled(false);
        */


        // sets the map at a fixed location i think
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ebu2, 18));

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        criteria = new Criteria();
        bestProvider = locationManager.getBestProvider(criteria, true);

        enableMyLocation();

		/*get current location
		double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(20));
		*/

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
            //random variables unpacking the variables
            double warLat = warrenLecture.latitude;
            double warLng = warrenLecture.longitude;
            double ebuLat = physicsEBU.latitude;
            double ebuLng = physicsEBU.longitude;
            double ebu2Lat = ebu2.latitude;
            double ebu2Lng = ebu2.longitude;

            locationKeeper.add(new LocationHolder("Warren Lecture Hall", 3, warLat, warLng));
            locationKeeper.add(new LocationHolder("Physics Building", 3, ebuLat, ebuLng));
            locationKeeper.add(new LocationHolder("CSE Building", 3, ebu2Lat, ebu2Lng));
            mMap.addMarker(new MarkerOptions().position(ebu2)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).snippet("CSE Building"));
            mMap.addMarker(new MarkerOptions().position(physicsEBU)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).snippet("Physics Building"));
            mMap.addMarker(new MarkerOptions().position(warrenLecture)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).snippet("Warren Lecture Hall"));
        }
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

    // allows us to click on a marker
    @Override
    public boolean onMarkerClick(Marker arg0) {
        arg0.showInfoWindow();
        return true;
    }

    @Override
    //handles adding in markers on the map
    // not sure we want to add markers from the first screen
    // right now not calling this method
    public void onMapClick(LatLng latLng) {
        float zoomLevel = 16;
        this.mMap.addMarker(new MarkerOptions().position(latLng).title("restroom"));
        this.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LocationHolder holder = new LocationHolder("restroom" ,0 ,latitude ,longitude);

        // LocationHolder pass = new LocationHolder("restroom", 3 , latLng);
        locationKeeper.add(holder);
    }
    // used to pass the intents between screens
    // going to decouple this from the UI so we can
    // use the click for control flow
    public void send(View view){
        Intent intent = new Intent(MapsActivity.this, MapAdder.class);
        Log.d("awebb", "0 locationKeeper.size() value: " + locationKeeper.size());
        intent.putParcelableArrayListExtra("array_list", locationKeeper);
        Log.d("awebb", "1 locationKeeper.size() value: " + locationKeeper.size());
        startActivity(intent);

		/* with bundle implementation
        Bundle bundle = new Bundle();
        LatLng pass = new LatLng(32.8814126, -117.2339695);
        bundle.putParcelableArrayList("object", locationKeeper);
        intent.putExtra("adder", bundle);
        intent.putExtra("current", pass);
        startActivity(intent);
		*/
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

    // no idea what this is even supposed to do
    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.group9.cse110project/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
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
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

    // no reason to touch any of this
    // a check to make sure we have access to the maps
    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }
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
            // mMap.setMyLocationEnabled(true);
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
}
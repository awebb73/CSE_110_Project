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
import com.parse.Parse;

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
    private ArrayList locationKeeper;
    //Location Coordinates
    private GoogleApiClient mGoogleApiClient;
    private double lastLat = 48.858207;
    private double lastLng = 2.294386;
    private LatLng warrenLecture= new LatLng(32.8807907, -117.2343316);
    private LatLng physicsEBU = new LatLng(32.8811838, -117.2332927);
    private LatLng ebu2 = new LatLng(32.8814126, -117.2339695);
    private double threshold = 2; //0.0012;
    private LocationManager locationManager;
    private static Location location;
    // private LocationHolder holder;
    private static String bestProvider;


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
    private ArrayList<LocationHolder> a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //show error dialog if GoolglePlayServices not available
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        setContentView(R.layout.activity_maps);
        // initializes the new arrayList
        locationKeeper = new ArrayList<LocationHolder>();



        // this is for parse
        // Enable Local Datastore.
        // Parse.enableLocalDatastore(this);
        // Parse.initialize(this, "U0lb0bfnvCNfUa39ytZHVwPOL79LdLHKHIhPPitI", "0EO6aOF1ne1V8UZxotkIkwTA9HSOWOCHZiXM5LQy");

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

        // getting the intent
        Intent intent = getIntent();
        if (intent.getExtras() == null)
        {
            // do the first pass stuff here
            // starting up the map
            // populates the array with the
            // the static locations
            // this appears to get called everytime
            // possibly by magic
            // onMapReady(mMap);

            // can use this to recenter the marker
            // this will also reset the map on the first activity
            if (location != null)
            {
                onLocationChanged(location);
            }
            //Location Coordinates
            buildGoogleApiClient();
            onConnected(savedInstanceState);

            // implementing the button
            // and making it clickable
            Button send = (Button) findViewById(R.id.button);
            send.setEnabled(true);

            // See https://g.co/AppIndexing/AndroidStudio for more information.
            client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        }
        // every other time we do this
        else
        {

            Bundle bundle = intent.getExtras();
            locationKeeper = bundle.getParcelableArrayList("array_list");

            Log.d("awebb", "1st act get size: " + locationKeeper.size());

            for (int i =0; i< locationKeeper.size();i++)
            {
                LocationHolder x = (LocationHolder) locationKeeper.get(i);
                Log.d("awebb", "x.getSent value: " + x.getSent());
            }

            // set up the map
            locationKeeper = setUpMap(locationKeeper);

            // can use this to recenter the marker
            // this will also reset the map on the first activity
            if (location != null)
            {
                onLocationChanged(location);
            }
            //Location Coordinates
            buildGoogleApiClient();
            onConnected(savedInstanceState);

            // implementing the button
            // and making it clickable
            Button send = (Button) findViewById(R.id.button);
            send.setEnabled(true);

            // See https://g.co/AppIndexing/AndroidStudio for more information.
            client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        }

    }


    //set up the location handler
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)                                //Location Coordinates
                .addOnConnectionFailedListener(this)                         //Location Coordinates
                .build();
    }
    // a formula to compute the distance between 2 locations
    private double distanceFormula(double x1, double x2, double y1, double y2){
        return Math.sqrt(Math.abs(Math.pow((x1-x2), 2)) + Math.abs(Math.pow((y1-y2),2)));
    }
    // this is the method to repopulate the map
    // when we receive the information from the second activity
    // will catch the intent here and unpack it
    // this has the geobox and the rating system setup.
    private ArrayList setUpMap(ArrayList<LocationHolder> a) {
        this.a = a;
        double avg = 500000;
        int star;
        int locationManager = -3;
        double value;
        mMap.setOnMarkerClickListener(this);

        // iterates through the ArrayList
        // compares the current item to the last item
        // this skips the first add we should be checking all the time
        // i dont think i am loading the ArrayList correctly
        // it should always have 3 or 4 elements in it
        for (int position = 0; position < a.size() - 1; position++) {
            value = distanceFormula(a.get(position).getLat(), a.get(a.size() - 1).getLat(),
                    a.get(position).getLng(), a.get(a.size() - 1).getLng());

            // Log.d("awebb", "value: " + value);

            // if the distance < 50000 and <= 0.000656
            // set a variable to the current position
            // in the array
            if (value < avg && value <= threshold) {
                avg = value;
                locationManager = position;
                Log.d("awebb", "locationManager value: " + locationManager);
            }
        }
        // if the locationManager was changed
        // get the element in the ArrayList and
        // average it rating with the last element in the list
        // why the last element every time? is that special
        if (locationManager !=-3) {
            LocationHolder newAdd = a.get(locationManager);
            // take current average
            float curRat = newAdd.getRating();
            // multiply by count
            int curCount = newAdd.getCount();
            float multRat = curRat * curCount;
            // add new rating
            multRat = multRat + a.get(a.size()-1).getRating();
            // increment count
            newAdd.incrementCount();
            // divide by count
            newAdd.setRating(multRat/newAdd.getCount());
            // set new info into array
            // a.get(locationManager).setRating(newAdd.getRating());
            // a.get(locationManager).incrementCount();
        }
        // Log.d("awebb", "a.size: " + a.size());
        // removes the last element from the arrayList
        a.remove(a.size() - 1);
        // Log.d("awebb", "a.size after remove: " + a.size());
        // iterate through the remaining list and
        // assign colors to the markers based on
        // the average of the rating
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
            // Log.d("awebb", "lat value: " + lat);
            double lng = a.get(z).getLng();
            // Log.d("awebb", "lng value: " + lng);
            LatLng loc = new LatLng(lat, lng);
            switch (star) {
                case 1:mMap.addMarker(new MarkerOptions().position(loc)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).title(a.get(z).
                                getSent()).snippet("" + a.get(z).getRating()+ "\n" + a.get(z).getCount() ));
                    break;
                case 2:mMap.addMarker(new MarkerOptions().position(loc)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)).title(a.get(z).getSent()).
                                snippet("" + a.get(z).getRating()+ "\n" + a.get(z).getCount() ));
                    break;
                case 3:mMap.addMarker(new MarkerOptions().position(loc)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).title(a.get(z).getSent()).
                                snippet("" + a.get(z).getRating()+ "\n" + a.get(z).getCount() ));
                    break;
                default: mMap.addMarker(new MarkerOptions().position(loc)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).title(a.get(z).getSent()).
                                snippet("" + a.get(z).getRating() + "\n" + a.get(z).getCount() ));
                    break;
            }
        }

        // cant do this with only 1 rating
        // we removed the last element and
        // then try to remove nothing
        double lat = a.get(z-1).getLat();
        // Log.d("awebb", "lat value: " + lat);
        double lng = a.get(z-1).getLng();
        // Log.d("awebb", "3 locationKeeper.size() value: " + lng);
        LatLng loc = new LatLng(lat, lng);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 17));
        return a;
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
        /*
        if(location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            LatLng latLng = new LatLng(latitude, longitude);

            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
        }
        else{
            Toast.makeText(this, "location null" + location, Toast.LENGTH_LONG);
        }
        */

        UiSettings mMapSettings;
        Criteria criteria;
        mMap = googleMap;

        mMapSettings = mMap.getUiSettings();
        // turns off the snap button
        mMapSettings.setMyLocationButtonEnabled(false);
        // sets the marker click listener
        mMap.setOnMarkerClickListener(this);

        // set the listener to the map
        // mMap.setOnMyLocationButtonClickListener(this);
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
        // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ebu2, 18));

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        criteria = new Criteria();
        bestProvider = locationManager.getBestProvider(criteria, true);

        enableMyLocation();

		// get current location
		double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));


        // doing this check in the onCreate
        // only calling this function the first time we use the map so no need for this check
        // removed to not
        // mMap.setOnMapClickListener(this);

        // NULL check to see if intent is null
        // if intent is null we populate the map w/ warren restroom
        // if intent is not null populate with new pin

        // getting the intent
        Intent intent = getIntent();
        if(intent.getExtras() == null) {

            Log.d("awebb", "first time onMapReady " );
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
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).title("CSE Building").snippet("")); //+ locationKeeper.get(2).getRating()));
            mMap.addMarker(new MarkerOptions().position(physicsEBU)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).title("Physics Building").snippet("Physics Building"));
            mMap.addMarker(new MarkerOptions().position(warrenLecture)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).title("Warren Lecture Hall").snippet("Warren Lecture Hall"));

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
        // ArrayList<LocationHolder> aList = new ArrayList();
        Intent intent = new Intent(getApplicationContext(), MapAdder.class);
        intent.putExtra("array_list", locationKeeper);


        Log.d("awebb", "1st act send size " + locationKeeper.size());
        /*
        for (int i =0; i<locationKeeper.size();i++)
        {
            LocationHolder x = locationKeeper.get(i);
            Log.d("awebb", "x.getSent value: " + x.getSent());
            // Log.d("awebb", "x.getCount value: " + x.getCount());

        }
        */
        /*
		//with bundle implementation
        Bundle bundle = new Bundle();
        // LatLng pass = new LatLng(32.8814126, -117.2339695);
        bundle.putParcelableArrayList("array_list", locationKeeper);
        intent.putExtra("adder", bundle);

        Log.d("awebb", "0 bundle.size() value: " + bundle.size());
        Log.d("awebb", "1 locationKeeper.size() value: " + locationKeeper.size());

        for (int i =0; i<locationKeeper.size();i++)
        {
            LocationHolder x = locationKeeper.get(i);
            Log.d("awebb", "x.getSent value: " + x.getSent());
            // Log.d("awebb", "x.getCount value: " + x.getCount());

        }
        */
        // intent.putExtra("current", pass);

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
//package com.group9.cse110project;

package com.group9.cse110project;

import android.content.Intent;
import android.os.Parcelable;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
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
        GoogleMap.OnMarkerDragListener{

    private GoogleMap mMap;
    private ArrayList<LocationHolder> position;
    private RatingBar ratingBar;
    //private Button fin;
    private double stars;
    private LatLng mark;

    // adding some comments as a test for git hub
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!isGooglePlayServicesAvailable()){finish();}
        setContentView(R.layout.activity_map_adder);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button fin = (Button) findViewById(R.id.button);
        fin.setEnabled(false);
        addListenerOnRatingBar();

    }


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
        mMap.setOnMarkerDragListener(this);
        Intent intent = getIntent();
        //LatLng current = intent.get("
        Bundle tmp = intent.getBundleExtra("adder");
        position = tmp.getParcelableArrayList("object");
        mark = position.get(position.size()-1).getLoc();
        mMap.addMarker(new MarkerOptions().position(position.get(position.size()-1).getLoc()).title("restroom")).setDraggable(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position.get(position.size() - 1).getLoc(), 17));
        // ART -- Add a marker in Sydney and move the camera
        // LatLng ucsd = new LatLng(32.881145, -117.2374);
        // mMap.addMarker(new MarkerOptions().position(ucsd).title("UCSD"));
        // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ucsd, 17));
    }

    @Override
    public void onMarkerDrag(Marker arg0){
        arg0.setDraggable(true);
    }

    @Override
    public void onMarkerDragEnd(Marker marker){
        mark = marker.getPosition();
        //double lat = mark.latitude;
        //double longi = mark.longitude;
        mark = marker.getPosition();
    }

    @Override
    public void onMarkerDragStart(Marker marker){}

    // ART --gathers the stars num data and can be pushed to the first intent
    public void fin(View view) {
        //LocationHolder value = new LocationHolder("", stars, mark);
        position.get(position.size()-1).setLoc(mark);
        position.get(position.size()-1).setRating(stars);
        Intent back = new Intent(this, MapsActivity.class);
        Bundle a = new Bundle();
        //a.putSerializable("objects", position);
        a.putParcelableArrayList("objects", position);
        back.putExtra("bundle", a);
        startActivity(back);
    }

    // ART -- listens to the rating bar and actives he confirnation screen
    public void addListenerOnRatingBar() {
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        // ART -- if the value is changed
        // ART -- we allow the confirmation button to become activated
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                stars = (int) ratingBar.getRating();
                // ART -- command to see logcat output
                // Log.d("Checking Stars value: ", String.valueOf(stars));
                // ART -- if else block to only turn on the confirmation button as required
                if (stars == 0) {
                    Button fin = (Button) findViewById(R.id.button);
                    fin.setEnabled(false);
                }
                else if (stars > 0) {
                    Button fin = (Button) findViewById(R.id.button);
                    fin.setEnabled(true);
                }
            }
        });
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

    //don't need current location, markers are draggable and dropped based on latest current location
    // dont touch any of this stuff
    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */

}
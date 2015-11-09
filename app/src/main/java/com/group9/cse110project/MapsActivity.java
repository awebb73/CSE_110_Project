package com.group9.cse110project;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
//handle adding a pin on pin drop
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener,   GoogleMap.OnMarkerClickListener {
    public final static String EXTRA_MESSAGE = "com.group9.cse110project.MESSAGE";
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        mMap.setMyLocationEnabled(true);
        LocationManager locate = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        System.out.println(locate);
        Criteria crit = new Criteria();
        String provider = locate.getBestProvider(crit, true);
        mMap.setOnMapClickListener(this);
        LatLng geisel = new LatLng(32.881145, -117.237394);
        //mMap.addMarker(new MarkerOptions().position(geisel).title("Geisel Library"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(geisel, 17));
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
        this.mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }
    public void sendMessage(View view){
        Intent intent = new Intent(this, MapAdder.class);
        //EditText tex = (EditText) findViewById(R.id.text);
        //String message = tex.getText().toString();
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

}

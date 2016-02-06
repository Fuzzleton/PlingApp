package com.fyp.geniu.plingapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class PlingMain extends FragmentActivity implements OnMapReadyCallback {


    GoogleMap mMap;
    Marker marker;
    Location location;
    LocationManager locationManager;
    GPSTracker tracker;


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.v("Maps", "Map Ready");
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pling_main);
        SupportMapFragment mapFrag = (SupportMapFragment)
                getSupportFragmentManager().
                        findFragmentById(R.id.map);
        tracker = new GPSTracker(this);



        mapFrag.getMapAsync(this);
        onHostClickSetup();
        onViewClickSetup();
    }

    /**
     * Manipulates the map once available.
     * This is where we can add markers or lines, add listeners or move the camera.
     */


    private void onHostClickSetup() {

        Button btnHost = (Button) findViewById(R.id.btnHost);
        btnHost.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                HostEvent();
            }
        });
    }

    private void onViewClickSetup() {

        Button btnView = (Button) findViewById(R.id.btnView);
        btnView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                ViewEvent();
            }
        });
    }

    private void HostEvent() {

        Toast.makeText(PlingMain.this, "Event host method called, but not yet written.", Toast.LENGTH_LONG).show();

        // Add a new intent to open a HostEvent class type doodad

    }

    private void ViewEvent() {

        Toast.makeText(PlingMain.this, "Event view method called, but not yet written.", Toast.LENGTH_LONG).show();

        // Add a new intent to open a ViewEvent class type doodad

    }


    private void drawMarker() {
        mMap.clear();

        Log.v("Maps", "starting draw");
        location = tracker.getLocation();

        //  convert the location object to a LatLng object that can be used by the map API
        LatLng currentPosition = new LatLng(location.getLatitude(),location.getLongitude());

        Log.v("Maps", "LatLong received, " + currentPosition.toString());

        // zoom to the current location
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 16));

        // add a marker to the map indicating our current position
        mMap.addMarker(new MarkerOptions()
                .position(currentPosition)
                .snippet(currentPosition.toString()));


    }
}
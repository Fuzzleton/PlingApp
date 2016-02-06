package com.fyp.geniu.plingapp;

import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pling_main);
        SupportMapFragment mapFrag = (SupportMapFragment)
                getSupportFragmentManager().
                        findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);

        onHostClickSetup();
        onViewClickSetup();



        mMap = mapFrag.getMap();

        if (mMap != null) {

            mMap.setOnMapLongClickListener(new
                                                   GoogleMap.OnMapLongClickListener() {
                                                       @Override
                                                       public void onMapLongClick(LatLng latLng) {
                                                           Geocoder geocoder =
                                                                   new Geocoder(PlingMain.this);
                                                           List<Address> list;
                                                           try {
                                                               list = geocoder.getFromLocation(latLng.latitude,
                                                                       latLng.longitude, 1);
                                                           } catch (IOException e) {
                                                               return;
                                                           }
                                                           Address address = list.get(0);
                                                           if (marker != null) {
                                                               marker.remove();
                                                           }

                                                           MarkerOptions options = new MarkerOptions()
                                                                   .title(address.getLocality())
                                                                   .position(new LatLng(latLng.latitude,
                                                                           latLng.longitude));

                                                           marker = mMap.addMarker(options);
                                                       }
                                                   });
        }
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
}

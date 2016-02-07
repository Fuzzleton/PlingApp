package com.fyp.geniu.plingapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

public class PlingMain extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener {


    GoogleMap mMap;
    Marker marker;
    Location location;
    LocationManager locationManager;
    GPSTracker tracker;
    Marker mrkMyMarker;
    Boolean isMyMarkerRemoved = true;
    LinkedList<Marker> llstEventMarkers = new LinkedList<>();


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.v("Maps", "Map Ready");
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapLongClickListener(this);
        zoomToCurrentPosition();
        mMap.setOnMarkerClickListener(this);

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




    private void SetMyMarker(LatLng point){
        ///set marker here at long click location

        if (mrkMyMarker != null) mrkMyMarker.remove();
        isMyMarkerRemoved = true;

        mrkMyMarker = mMap.addMarker(new MarkerOptions().position(point).draggable(false));
        isMyMarkerRemoved = false;
    }

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



        if (isMyMarkerRemoved) {
            mrkMyMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(tracker.getLatitude(),tracker.getLongitude())));
            isMyMarkerRemoved = false;
        }



        AlertDialog.Builder hostDialog = new AlertDialog.Builder(this);

        hostDialog.setTitle("Host Event Description");
        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        //input.setLayoutParams(lp);
        hostDialog.setView(input);


        hostDialog.setPositiveButton("Host", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                JSONObject json = new JSONObject();

                SharedPreferences settings = getSharedPreferences("prefs", 0);


                String desc = null;
                try {
                    desc = java.net.URLEncoder.encode(input.getText().toString(),"UTF-8").replace("+","%20");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


                try {
                    json.put("sourcemac",getMAC());
                    json.put("location",mrkMyMarker.getPosition().latitude+","+mrkMyMarker.getPosition().longitude);
                    json.put("description", desc);


                    NetworkAgentBridge br = new NetworkAgentBridge();
                    String response = br.ServerRequest(settings.getString("IPADDRESS", null),"host_event"+json.toString());

                    if (response == "registered") {

                        Toast.makeText(PlingMain.this, "Event Registered!", Toast.LENGTH_LONG).show();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
        hostDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //nothing
            }
        });
        hostDialog.show();
        // Add a new intent to open a HostEvent class type doodad

    }

    private void ViewEvent() {

        for (int i = 0; i < llstEventMarkers.size() ; i++) {
            llstEventMarkers.get(i).remove();

        }

        llstEventMarkers.clear();

        Toast.makeText(PlingMain.this, "Event view method called, but not yet written.", Toast.LENGTH_LONG).show();


        SharedPreferences settings = getSharedPreferences("prefs", 0);
        NetworkAgentBridge br = new NetworkAgentBridge();
        String reply = br.ServerRequest(settings.getString("IPADDRESS",null),"get_events");
        // Add a new intent to open a ViewEvent class type doodad

        JSONArray array = null;
        try {
            array = new JSONArray(reply);

        for (int i = 0 ; i < array.length() ; i++) {
            MarkerOptions mrkopt = new MarkerOptions();
            mrkopt.title(array.getJSONObject(i).getString("sourcemac"));
            mrkopt.snippet(array.getJSONObject(i).getString("description"));

            String[] latlong =  array.getJSONObject(i).getString("latlong").split(",");
            double latitude = Double.parseDouble(latlong[0]);
            double longitude = Double.parseDouble(latlong[1]);

            mrkopt.position(new LatLng(latitude,longitude));

            Marker mrk = mMap.addMarker(mrkopt);
            llstEventMarkers.add(mrk);



        }
    } catch (JSONException e) {
        e.printStackTrace();
    }


    }


    private void zoomToCurrentPosition() {
        Log.v("Maps", "starting draw");
        location = tracker.getLocation();

        //  convert the location object to a LatLng object that can be used by the map API
        LatLng currentPosition = new LatLng(location.getLatitude(),location.getLongitude());

        Log.v("Maps", "LatLong received, " + currentPosition.toString());

        // zoom to the current location
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 16));
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        SetMyMarker(latLng);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Do you want to delete the marker?").setTitle("Marker Deletion");

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mrkMyMarker.remove();
                isMyMarkerRemoved = true;
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //nothing
            }
        });

        AlertDialog dialog = builder.create();

        dialog.show();

        return false;
    }

    public String getMAC(){
        WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        return info.getMacAddress();
    }
}
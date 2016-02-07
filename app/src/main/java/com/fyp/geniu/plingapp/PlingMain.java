package com.fyp.geniu.plingapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;

public class PlingMain extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener {


    GoogleMap mMap;
    Marker marker;
    Location location;
    LocationManager locationManager;
    GPSTracker tracker;
    Marker mrkMyMarker;
    Boolean isMyMarkerRemoved = true;
    LinkedList<Marker> llstEventMarkers = new LinkedList<>();



    /*Begins Setup for view, including
    * - Misc Activity setup
    * - Starting the GPS tracker
    * - Starting the Async building of the Map
    * Setting up event listeners for the Buttons  */
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

    /*Handles Setup additional components once the map is ready:
    *  - Drawing User Location
    *  - Moving map to User Location
    *  - Setting up long click and marker listeners*/
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.v("Maps", "Map Ready");
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapLongClickListener(this);
        zoomToCurrentPosition();
        mMap.setOnMarkerClickListener(this);

    }




    /*Sets up the Client's marker at given location*/
    private void SetMyMarker(LatLng point){


        if (mrkMyMarker != null) mrkMyMarker.remove();
        isMyMarkerRemoved = true;

        mrkMyMarker = mMap.addMarker(new MarkerOptions().position(point).draggable(false));
        isMyMarkerRemoved = false;
    }

    /*Sets up the Host Button's Listener*/
    private void onHostClickSetup() {

        Button btnHost = (Button) findViewById(R.id.btnHost);
        btnHost.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                HostEvent();
            }
        });
    }
    /*Sets up the View Button's Listener*/
    private void onViewClickSetup() {

        Button btnView = (Button) findViewById(R.id.btnView);
        btnView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                ViewEvent();
            }
        });
    }


    /*This method is incharge of handling the hosting of event at the
    * Client's marker location*/
    private void HostEvent() {
        //Checks to see if there is the client marker is currently present on the map, and if not, place it at user's current location
        if (isMyMarkerRemoved) {
            mrkMyMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(tracker.getLatitude(),tracker.getLongitude())));
            isMyMarkerRemoved = false;
        }

        //Builds the dialog asking user to supply a description for the event
        AlertDialog.Builder hostDialog = new AlertDialog.Builder(this);
        hostDialog.setTitle("Host Event Description");
        final EditText input = new EditText(this);
        hostDialog.setView(input);

        //if the user clicks host
        hostDialog.setPositiveButton("Host", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //creates a JSON object, and a SharedPrefs for use later
                JSONObject json = new JSONObject();
                SharedPreferences settings = getSharedPreferences("prefs", 0);

                //gets the input from, and makes it http friendly
                String desc = null;
                try {
                    desc = java.net.URLEncoder.encode(input.getText().toString(),"UTF-8").replace("+","%20");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                //Working with JSON and Sending info
                try {
                    //Assembles the JSON document
                    json.put("sourcemac",getMAC());
                    json.put("location",mrkMyMarker.getPosition().latitude+","+mrkMyMarker.getPosition().longitude);
                    json.put("description", desc);

                    //Sends the Host Event http request to the server, and gets the response
                    //Event registration and hosting is handled serverside
                    NetworkAgentBridge br = new NetworkAgentBridge();
                    String response = br.ServerRequest(settings.getString("IPADDRESS", null),"host_event"+json.toString());


                    //if response from server is registered, notify user of success
                    if (response == "registered") {
                        Toast.makeText(PlingMain.this, "Event Registered!", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

        //if the user clicks cancel
        hostDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //nothing
            }
        });
        hostDialog.show();
        // Add a new intent to open a HostEvent class type doodad

    }

    private void ViewEvent() {
        //Removes all Event markers from the map
        for (int i = 0; i < llstEventMarkers.size() ; i++) {
            llstEventMarkers.get(i).remove();
        }
        //Clears the list of all events
        llstEventMarkers.clear();

        //Builds SharedPrefs to retrieve IP Address
        SharedPreferences settings = getSharedPreferences("prefs", 0);

        //Starts NetworkAgentBridge and sends a request to view all current events
        NetworkAgentBridge br = new NetworkAgentBridge();
        String reply = br.ServerRequest(settings.getString("IPADDRESS",null),"get_events");


        //Handles the JSON response from the server
        JSONArray array = null;
        try {
            //builds a JSONArray from the reply string
            array = new JSONArray(reply);


            //Cycles through the JSONArray to work on each event
            for (int i = 0 ; i < array.length() ; i++) {
                //Sets up the Markers for each event.
                MarkerOptions mrkopt = new MarkerOptions();
                mrkopt.title(array.getJSONObject(i).getString("sourcemac"));
                mrkopt.snippet(array.getJSONObject(i).getString("description"));
                String[] latlong =  array.getJSONObject(i).getString("latlong").split(",");
                double latitude = Double.parseDouble(latlong[0]);
                double longitude = Double.parseDouble(latlong[1]);
                mrkopt.position(new LatLng(latitude,longitude));

                //Adds the Marker to the map and the eventmarkers list
                Marker mrk = mMap.addMarker(mrkopt);
                llstEventMarkers.add(mrk);



        }
    } catch (JSONException e) {
        e.printStackTrace();
    }


    }

    //Zooms into the user's current location
    private void zoomToCurrentPosition() {


        //  pull location from tracker
        location = tracker.getLocation();
        //  convert the location object to a LatLng object that can be used by the map API
        LatLng currentPosition = new LatLng(location.getLatitude(),location.getLongitude());

        Log.v("Maps", "LatLong received, " + currentPosition.toString());

        // zoom to the current location
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 16));
    }


    /*On Long Click of map, calls the SetMyMarker*/
    @Override
    public void onMapLongClick(LatLng latLng) {
        SetMyMarker(latLng);
    }

    /*Handles When the Marker was clicked
    * - Opens Alert Dialog to give them option to delete*/
    @Override
    public boolean onMarkerClick(Marker marker) {
        //Builds Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to delete the marker?").setTitle("Marker Deletion");
        //if Delete pressed Removes the client marker
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mrkMyMarker.remove();
                isMyMarkerRemoved = true;
            }
        });
        //if negative clicked, do nothing
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //nothing
            }
        });
        //Show Dialog
        AlertDialog dialog = builder.create();
        dialog.show();
        return false;
    }
    //Fetches phone's MAC address
    public String getMAC(){
        WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        return info.getMacAddress();
    }
}
package com.fyp.geniu.plingapp;

/**
 * Created by geniu on 06/02/2016.
 */
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

/**
 * Created by Kieran on 25/01/2016.
 */
public class NetworkAgentBridge {
    Context context;

    public NetworkAgentBridge(Context c){
        context = c;
    }


    /*Starts Async Worker to handle network activity*/
    public  String ServerRequest(String request_type, String content){

        SharedPreferences settings = context.getSharedPreferences("prefs",0);

        try {
            content = URLEncoder.encode(content, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String address = "http://" + settings.getString("IPADDRESS",null) + "/" + request_type + "?" + content;
        try {
            return new AsyncWorker().execute(address).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        //if network failed, return a failure
        return "failure";
    }

    //Async worker that handles network
    private class AsyncWorker extends AsyncTask<String,Integer, String>{


        protected String doInBackground(String ... url) {

            String reply = "";
            InputStream response = null;
            URLConnection connection = null;
            try {
                //Makes the Network connection, and gets the HTTP response
                response = new URL(url[0]).openStream();
                Log.d("Network","Conection Setup");
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Converts the Input Stream into a string for us to use
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(response,"UTF-8"))) {
                for (String line; (line = reader.readLine()) != null;) {
                    reply += line;
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("Network","Reply is " + reply);
            return reply;
        }

    }

}






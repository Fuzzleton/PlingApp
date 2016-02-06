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
import java.util.concurrent.ExecutionException;

/**
 * Created by Kieran on 25/01/2016.
 */
public class NetworkAgentBridge {

    public  String ServerRequest(String strURL, String strMSG){


        String address = "http://" + strURL + "/" + strMSG;

        try {
            return new AsyncWorker().execute(address).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return "failure";


    }


    private class AsyncWorker extends AsyncTask<String,Integer, String>{


        protected String doInBackground(String ... url) {



            String reply = "";
            InputStream response = null;
            URLConnection connection = null;
            try {
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






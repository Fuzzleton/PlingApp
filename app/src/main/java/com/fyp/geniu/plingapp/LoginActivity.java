package com.fyp.geniu.plingapp;


import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {

    SharedPreferences settings;
    EditText edtIpAddress;
    EditText edtUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        settings = getSharedPreferences("prefs",0);
        edtIpAddress = (EditText) findViewById(R.id.txtServerAddress);
        edtUsername = (EditText) findViewById(R.id.txtUsername);
        edtIpAddress.setText(settings.getString("IPADDRESS",""));
        edtUsername.setText(settings.getString("USERNAME",""));
        ButtonSwitch();
    }

    // Sets up the OnClick Listener for the Connect Button
    private void ButtonSwitch() {

        Button btnSwitch = (Button) findViewById(R.id.btnConnect);
        btnSwitch.setOnClickListener(new View.OnClickListener() {


            public void onClick(View v) {
                ButtonClicked();
            }
        });
    }

    //Method Called when Connect button is clicked
    //Gets Connection details and sends a request to server to check connectivity
    //Then loads PlingMain
    private void ButtonClicked() {


        //puts the IP address into shared preferences for later use
        settings = getSharedPreferences("prefs",0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("IPADDRESS",edtIpAddress.getText().toString());
        editor.putString("USERNAME", edtUsername.getText().toString());
        editor.commit();



        //Assembles the request string
        String MSG = "connect" +edtUsername.getText().toString();


        try {
            //Uses NetworkAgentBridge class to send request to server
            NetworkAgentBridge br = new NetworkAgentBridge(this);
            String Reply = br.ServerRequest("register","nan");
            Log.d("Login","And reply here is {" + Reply + "}");

            JSONObject jsonReply = new JSONObject(Reply);


            //if the page returns has 'true' in it
            if (jsonReply.getString("reply").contains("0")) {

                //notify user of successful request
                Toast.makeText(LoginActivity.this, "Connected.", Toast.LENGTH_LONG).show();

                //Launch Pling Main
                startActivity(new Intent(LoginActivity.this, PlingMain.class));
            }

        } catch (Exception ex) {

            //Notify user of failure
            Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }
}


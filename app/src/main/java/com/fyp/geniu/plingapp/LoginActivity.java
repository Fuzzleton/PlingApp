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

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        ButtonSwitch();
    }


    private void ButtonSwitch() {

        Button btnSwitch = (Button) findViewById(R.id.btnConnect);
        btnSwitch.setOnClickListener(new View.OnClickListener() {


            public void onClick(View v) {

                ButtonClicked();

            }
        });
    }


    private void ButtonClicked() {


        //Sends Login Request to Server (for now Ultra Simple)
        //If Login is Successful (At this stage, if can connect to server)...
        //... Proceed loading Maps Activity
        //If no INTERNET CONNECTION or UNABLE TO FIND SERVER...
        // DO NOT PROCEED

        EditText EDTipaddress = (EditText) findViewById(R.id.txtServerAddress);
        String strIPADDRESS = EDTipaddress.getText().toString();



        SharedPreferences settings = getSharedPreferences("prefs",0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("IPADDRESS",strIPADDRESS);
        editor.commit();

        WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        String strMac = info.getMacAddress();

        String MSG = "connect" +strMac;

        try {
            NetworkAgentBridge br = new NetworkAgentBridge();
            String Reply = br.ServerRequest(strIPADDRESS,MSG);
            Log.d("Login","And reply here is {" + Reply + "}");

            if (Reply.contains("true")) {
                Log.d("Login", "Reply Received here too!");
                Toast.makeText(LoginActivity.this, "Connected.", Toast.LENGTH_LONG).show();


                startActivity(new Intent(LoginActivity.this, PlingMain.class));
            }

        } catch (Exception ex) {
            Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }
}


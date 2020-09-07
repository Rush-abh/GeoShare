package com.example.vishalmunjani.geoshare;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * Created by vishal.munjani on 4/15/2017.
 */

public class Home extends AppCompatActivity implements View.OnClickListener,LocationListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    Button btnEncrypt,btnDecrypt;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 1800 * 1000;  /* 30 mins */
    private long FASTEST_INTERVAL = 1500 * 1000; /* 15 mins*/
    private boolean permissionIsGranted=false;
    Double dLatitude;
    Double dLongitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        btnEncrypt=(Button)findViewById(R.id.btnEncrypt);
        btnDecrypt=(Button)findViewById(R.id.btnDecrypt);
        btnEncrypt.setOnClickListener(this);
        btnDecrypt.setOnClickListener(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);


    }


    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btnEncrypt){
            Toast.makeText(this, "Select Location to Encrypt Data", Toast.LENGTH_LONG).show();
            Intent intent=new Intent(Home.this,MapsActivity.class);
            startActivity(intent);
        }
        if(v.getId()==R.id.btnDecrypt){
            startLocationUpdates();
            Intent intent=new Intent(Home.this,Decrypt.class);
            startActivity(intent);
        }
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] {ACCESS_FINE_LOCATION},SSLUtil.REQUEST_FINE_LOCATION);
            }else{
                permissionIsGranted=true;
            }
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,this);
    }
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();
        if(permissionIsGranted)
            mGoogleApiClient.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(permissionIsGranted){
            if(mGoogleApiClient.isConnected()){
                startLocationUpdates();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(permissionIsGranted)
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        dLatitude = location.getLatitude();
        dLongitude = location.getLongitude();
        SharedPreferences.Editor editor = getSharedPreferences(SSLUtil.PREFERENCE_NAME, MODE_PRIVATE).edit();
        editor.putString(SSLUtil.MYLATITUDE,""+dLatitude+"");
        editor.putString(SSLUtil.MYLONGITUDE,""+dLongitude+"");
        editor.commit();
        String msg = "Your Current Location is : " +
                Double.toString(dLatitude) + "," +
                Double.toString(dLongitude);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}

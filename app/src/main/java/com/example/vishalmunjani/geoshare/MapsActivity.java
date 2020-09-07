package com.example.vishalmunjani.geoshare;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
/**
 * Created by vishal.munjani on 4/15/2017.
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMarkerDragListener{

    private GoogleMap mMap;
    private boolean permissionIsGranted = false;
    double mLatitude,mLongitude;
    Button btnSendLatLang;
    String email=null;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        btnSendLatLang=(Button)findViewById(R.id.sendLatLang);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i(SSLUtil.TAG, "Place: " + place.getName());
                String locationName = place.getName().toString();
                if (locationName != null || !locationName.equals("")) {
                    mMap.clear();
                    LatLng latLng = place.getLatLng();
                    mLatitude=latLng.latitude;
                    mLongitude=latLng.longitude;
                    Toast.makeText(getApplicationContext(), "Latitude :" + latLng.latitude + "Longitude :" + latLng.longitude, Toast.LENGTH_SHORT).show();
                    mMap.addMarker(new MarkerOptions().position(latLng).title(locationName).draggable(true));

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,14.0f));

                    mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                        @Override
                        public void onMarkerDragStart(Marker marker) {
                            Log.e(SSLUtil.TAG,"MArker Drag Start");
                            Toast.makeText(getApplicationContext(), "Marker Drag start", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onMarkerDrag(Marker marker) {
                            Toast.makeText(getApplicationContext(), "On Marker Drag" , Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onMarkerDragEnd(Marker marker) {
                            Log.e(SSLUtil.TAG,"Marker Drag End");
                            LatLng latLng=marker.getPosition();
                            mLatitude=latLng.latitude;
                            mLongitude=latLng.longitude;
                            Toast.makeText(getApplicationContext(), "Marker Drag End", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "search place", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Status status) {
                Log.i(SSLUtil.TAG, "An error occurred: " + status);
                Toast.makeText(MapsActivity.this, status.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        btnSendLatLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendLatLang();
            }
        });
    }

    private void sendLatLang() {
        class SSLSendAsync extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                SharedPreferences prefs = getSharedPreferences(SSLUtil.PREFERENCE_NAME,MODE_PRIVATE);
                email = prefs.getString(SSLUtil.EMAIL,null);
                try{
                    CertificateFactory cf = CertificateFactory.getInstance(SSLUtil.CERTIFICATE_TYPE);
                    InputStream is =getResources().openRawResource(R.raw.server);
                    InputStream caInput=new BufferedInputStream(is);
                    Certificate ca;
                    try {
                        ca = cf.generateCertificate(caInput);
                    } finally {
                        caInput.close();
                    }
                    String keyStoreType= KeyStore.getDefaultType();
                    KeyStore keyStore = KeyStore.getInstance(keyStoreType);
                    keyStore.load(null, null);
                    keyStore.setCertificateEntry("ca", ca);
                    String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                    TrustManagerFactory trustManagerFactory= TrustManagerFactory.getInstance(tmfAlgorithm);
                    trustManagerFactory.init(keyStore);

                    SSLContext sslContext= SSLContext.getInstance(SSLUtil.TLS_VERSION);
                    sslContext.init(null,trustManagerFactory.getTrustManagers(), null);
                    SSLSocket sslSocket= (SSLSocket) sslContext.getSocketFactory().createSocket(SSLUtil.HOST_IP,SSLUtil.TCP_PORT);
                    JSONObject jsonSendLatLang = new JSONObject();
                    jsonSendLatLang.put(SSLUtil.TYPE,SSLUtil.DEC_TYPE);
                    jsonSendLatLang.put(SSLUtil.SUBTYPE,SSLUtil.SUBTYPE_ENCRYPT);
                    jsonSendLatLang.put(SSLUtil.EMAIL,email);
                    jsonSendLatLang.put(SSLUtil.LATITUDE,""+mLatitude+"");
                    jsonSendLatLang.put(SSLUtil.LONGITUDE,""+mLongitude+"");
                    PrintWriter outp = new PrintWriter(sslSocket.getOutputStream(), true);
                    outp.println(jsonSendLatLang);
                    sslSocket.close();
                } catch (CertificateException e) {
                    Toast.makeText(MapsActivity.this, "Certificate Verify Error :"+e.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (IOException e){
                    Toast.makeText(MapsActivity.this, "Input Output Excetion :"+e.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (NoSuchAlgorithmException e) {
                    Toast.makeText(MapsActivity.this, "No such Algo  :"+e.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (KeyStoreException e) {
                    Toast.makeText(MapsActivity.this, "Key Store :"+e.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (KeyManagementException e) {
                    Toast.makeText(MapsActivity.this, "Key Manager :"+e.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return "True";
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog= ProgressDialog.show(MapsActivity.this, "Sending Data", "Please Wait.......",false,false);
                progressDialog.show();
            }

            @Override
            protected void onPostExecute(String s) {
                progressDialog.dismiss();
                Toast.makeText(MapsActivity.this, "Send data successfully", Toast.LENGTH_SHORT).show();
            }
        }
        SSLSendAsync usersend=new SSLSendAsync();
        usersend.execute();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mMap.setOnMarkerDragListener(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] {ACCESS_FINE_LOCATION},SSLUtil.REQUEST_FINE_LOCATION);
            }else{
                permissionIsGranted=true;
            }
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                return true;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case SSLUtil.REQUEST_FINE_LOCATION:
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    permissionIsGranted=true;
                }else{
                    permissionIsGranted=false;
                    Toast.makeText(this, "This app Required Location Permission", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SSLUtil.PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(SSLUtil.TAG, "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.i(SSLUtil.TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
            }
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        Log.e(SSLUtil.TAG,"MArker Drag Start");
        Toast.makeText(this, "Marker Drag start", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        Toast.makeText(this, "On Marker Drag" , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        LatLng latLng=marker.getPosition();
        mLatitude=latLng.latitude;
        mLongitude=latLng.longitude;
        Toast.makeText(this, "Marker Drag End", Toast.LENGTH_SHORT).show();
    }
}

package com.example.vishalmunjani.geoshare;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_PHONE_STATE;

/**
 * Created by vishal.munjani on 4/15/2017.
 */
public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks{

    private final SecureRandom secureRandom = new SecureRandom();
    GoogleApiClient mGoogleApiClient;
    String decodedPayload;
    EditText edEmail,edPass;
    Button signIn;
    ProgressDialog progressDialog;
    TextView tvSerialNumber;
    String email,pass;
    String type="mlogin";
    String serialNumber=null;
    String result=null;
    boolean isConnected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] {ACCESS_FINE_LOCATION,READ_PHONE_STATE},SSLUtil.REQUEST_FINE_LOCATION);
            }else{
            }
        }

        progressDialog=ProgressDialog.show(this, "Checking Device Compatibility", "Please wait...",false,false);
        buildGoogleApiClient();
        safetyNetRootTest();
    }

    private void safetyNetRootTest() {
        mGoogleApiClient.connect();
        byte[] nonce = getRequestNonce();
        SafetyNet.SafetyNetApi.attest(mGoogleApiClient, nonce)
                .setResultCallback(new ResultCallback<SafetyNetApi.AttestationResult>() {
                    @Override
                    public void onResult(SafetyNetApi.AttestationResult result) {
                        Status status = result.getStatus();
                        if (status.isSuccess()) {
                            Toast.makeText(MainActivity.this, "Connection Successfull", Toast.LENGTH_SHORT).show();
                            // Indicates communication with the service was successful.
                            // Use result.getJwsResult() to get the result data.
                            String jwsResult=result.getJwsResult();
                            final String[] jwtParts = jwsResult.split("\\.");
                            if (jwtParts.length == 3) {
                                decodedPayload = new String(Base64.decode(jwtParts[1], Base64.DEFAULT));
                                try {
                                    JSONObject response= new JSONObject(decodedPayload);
                                    if(response.getBoolean(SSLUtil.CTS_PROFILE_MATCH)){
                                        progressDialog.dismiss();
                                        setContentView(R.layout.activity_main);
                                        Toast.makeText(MainActivity.this, "Device is not Rooted", Toast.LENGTH_LONG).show();
                                        edEmail=(EditText)findViewById(R.id.edEmail);
                                        edPass=(EditText)findViewById(R.id.edPass);
                                        signIn=(Button)findViewById(R.id.signIn);
                                        tvSerialNumber=(TextView)findViewById(R.id.textView);
                                        Process process = null;
                                        try
                                        {
                                            process = Runtime.getRuntime().exec(SSLUtil.GET_SERIAL);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        try{
                                            BufferedReader bufR = new BufferedReader(new InputStreamReader(process.getInputStream()));
                                            String line;
                                            while ((line = bufR.readLine()) != null) {
                                                tvSerialNumber.append(line);
                                            }
                                        } catch (NullPointerException | IOException e) {
                                            e.printStackTrace();
                                        }
                                        signIn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                email=edEmail.getText().toString();
                                                pass=edPass.getText().toString();
                                                serialNumber=tvSerialNumber.getText().toString();
                                                isConnected=checkConnected();
                                                if(isConnected){
                                                    sslUserLogin(email,pass,serialNumber,type);
                                                }
                                                else{
                                                    Toast.makeText(MainActivity.this, "Please Connect To Internet", Toast.LENGTH_LONG).show();
                                                    edEmail.setText("");
                                                    edPass.setText("");
                                                }
                                            }
                                        });
                                    }
                                    else if(response.getBoolean(SSLUtil.BASIC_INTEGRITY)){
                                        Toast.makeText(MainActivity.this, "Device is Rooted", Toast.LENGTH_SHORT).show();
                                        MainActivity.this.finish();
                                    }
                                    else{
                                        Toast.makeText(MainActivity.this, "Device is Rooted", Toast.LENGTH_SHORT).show();
                                        MainActivity.this.finish();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Connection failed", Toast.LENGTH_SHORT).show();
                            MainActivity.this.finish();
                        }
                    }
                });
    }

    private boolean checkConnected() {
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    private void sslUserLogin(final String email, String pass, String serialNumber, String type) {

        class SSLUserLoginAsync extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                try{
                    CertificateFactory cf = CertificateFactory.getInstance(SSLUtil.CERTIFICATE_TYPE);
                    InputStream is = getResources().openRawResource(R.raw.server);
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

                    JSONObject jsonSendUser = new JSONObject();
                    jsonSendUser.put(SSLUtil.TYPE,params[3]);
                    jsonSendUser.put(SSLUtil.CHECK_EXIST,params[0]);
                    jsonSendUser.put(SSLUtil.CHECK_PASS,params[1]);
                    jsonSendUser.put(SSLUtil.DEV_SERIAL,params[2]);

                    PrintWriter pw = new PrintWriter(sslSocket.getOutputStream(), true);
                    //outp.println("a"+jsonSendUser);
                    pw.println(jsonSendUser);

                    BufferedInputStream bufferedInputStream = new
                            BufferedInputStream(sslSocket.getInputStream());
                    Log.e(SSLUtil.TAG,"Check point 3");
                    StringBuffer sb=new StringBuffer();
                    int i;
                    while(true){
                        i=bufferedInputStream.read();
                        sb.append((char)i);
                        if(bufferedInputStream.available()==0){
                            break;
                        }
                    }
                    result=sb.toString();
                    sslSocket.close();
                } catch (CertificateException e) {
                    Toast.makeText(MainActivity.this, "Certificate Verify Error :"+e.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (IOException e){
                    Toast.makeText(MainActivity.this, "Input Output Excetion :"+e.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (NoSuchAlgorithmException e) {
                    Toast.makeText(MainActivity.this, "No such Algo  :"+e.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (KeyStoreException e) {
                    Toast.makeText(MainActivity.this, "Key Store :"+e.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (KeyManagementException e) {
                    Toast.makeText(MainActivity.this, "Key Manager :"+e.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog=ProgressDialog.show(MainActivity.this, "Verifying ......", "Please Wait.......",false,false);
                progressDialog.show();
            }

            @Override
            protected void onPostExecute(String s) {
                progressDialog.dismiss();
                if(s.equals("True")){
                    Toast.makeText(MainActivity.this, "Login Succesful", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(MainActivity.this,Home.class);
                    SharedPreferences.Editor editor = getSharedPreferences(SSLUtil.PREFERENCE_NAME, MODE_PRIVATE).edit();
                    editor.putString(SSLUtil.EMAIL,email );
                    editor.commit();
                    startActivity(intent);
                }
                else{
                    Toast.makeText(MainActivity.this, "Login Failed !! Check Email or Password", Toast.LENGTH_SHORT).show();
                    edEmail.setText("");
                    edPass.setText("");
                }


            }
        }
        SSLUserLoginAsync userLoginAsync=new SSLUserLoginAsync();
        userLoginAsync.execute(email,pass,serialNumber,type);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(SafetyNet.API)
                .addConnectionCallbacks(this)
                .build();
    }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(MainActivity.this, "Connection failed", Toast.LENGTH_SHORT).show();
        MainActivity.this.finish();
    }

    private byte[] getRequestNonce() {
        byte[] nonce = new byte[32];
        secureRandom.nextBytes(nonce);
        return nonce;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case SSLUtil.REQUEST_FINE_LOCATION:
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "This app Required Location Permission", Toast.LENGTH_SHORT).show();
                }
                return;
            case SSLUtil.REQUEST_READ_STATE:
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "This app Required Location Permission", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }
}

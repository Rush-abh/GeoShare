package com.example.vishalmunjani.geoshare;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.graphics.BitmapCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
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

public class Decrypt extends AppCompatActivity {
    Button btnSend;
    String  mLatitude;
    String mLongitude;
    ProgressDialog progressDialog;
    String email=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decrypt);
        btnSend=(Button)findViewById(R.id.button2);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendLatLang();
                Toast.makeText(Decrypt.this, "Send Data SuccessFully", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void sendLatLang() {

        class SSLSendAsync extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                SharedPreferences prefs = getSharedPreferences(SSLUtil.PREFERENCE_NAME, Decrypt.this.MODE_PRIVATE);
                email = prefs.getString(SSLUtil.EMAIL,null);
                mLatitude=prefs.getString(SSLUtil.MYLATITUDE,null);
                mLongitude=prefs.getString(SSLUtil.MYLONGITUDE,null);
                try{
                    CertificateFactory cf = CertificateFactory.getInstance(SSLUtil.CERTIFICATE_TYPE);
                    InputStream is = Decrypt.this.getResources().openRawResource(R.raw.server);
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
                    jsonSendLatLang.put(SSLUtil.SUBTYPE,SSLUtil.SUBTYPE_DECRYPT);
                    jsonSendLatLang.put(SSLUtil.EMAIL,email);
                    jsonSendLatLang.put(SSLUtil.LATITUDE,mLatitude);
                    jsonSendLatLang.put(SSLUtil.LONGITUDE,mLongitude);

                    PrintWriter outp = new PrintWriter(sslSocket.getOutputStream(), true);
                    outp.println(jsonSendLatLang);
                    sslSocket.close();
                } catch (CertificateException e) {
                    Toast.makeText(Decrypt.this, "Certificate Verify Error :"+e.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (IOException e){
                    Toast.makeText(Decrypt.this, "Input Output Excetion :"+e.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (NoSuchAlgorithmException e) {
                    Toast.makeText(Decrypt.this, "No such Algo  :"+e.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (KeyStoreException e) {
                    Toast.makeText(Decrypt.this, "Key Store :"+e.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (KeyManagementException e) {
                    Toast.makeText(Decrypt.this, "Key Manager :"+e.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return "True";
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog= ProgressDialog.show(Decrypt.this, "Sending Data", "Please Wait.......",false,false);
                progressDialog.show();
            }

            @Override
            protected void onPostExecute(String s) {
                progressDialog.dismiss();
                Toast.makeText(Decrypt.this, "Send data successfully", Toast.LENGTH_SHORT).show();
            }
        }
        SSLSendAsync usersend=new SSLSendAsync();
        usersend.execute();
    }
}

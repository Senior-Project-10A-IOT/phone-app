package com.example.testapp;

import android.app.Application;
import android.content.res.Configuration;
import android.os.StrictMode;
import android.util.Log;

import com.amazonaws.auth.AWSSessionCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;
import com.amazonaws.regions.Regions;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.UUID;

public class SecurityApplication extends Application {
    public static final String SHADOW_PREFIX = "$aws/things/raspi/shadow";

    public static final String SHADOW_GET_TOPIC = SHADOW_PREFIX + "/get";
    public static final String SHADOW_GET_ACCEPTED_TOPIC = SHADOW_GET_TOPIC + "/accepted";
    public static final String SHADOW_GET_REJECTED_TOPIC = SHADOW_GET_TOPIC + "/rejected";

    public static final String SHADOW_UPDATE_TOPIC = SHADOW_PREFIX + "/update";
    public static final String SHADOW_UPDATE_DOCUMENTS_TOPIC = SHADOW_UPDATE_TOPIC + "/documents";

    private static final String CUSTOMER_SPECIFIC_ENDPOINT = "aw62mbu5dp5po-ats.iot.us-east-1.amazonaws.com";
    private static final String COGNITO_POOL_ID = "us-east-1:11c5508c-25db-4d40-baf1-6e15da71b02a";
    private static final Regions MY_REGION = Regions.US_EAST_1;
    public static RequestQueue requestQueue;
    public static AWSIotMqttManager mqttManager;
    public static CognitoCachingCredentialsProvider credentialsProvider;
    String clientId;

    @Override
    public void onCreate() {
        super.onCreate();
        StrictMode.ThreadPolicy bruh = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(bruh);

        // Initialize the AWS Cognito credentials provider
        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                COGNITO_POOL_ID,
                MY_REGION
        );
        credentialsProvider.refresh();

        Log.e("efjioajioejwa", String.valueOf(credentialsProvider.getLogins()));
        AWSSessionCredentials creds = credentialsProvider.getCredentials();
        Log.e("AAAAAAAAAAAA", creds.getSessionToken());

        clientId = UUID.randomUUID().toString();
        mqttManager = new AWSIotMqttManager(clientId, CUSTOMER_SPECIFIC_ENDPOINT);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfiguration) {
        super.onConfigurationChanged(newConfiguration);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}

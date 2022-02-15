package com.example.testapp;

import android.app.Application;
import android.content.res.Configuration;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;
import com.amazonaws.regions.Regions;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.UUID;

public class SecurityApplication extends Application {
    private static final String CUSTOMER_SPECIFIC_ENDPOINT = "aw62mbu5dp5po-ats.iot.us-east-1.amazonaws.com";
    private static final String COGNITO_POOL_ID = "us-east-1:11c5508c-25db-4d40-baf1-6e15da71b02a";
    private static final Regions MY_REGION = Regions.US_EAST_1;
    public static RequestQueue requestQueue;
    public static AWSIotMqttManager mqttManager;
    public static CognitoCachingCredentialsProvider credentialsProvider;
    String clientId;
    public static final String GET_TOPIC = "$aws/things/raspi/shadow/get";
    public static final String GET_ACCEPTED_TOPIC = GET_TOPIC + "/accepted";
    public static final String GET_REJECTED_TOPIC = GET_TOPIC + "/rejected";

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize the AWS Cognito credentials provider
        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                COGNITO_POOL_ID,
                MY_REGION
        );

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

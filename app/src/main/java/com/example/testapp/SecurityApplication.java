package com.example.testapp;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttNewMessageCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;
import com.amazonaws.regions.Regions;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SecurityApplication extends Application {
    public static RequestQueue requestQueue;


    static final String LOG_TAG = SecurityApplication.class.getCanonicalName();

    // --- Constants to modify per your configuration ---

    // Customer specific IoT endpoint
    // AWS Iot CLI describe-endpoint call returns: XXXXXXXXXX.iot.<region>.amazonaws.com,
    private static final String CUSTOMER_SPECIFIC_ENDPOINT = "aw62mbu5dp5po-ats.iot.us-east-1.amazonaws.com";

    // Cognito pool ID. For this app, pool needs to be unauthenticated pool with
    // AWS IoT permissions.
    private static final String COGNITO_POOL_ID = "us-east-1:11c5508c-25db-4d40-baf1-6e15da71b02a";

    // Region of AWS IoT
    private static final Regions MY_REGION = Regions.US_EAST_1;

    public static AWSIotMqttManager mqttManager;
    String clientId;

    public static CognitoCachingCredentialsProvider credentialsProvider;

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize the AWS Cognito credentials provider
        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(), // context
                COGNITO_POOL_ID, // Identity Pool ID
                MY_REGION // Region
        );

        // MQTT Client
        clientId = UUID.randomUUID().toString();
        mqttManager = new AWSIotMqttManager(clientId, CUSTOMER_SPECIFIC_ENDPOINT);

        // The following block uses a Cognito credentials provider for authentication with AWS IoT.
        //new Thread(new Runnable() {
        //    @Override
        //    public void run() {
        //        runOnUiThread(new Runnable() {
        //            @Override
        //            public void run() {
        //                btnConnect.setEnabled(true);
        //            }
        //        });
        //    }
        //}).start();

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        retryRequest(false);
    }

    public void retryRequest(boolean doDelay) {
        int delay = 1;
        if (!doDelay) delay = 0;

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(DownloadWorker.class)
                .setInitialDelay(delay, TimeUnit.SECONDS).build();
        WorkManager.getInstance(getApplicationContext())
                .enqueueUniqueWork(DownloadWorker.UNIQUE_NAME, ExistingWorkPolicy.REPLACE, workRequest);
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

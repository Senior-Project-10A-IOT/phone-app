package com.example.testapp;

import android.app.Application;
import android.content.res.Configuration;
import android.os.StrictMode;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.neovisionaries.ws.client.WebSocketFactory;

public class SecurityApplication extends Application {
    public static RequestQueue requestQueue;
    public static String TAG = "secapp";
    public static WebSocketFactory factory;

    public static boolean armed = false;

    public static void logErr(String info) {
        Log.e(TAG, info);
    }

    public static void logInfo(String info) {
        Log.i(TAG, info);
    }

    public static void logDebug(String info) {
        Log.d(TAG, info);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // TODO move web socket handling to a Worker and do requests
        StrictMode.ThreadPolicy bruh = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(bruh);

        factory = new WebSocketFactory();
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

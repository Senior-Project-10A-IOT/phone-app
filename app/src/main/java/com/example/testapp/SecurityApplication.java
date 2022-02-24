package com.example.testapp;

import android.app.Application;
import android.content.res.Configuration;
import android.os.StrictMode;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;

import java.io.IOException;

public class SecurityApplication extends Application {
    public static RequestQueue requestQueue;
    public static String TAG = "secapp";
    public static WebSocket ws;

    @Override
    public void onCreate() {
        super.onCreate();
        StrictMode.ThreadPolicy bruh = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(bruh);

        WebSocketFactory factory = new WebSocketFactory();
        try {
            //WebSocket ws = factory.createSocket("ws://xn--yh8hfqgj.ws:8765/");
            ws = factory.createSocket("ws://10.0.2.2:8765/");
            ws.connect();
        } catch (IOException | WebSocketException e) {
            Log.e(TAG, "web socket: " + e);
        }

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

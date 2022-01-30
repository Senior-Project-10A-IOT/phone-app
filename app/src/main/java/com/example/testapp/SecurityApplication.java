package com.example.testapp;

import android.app.Application;
import android.content.res.Configuration;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.UUID;

public class SecurityApplication extends Application {
    public static UUID request;
    public static RequestQueue requestQueue;

    @Override
    public void onCreate() {
        super.onCreate();

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        WorkRequest workRequest = OneTimeWorkRequest.from(DownloadWorker.class);
        request = workRequest.getId();
        WorkManager.getInstance(getApplicationContext()).enqueue(workRequest);
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

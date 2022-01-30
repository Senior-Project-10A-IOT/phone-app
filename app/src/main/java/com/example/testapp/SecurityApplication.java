package com.example.testapp;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SecurityApplication extends Application {
    public static RequestQueue requestQueue;

    @Override
    public void onCreate() {
        super.onCreate();

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        retryRequest();
    }

    public void retryRequest() {
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(DownloadWorker.class)
                .setInitialDelay(1, TimeUnit.SECONDS).build();
        WorkManager.getInstance(getApplicationContext())
                .enqueueUniqueWork(DownloadWorker.UNIQUE_NAME, ExistingWorkPolicy.REPLACE, workRequest);
        Log.d("security", "retry network request");
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

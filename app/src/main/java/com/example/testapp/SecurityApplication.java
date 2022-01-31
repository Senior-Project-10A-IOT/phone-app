package com.example.testapp;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.concurrent.TimeUnit;

public class SecurityApplication extends Application {
    public static RequestQueue requestQueue;

    @Override
    public void onCreate() {
        super.onCreate();

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

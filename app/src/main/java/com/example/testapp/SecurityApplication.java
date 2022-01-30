package com.example.testapp;

import android.app.Application;
import android.content.res.Configuration;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import java.util.UUID;

public class SecurityApplication extends Application {
    public static UUID REQUEST;

    @Override
    public void onCreate() {
        super.onCreate();

        WorkRequest workRequest = OneTimeWorkRequest.from(DownloadWorker.class);
        REQUEST = workRequest.getId();
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

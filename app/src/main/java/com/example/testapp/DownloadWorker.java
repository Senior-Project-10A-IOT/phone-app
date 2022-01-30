package com.example.testapp;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.Request;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class DownloadWorker extends Worker {
    public static final String UNIQUE_NAME = "downloadWorker";

    public DownloadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        //String url = "https://xn--yh8hfqgj.ws/";
        String url = "https://www.timeapi.io/api/Time/current/zone?timeZone=America/New_York";
        RequestFuture<String> future = RequestFuture.newFuture();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, future, future);
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);

        try {
            String response = future.get();
            Data data = new Data.Builder().putString("", response).build();
            return Result.success(data);
        } catch (Exception e) {
            Log.e("downloadworker", e.toString());
            Data data = new Data.Builder().putString("", e.toString()).build();
            return Result.failure(data);
        }
    }
}

package com.example.testapp;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.Request;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class DatabaseWorker extends Worker {
    public DatabaseWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    public static final String UNIQUE_NAME = "jfewioijoefjiow";

    @NonNull
    @Override
    public Result doWork() {
        try {
            Thread.sleep(5000);
        } catch (Exception e) {
        }

        String url = "http://gang-and-friends.com:8764/";
        RequestFuture<String> future = RequestFuture.newFuture();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, future, future);
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);

        try {
            String response = future.get();
            Data data = new Data.Builder().putString("", response).build();
            return Result.success(data);
        } catch (Exception e) {
            Data data = new Data.Builder().putString("", e.toString()).build();
            return Result.failure(data);
        }
    }
}

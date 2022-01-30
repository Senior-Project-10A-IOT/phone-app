package com.example.testapp;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.security.cert.Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;

public class DownloadWorker extends Worker {
    public DownloadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            Thread.sleep(5000);
        } catch (Exception e) {}

        String url = "https://xn--yh8hfqgj.ws";
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

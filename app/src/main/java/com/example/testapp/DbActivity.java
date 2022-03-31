package com.example.testapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.UUID;

public class DbActivity extends AppCompatActivity {
    DbAdapter adapter;
    public static RequestQueue requestQueue;
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db);

        ArrayList<DbAdapter.DbItem> list = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.db_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DbAdapter(this, list);
        recyclerView.setAdapter(adapter);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        WorkRequest workRequest = OneTimeWorkRequest.from(DownloadWorker.class);
        UUID requestId = workRequest.getId();
        WorkManager.getInstance(getApplicationContext()).enqueue(workRequest);

        WorkManager
                .getInstance(getApplicationContext())
                .getWorkInfoByIdLiveData(requestId)
                .observe(this, workInfo -> {
                    if (workInfo.getState() != null && workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                        String rawJson = workInfo.getOutputData().getString("");
                        Type listType = new TypeToken<ArrayList<DbAdapter.DbItem>>(){}.getType();
                        ArrayList<DbAdapter.DbItem> newList = gson.fromJson(rawJson, listType);
                        for (DbAdapter.DbItem newItem : newList) {
                            Log.e("", "" + newItem);
                            list.add(0, newItem);
                            adapter.notifyItemInserted(0);
                        }
                    }
                });
    }
}
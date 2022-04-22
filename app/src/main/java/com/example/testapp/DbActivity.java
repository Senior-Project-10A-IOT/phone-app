package com.example.testapp;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

public class DbActivity extends AppCompatActivity {
    public static RequestQueue requestQueue;
    DbAdapter adapter;
    Gson gson = new Gson();
    HashMap<UUID, Integer> imageRequests = new HashMap<>();

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
                        Type listType = new TypeToken<ArrayList<ResponseRow>>() {
                        }.getType();

                        SecurityApplication.logInfo("responded with json " + rawJson);
                        ArrayList<ResponseRow> newList = gson.fromJson(rawJson, listType);

                        if (newList != null) {
                            Collections.reverse(newList);
                            for (ResponseRow row : newList) {
                                list.add(0, new DbAdapter.DbItem(row.timestamp, row.id));
                                adapter.notifyItemInserted(0);
                            }
                        }
                    } else if (workInfo.getState() != null && workInfo.getState() == WorkInfo.State.FAILED) {
                        String errormessage = workInfo.getOutputData().getString("");
                        Toast.makeText(getApplicationContext(), "DB query failed" + errormessage, Toast.LENGTH_SHORT).show();
                        SecurityApplication.logErr("db query " + errormessage);
                    }
                });
    }

    public static class ResponseRow {
        String timestamp;
        Integer id;

        public ResponseRow(String uuid, Integer imageId) {
            this.timestamp = uuid;
            this.id = imageId;
        }
    }
}
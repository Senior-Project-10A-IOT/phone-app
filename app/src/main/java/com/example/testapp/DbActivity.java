package com.example.testapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class DbActivity extends AppCompatActivity {
    DbAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db);

        ArrayList<DbAdapter.DbItem> list = new ArrayList<>();
        list.add(new DbAdapter.DbItem());
        list.add(new DbAdapter.DbItem());
        list.add(new DbAdapter.DbItem());
        list.add(new DbAdapter.DbItem());

        RecyclerView recyclerView = findViewById(R.id.db_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DbAdapter(this, list);
        recyclerView.setAdapter(adapter);
    }
}
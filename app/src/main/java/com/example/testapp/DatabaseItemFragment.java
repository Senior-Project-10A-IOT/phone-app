package com.example.testapp;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.testapp.placeholder.PlaceholderContent;

/**
 * A fragment representing a list of Items.
 */
public class DatabaseItemFragment extends Fragment {

    private void doWorkerStuff() {
        WorkManager.getInstance(getContext())
                .getWorkInfosForUniqueWorkLiveData(DatabaseWorker.UNIQUE_NAME)
                .observe(getViewLifecycleOwner(), workInfos -> {
                    // TODO
                    WorkInfo workInfo = workInfos.get(0);
                    if (workInfo.getState() != null && workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                        //binding.networkText.setText(workInfo.getOutputData().getString(""));
                        //((SecurityApplication) getActivity().getApplication()).retryRequest();
                    } else if (workInfo.getState() != null && workInfo.getState() == WorkInfo.State.FAILED) {
                        //binding.networkText.setText("Network failed");
                    }
                    //} else if (firstLoad) {
                    //    //binding.networkText.setText("Loading...");
                    //    //firstLoad = false;
                    //}
                });
    }

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DatabaseItemFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static DatabaseItemFragment newInstance(int columnCount) {
        DatabaseItemFragment fragment = new DatabaseItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new DatabaseViewAdapter(PlaceholderContent.ITEMS));
        }
        return view;
    }
}
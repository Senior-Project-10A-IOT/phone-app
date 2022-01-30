package com.example.testapp;

import android.app.PendingIntent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDeepLinkBuilder;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.testapp.databinding.FragmentFirstBinding;

public class FirstFragment extends Fragment {
    private FragmentFirstBinding binding;
    private boolean firstLoad = true;

    private void doWorkerStuff() {
        WorkManager.getInstance(getContext())
                .getWorkInfosForUniqueWorkLiveData(DownloadWorker.UNIQUE_NAME)
                .observe(getViewLifecycleOwner(), workInfos -> {
                    WorkInfo workInfo = workInfos.get(0);
                    if (workInfo.getState() != null && workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                        binding.networkText.setText(workInfo.getOutputData().getString(""));
                        ((SecurityApplication) getActivity().getApplication()).retryRequest();
                    } else if (workInfo.getState() != null && workInfo.getState() == WorkInfo.State.FAILED) {
                        binding.networkText.setText("Network failed");
                    } else if (firstLoad) {
                        binding.networkText.setText("Loading...");
                        firstLoad = false;
                    }
                });
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);

        binding.retry.setOnClickListener(view -> {
            ((SecurityApplication)getActivity().getApplication()).retryRequest();
            doWorkerStuff();
        });

        binding.button.setOnClickListener(view -> {
            PendingIntent pendingIntent = new NavDeepLinkBuilder(getContext())
                    .setComponentName(MainActivity.class)
                    .setGraph(R.navigation.nav_graph)
                    .setDestination(R.id.FirstFragment)
                    .setArguments(getArguments())
                    .createPendingIntent();

            NotificationCompat.Builder b = new NotificationCompat.Builder(getActivity(), MainActivity.CHANNEL);
            b.setSmallIcon(R.drawable.ic_launcher_foreground);
            b.setContentText("jifeowjioefw");
            b.setContentTitle("jie88888");
            b.setContentIntent(pendingIntent);
            b.setAutoCancel(true);
            ((MainActivity) getActivity()).man.notify(new java.util.Random().nextInt(), b.build());
        });

        doWorkerStuff();

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
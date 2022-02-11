package com.example.testapp;

import android.app.PendingIntent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDeepLinkBuilder;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttLastWillAndTestament;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttNewMessageCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;
import com.example.testapp.databinding.FragmentFirstBinding;

import java.io.UnsupportedEncodingException;

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
                        ((SecurityApplication) getActivity().getApplication()).retryRequest(true);
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
            ((SecurityApplication) getActivity().getApplication()).retryRequest(false);
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
            b.setContentText("This is a test notification.");
            b.setContentTitle("Security alert");
            b.setContentIntent(pendingIntent);
            b.setAutoCancel(true);
            ((MainActivity) getActivity()).man.notify(new java.util.Random().nextInt(), b.build());
        });

        SecurityApplication.mqttManager.connect(SecurityApplication.credentialsProvider, new AWSIotMqttClientStatusCallback() {
            @Override
            public void onStatusChanged(AWSIotMqttClientStatus status, Throwable throwable) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (status == AWSIotMqttClientStatus.Connecting) {
                            binding.connectionStatus.setText("Connecting...");

                        } else if (status == AWSIotMqttClientStatus.Connected) {
                            binding.connectionStatus.setText("Connected");

                        } else if (status == AWSIotMqttClientStatus.Reconnecting) {
                            if (throwable != null) {
                                Log.e("ack", "Connection error.", throwable);
                            }
                            binding.connectionStatus.setText("Reconnecting");
                        } else if (status == AWSIotMqttClientStatus.ConnectionLost) {
                            if (throwable != null) {
                                Log.e("ack", "Connection error.", throwable);
                                throwable.printStackTrace();
                            }
                            binding.connectionStatus.setText("Disconnected");
                        } else {
                            binding.connectionStatus.setText("Disconnected");
                        }
                    }
                });
            }
        });

        binding.shadowMessage.setText("no response from device...");

        SecurityApplication.mqttManager.subscribeToTopic("$aws/things/raspi/shadow/get", AWSIotMqttQos.QOS0, new AWSIotMqttNewMessageCallback() {
            @Override
            public void onMessageArrived(String topic, byte[] data) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String message = new String(data, "UTF-8");
                            binding.shadowMessage.setText(message);
                        } catch (UnsupportedEncodingException e) {
                            binding.shadowMessage.setText(e.toString());
                        }
                    }
                });
            }
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
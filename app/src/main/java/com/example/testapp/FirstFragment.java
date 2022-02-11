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

import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;
import com.example.testapp.databinding.FragmentFirstBinding;

import java.io.UnsupportedEncodingException;

public class FirstFragment extends Fragment {
    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);

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

        SecurityApplication.mqttManager.connect(
            SecurityApplication.credentialsProvider,
            (status, throwable) -> getActivity().runOnUiThread(() -> {
                if (status == AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.Connecting) {
                    binding.connectionStatus.setText("Connecting...");

                } else if (status == AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.Connected) {
                    binding.connectionStatus.setText("Connected");

                } else if (status == AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.Reconnecting) {
                    if (throwable != null) {
                        Log.e("ack", "Connection error.", throwable);
                    }
                    binding.connectionStatus.setText("Reconnecting");
                } else if (status == AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.ConnectionLost) {
                    if (throwable != null) {
                        Log.e("ack", "Connection error.", throwable);
                        throwable.printStackTrace();
                    }
                    binding.connectionStatus.setText("Disconnected");
                } else {
                    binding.connectionStatus.setText("Disconnected");
                }
            })
        );

        binding.shadowMessage.setText("no response from device...");

        SecurityApplication.mqttManager.subscribeToTopic(
            "$aws/things/raspi/shadow/get",
            AWSIotMqttQos.QOS0,
            (topic, data) -> getActivity().runOnUiThread(() -> {
                try {
                    String message = new String(data, "UTF-8");
                    binding.shadowMessage.setText(message);
                } catch (UnsupportedEncodingException e) {
                    binding.shadowMessage.setText(e.toString());
                }
            })
        );

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
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
import com.amazonaws.mobileconnectors.iot.AWSIotMqttSubscriptionStatusCallback;
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

        binding.testNoto.setOnClickListener(view -> {
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
                SecurityApplication.SHADOW_GET_ACCEPTED_TOPIC,
                AWSIotMqttQos.QOS1,
                new AWSIotMqttSubscriptionStatusCallback() {
                    @Override
                    public void onSuccess() {
                        Log.i("", "accepted successfully subscribed");
                    }

                    @Override
                    public void onFailure(Throwable exception) {
                        Log.e("", "accepted could not subscribe", exception);
                    }
                },
                (topic, data) -> getActivity().runOnUiThread(() -> {
                    Log.e("", "accepted");
                    try {
                        String message = new String(data, "UTF-8");
                        binding.shadowMessage.setText(topic + " " + message);
                    } catch (UnsupportedEncodingException e) {
                        binding.shadowMessage.setText(topic + " " + e);
                    }
                })
        );

        //SecurityApplication.mqttManager.subscribeToTopic(
        //        SecurityApplication.GET_REJECTED_TOPIC,
        //        AWSIotMqttQos.QOS1,
        //        (topic, data) -> getActivity().runOnUiThread(() -> {
        //            Log.e("", "rejected");
        //            try {
        //                String message = new String(data, "UTF-8");
        //                binding.shadowMessage.setText(topic + " " + message);
        //            } catch (UnsupportedEncodingException e) {
        //                binding.shadowMessage.setText(topic + " " + e);
        //            }
        //        })
        //);

        SecurityApplication.mqttManager.subscribeToTopic(
                SecurityApplication.SHADOW_UPDATE_DOCUMENTS_TOPIC,
                AWSIotMqttQos.QOS0,
                (topic, data) -> getActivity().runOnUiThread(() -> {
                    Log.e("", "accepted");
                    try {
                        String message = new String(data, "UTF-8");
                        binding.shadowMessage.setText(topic + " " + message);
                    } catch (UnsupportedEncodingException e) {
                        binding.shadowMessage.setText(topic + " " + e);
                    }
                })
        );

        binding.publishSomething.setOnClickListener(view -> {
            Log.d("", "publish get");
            byte[] data = {};
            SecurityApplication.mqttManager.publishData(data, SecurityApplication.SHADOW_GET_TOPIC, AWSIotMqttQos.QOS0);
        });

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
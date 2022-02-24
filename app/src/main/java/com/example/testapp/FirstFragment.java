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

import com.example.testapp.databinding.FragmentFirstBinding;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;

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

        binding.shadowMessage.setText("no response from device...");
        SecurityApplication.ws.addListener(new WebSocketAdapter() {
            @Override
            public void onTextMessage(WebSocket ws, String message) {
                binding.shadowMessage.setText(message);
            }
        });

        binding.publishSomething.setOnClickListener(view -> SecurityApplication.ws.sendText("phone"));

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
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
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFrame;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class FirstFragment extends Fragment {
    private FragmentFirstBinding binding;
    private Listener listener;
    private WebSocket ws;
    private boolean connected;

    private void makeSocket() {
        try {
            ws = SecurityApplication.factory.createSocket("ws://gang-and-friends.com:8765/");
        } catch (IOException e) {
            Log.e(SecurityApplication.TAG, "create socket: " + e);
            setDisconnectedState();
            return;
        }

        ws.addListener(listener);
        try {
            ws.connect();
            setConnectedState();
        } catch (WebSocketException e) {
            Log.e(SecurityApplication.TAG, "connect: " + e);
            setDisconnectedState();
        }
    }

    private void setConnectedState() {
        binding.connectDisconnect.setText("disconnect");
        binding.connectionStatus.setText("connected");
        connected = true;
    }

    private void setDisconnectedState() {
        binding.connectDisconnect.setText("connect");
        binding.connectionStatus.setText("not connected");
        connected = false;
    }

    private void makeNoto(String contentText) {
        PendingIntent pendingIntent = new NavDeepLinkBuilder(getContext())
                .setComponentName(MainActivity.class)
                .setGraph(R.navigation.nav_graph)
                .setDestination(R.id.FirstFragment)
                .setArguments(getArguments())
                .createPendingIntent();

        NotificationCompat.Builder b = new NotificationCompat.Builder(getActivity(), MainActivity.CHANNEL);
        b.setSmallIcon(R.drawable.ic_launcher_foreground);
        b.setContentTitle("Security alert");
        b.setContentText(contentText);
        b.setContentIntent(pendingIntent);
        b.setAutoCancel(true);
        ((MainActivity) getActivity()).man.notify(new java.util.Random().nextInt(), b.build());
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);

        binding.testNoto.setOnClickListener(view -> {
            makeNoto("This is a test notification.");
        });

        setDisconnectedState();
        listener = new Listener();

        binding.sendMessage.setOnClickListener(view -> {
            if (ws == null)
                makeSocket();

            ws.sendText("phone");
        });

        binding.connectDisconnect.setOnClickListener(view -> {
            if (connected) {
                ws.disconnect();
                setDisconnectedState();
            } else {
                setConnectedState();
                makeSocket();
            }
        });

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ws.disconnect();
        setDisconnectedState();
        binding = null;
    }

    private class Listener extends WebSocketAdapter {
        @Override
        public void onTextMessage(WebSocket ws, String message) {
            Log.e(SecurityApplication.TAG, message);

            if (message.endsWith("4")) {
                makeNoto("whoa is a lot");
            }

            binding.socketResponse.setText(message);
        }

        @Override
        public void onConnected(WebSocket ws, Map<String, List<String>> headers) {
            setConnectedState();
        }

        @Override
        public void onDisconnected(WebSocket ws, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) {
            setDisconnectedState();
        }
    }
}
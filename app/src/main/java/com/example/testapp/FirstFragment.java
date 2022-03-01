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
import com.neovisionaries.ws.client.WebSocketFrame;

import java.util.List;
import java.util.Map;

public class FirstFragment extends Fragment {
    private static Listener listener;
    private FragmentFirstBinding binding;

    private void setConnectedState() {
        binding.connectDisconnect.setText("disconnect");
        binding.connectionStatus.setText("connected to " + WebsocketWrapper.getCurrentServer());
    }

    private void setDisconnectedState() {
        if (WebsocketWrapper.isConnected()) {
            return;
        }

        binding.connectDisconnect.setText("connect");
        binding.connectionStatus.setText("not connected");
    }

    private void setResponse(String response) {
        binding.socketResponse.setText(response);
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
            WebsocketWrapper.sendText("phone");
        });

        binding.connectDisconnect.setOnClickListener(view -> {
            if (WebsocketWrapper.isConnected()) {
                WebsocketWrapper.disconnect();
            } else {
                WebsocketWrapper.connect(listener);
            }
        });

        binding.useRemote.setOnCheckedChangeListener((compoundButton, checked) -> {
            WebsocketWrapper.swapServer();

            if (WebsocketWrapper.isConnected()) {
                setConnectedState();
            } else {
                setDisconnectedState();
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
        WebsocketWrapper.disconnect();
        binding = null;
    }

    private class Listener extends WebSocketAdapter {
        @Override
        public void onTextMessage(WebSocket ws, String message) {
            Log.e(SecurityApplication.TAG, message);

            if (message.endsWith("4")) {
                makeNoto("whoa is a lot");
            }

            setResponse(message);
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
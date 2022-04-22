package com.example.testapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDeepLinkBuilder;

import com.example.testapp.databinding.FragmentFirstBinding;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFrame;

import java.util.List;
import java.util.Map;

public class FirstFragment extends Fragment {
    private static Listener listener;
    private FragmentFirstBinding binding;

    private void setConnectedState() {
        SecurityApplication.logDebug("connected to ws");
        binding.connectDisconnect.setText("disconnect");
    }

    private void setDisconnectedState() {
        SecurityApplication.logDebug("disconnected from ws");
        binding.connectDisconnect.setText("connect");
    }

    private void setArmedState() {
        SecurityApplication.logDebug("armed");
        binding.armDisarm.setText("disarm");
    }

    private void setDisarmedState() {
        SecurityApplication.logDebug("disarmed");
        binding.armDisarm.setText("arm");
    }

    private void makeNoto(String contentText) {
        PendingIntent pendingIntent = new NavDeepLinkBuilder(getContext())
                .setComponentName(MainActivity.class)
                .setGraph(R.navigation.nav_graph)
                .setDestination(R.id.FirstFragment)
                .setArguments(getArguments())
                .createPendingIntent();

        Intent christ = new Intent(Intent.ACTION_DIAL);
        christ.setData(Uri.parse("tel:" + PhoneNumbers.ZACK));
        PendingIntent pendingIntent1 = PendingIntent.getActivity(getContext(), 0, christ, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder b = new NotificationCompat.Builder(getActivity(), MainActivity.CHANNEL)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Security alert")
                .setContentText(contentText)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setDefaults(NotificationCompat.DEFAULT_LIGHTS | NotificationCompat.DEFAULT_SOUND)
                .addAction(R.drawable.ic_launcher_foreground, "Dial authorities", pendingIntent1);
        Notification noto = b.build();
        NotificationManager man = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        man.notify(1, noto);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);

        listener = new Listener();
        WebsocketWrapper.connect(listener, true);
        setDisarmedState();
        WebsocketWrapper.sendText(Messages.ARM);

        ExoPlayer player = new ExoPlayer.Builder(getContext())
                .setMediaSourceFactory(
                        new DefaultMediaSourceFactory(getContext())
                                .setLiveTargetOffsetMs(5000))
                .build();
        player.setPlayWhenReady(true);
        MediaItem mediaItem = new MediaItem.Builder()
                .setUri("rtmp://gang-and-friends.com:1935/live/stream")
                .setLiveConfiguration(
                        new MediaItem.LiveConfiguration.Builder()
                                .setMaxPlaybackSpeed(1.02f).
                                build()
                ).build();
        player.setMediaItem(mediaItem);

        StyledPlayerView styledPlayerView = binding.videoPlayer;
        styledPlayerView.setPlayer(player);

        binding.connectDisconnect.setOnClickListener(view -> {
            SecurityApplication.logErr("cdc " + WebsocketWrapper.isConnected());
            if (WebsocketWrapper.isConnected()) {
                WebsocketWrapper.disconnect();
            } else {
                WebsocketWrapper.connect(listener, ((MainActivity) getActivity()).isRemote());
            }
        });

        binding.showDB.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), DbActivity.class);
            startActivity(intent);
        });

        binding.armDisarm.setOnClickListener(view -> {
            if (SecurityApplication.armed) {
                WebsocketWrapper.sendText(Messages.DISARM);
            } else {
                WebsocketWrapper.sendText(Messages.ARM);
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
            SecurityApplication.logErr("message from pi '" + message + "'");

            //makeNoto("Oh no! Go check your stuff");
            if (message.equals(Messages.ARMED)) {
                SecurityApplication.armed = true;
                setArmedState();
                Toast.makeText(getActivity().getBaseContext(), "Armed!", Toast.LENGTH_SHORT).show();
            } else if (message.equals(Messages.DISARMED)) {
                SecurityApplication.armed = false;
                setDisarmedState();
                Toast.makeText(getActivity().getBaseContext(), "Disarmed", Toast.LENGTH_SHORT).show();
            } else {
                makeNoto("Oh no! " + message);
            }
        }

        @Override
        public void onConnected(WebSocket ws, Map<String, List<String>> headers) {
            setConnectedState();
        }

        @Override
        public void onDisconnected(WebSocket ws, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) {
            setDisconnectedState();
            WebsocketWrapper.disconnect();
        }

        @Override
        public void onError(WebSocket ws, WebSocketException cause) {
            SecurityApplication.logErr("error - " + cause);
            setDisconnectedState();
            WebsocketWrapper.disconnect();
        }
    }
}
package com.example.testapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.ui.StyledPlayerView;
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
    }

    private void setDisconnectedState() {
        if (WebsocketWrapper.isConnected()) {
            return;
        }

        binding.connectDisconnect.setText("connect");
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

        //setDisconnectedState();
        WebsocketWrapper.connect(listener, true);
        setConnectedState();
        listener = new Listener();

        ExoPlayer player = new ExoPlayer.Builder(getContext())
                .setMediaSourceFactory(
                        new DefaultMediaSourceFactory(getContext())
                                .setLiveTargetOffsetMs(5000))
                .build();
        MediaItem mediaItem = new MediaItem.Builder()
                .setUri("rtmp://gang-and-friends.com:1935/live/stream")
                .setLiveConfiguration(
                        new MediaItem.LiveConfiguration.Builder()
                                .setMaxPlaybackSpeed(1.02f).
                                build()
                ).build();
        player.setMediaItem(mediaItem);
        player.setPlayWhenReady(true);

        StyledPlayerView styledPlayerView = binding.videoPlayer;
        styledPlayerView.setPlayer(player);

        binding.connectDisconnect.setOnClickListener(view -> {
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

        binding.testNoto.setOnClickListener(view -> {
            makeNoto("Test noto!");
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

            if (!SecurityApplication.eventInProgress) {
                SecurityApplication.eventInProgress = true;
                makeNoto("Oh no! Go check your stuff");
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
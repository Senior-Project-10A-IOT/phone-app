package com.example.testapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.testapp.databinding.ActivityMainBinding;
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

public class MainActivity extends AppCompatActivity {
    public static final String CHANNEL = "Hello?";
    private static Listener listener;
    public NotificationManagerCompat man;
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private boolean useLocalWsServer = false;

    private void setConnectedState() {
        SecurityApplication.logDebug("connected to ws");
        binding.innerlayout.connectDisconnect.setText("disconnect");
    }

    private void setDisconnectedState() {
        SecurityApplication.logDebug("disconnected from ws");
        binding.innerlayout.connectDisconnect.setText("connect");
    }

    private void setArmedState() {
        SecurityApplication.logDebug("armed");
        binding.innerlayout.armDisarm.setText("disarm");
    }

    private void setDisarmedState() {
        SecurityApplication.logDebug("disarmed");
        binding.innerlayout.armDisarm.setText("arm");
    }

    private void makeNoto(String contentText) {
        Intent dialIntent = new Intent(Intent.ACTION_DIAL);
        dialIntent.setData(Uri.parse("tel:" + PhoneNumbers.ZACK));
        PendingIntent pendingDialIntent = PendingIntent.getActivity(getBaseContext(), 0, dialIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent navIntent = new Intent(getBaseContext(), MainActivity.class);
        navIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingNavIntent = PendingIntent.getActivity(getBaseContext(), 0, navIntent, 0);

        NotificationCompat.Builder b = new NotificationCompat.Builder(this, MainActivity.CHANNEL)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Security alert")
                .setContentText(contentText)
                .setContentIntent(pendingNavIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setDefaults(NotificationCompat.DEFAULT_LIGHTS | NotificationCompat.DEFAULT_SOUND)
                .addAction(R.drawable.ic_launcher_foreground, "Dial authorities", pendingDialIntent);
        Notification noto = b.build();
        NotificationManager man = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        man.notify(1, noto);
    }


    private void createNotificationChannel() {
        CharSequence name = "Security Application";
        String desc = "Security alerts from your device";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel chan = new NotificationChannel(CHANNEL, name, importance);
        chan.setDescription(desc);
        chan.enableVibration(true);
        chan.enableLights(true);
        chan.setShowBadge(true);
        chan.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(chan);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createNotificationChannel();
        man = NotificationManagerCompat.from(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        listener = new Listener();
        WebsocketWrapper.connect(listener, true);
        setDisarmedState();
        WebsocketWrapper.sendText(Messages.ARM);

        ExoPlayer player = new ExoPlayer.Builder(getBaseContext())
                .setMediaSourceFactory(
                        new DefaultMediaSourceFactory(getBaseContext())
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

        StyledPlayerView styledPlayerView = binding.innerlayout.videoPlayer;
        styledPlayerView.setPlayer(player);

        binding.innerlayout.connectDisconnect.setOnClickListener(view -> {
            SecurityApplication.logErr("cdc " + WebsocketWrapper.isConnected());
            if (WebsocketWrapper.isConnected()) {
                WebsocketWrapper.disconnect();
            } else {
                WebsocketWrapper.connect(listener, this.isRemote());
            }
        });

        binding.innerlayout.showDB.setOnClickListener(view -> {
            Intent intent = new Intent(getBaseContext(), DbActivity.class);
            startActivity(intent);
        });

        binding.innerlayout.armDisarm.setOnClickListener(view -> {
            if (SecurityApplication.armed) {
                WebsocketWrapper.sendText(Messages.DISARM);
            } else {
                WebsocketWrapper.sendText(Messages.ARM);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean isLocal() {
        return useLocalWsServer;
    }

    public boolean isRemote() {
        return !useLocalWsServer;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem checkable = menu.findItem(R.id.app_bar_switch);
        checkable.setChecked(useLocalWsServer);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.app_bar_switch:
                useLocalWsServer = !item.isChecked();
                item.setChecked(useLocalWsServer);
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private class Listener extends WebSocketAdapter {
        @Override
        public void onTextMessage(WebSocket ws, String message) {
            SecurityApplication.logErr("message from pi '" + message + "'");

            //makeNoto("Oh no! Go check your stuff");
            if (message.equals(Messages.ARMED)) {
                SecurityApplication.armed = true;
                setArmedState();
                Toast.makeText(getBaseContext(), "Armed!", Toast.LENGTH_SHORT).show();
            } else if (message.equals(Messages.DISARMED)) {
                SecurityApplication.armed = false;
                setDisarmedState();
                Toast.makeText(getBaseContext(), "Disarmed", Toast.LENGTH_SHORT).show();
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
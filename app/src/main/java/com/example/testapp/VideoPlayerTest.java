package com.example.testapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.ui.StyledPlayerView;

public class VideoPlayerTest extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player_test);

        ExoPlayer player = new ExoPlayer.Builder(getApplicationContext())
                .setMediaSourceFactory(
                        new DefaultMediaSourceFactory(getApplicationContext())
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

        StyledPlayerView styledPlayerView = findViewById(R.id.videoPlayer);
        styledPlayerView.setPlayer(player);

        //String uri = "tel:" + PhoneNumbers.ZACK;

        //Intent intent = new Intent(Intent.ACTION_DIAL);
        //intent.setData(Uri.parse(uri));
        //startActivity(intent);
    }
}
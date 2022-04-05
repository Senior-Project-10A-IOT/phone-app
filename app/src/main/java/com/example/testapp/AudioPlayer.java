package com.example.testapp;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.util.Log;

public class AudioPlayer {
    private static MediaPlayer mediaPlayer = new MediaPlayer();
    private static Thread thread;

    public static void start() {
        if (thread != null)
            thread.interrupt();

        thread = new Thread(() -> {
            try {
                String url = "http://gang-and-friends.com:9922/pisound.mp3";
                mediaPlayer.setAudioAttributes(
                        new AudioAttributes.Builder()
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .build()
                );
                Log.e("a", "a");
                mediaPlayer.setDataSource(url);
                Log.e("b", "a");
                mediaPlayer.prepare();
                Log.e("c", "a");
                mediaPlayer.start();
                Log.e("d", "a");
            } catch (Exception e) {
                Log.e("couldn't play audio", "" + e);
            }
        });

        thread.start();
    }

    public static void stop() {
        mediaPlayer.stop();
        thread.interrupt();
    }
}

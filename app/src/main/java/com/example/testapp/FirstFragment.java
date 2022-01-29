package com.example.testapp;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDeepLinkBuilder;
import androidx.navigation.fragment.NavHostFragment;

import com.example.testapp.databinding.FragmentFirstBinding;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private String a;


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);

        a = "AAAAA";
        binding.textView.setText(a + "h");
        binding.aaaa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                a = a + "aaaaaaaaaaaaaAAAaa";
                binding.textView.setText(a + "h!");
            }
        });

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PendingIntent pendingIntent = new NavDeepLinkBuilder(getContext())
                        .setComponentName(MainActivity.class)
                        .setGraph(R.navigation.nav_graph)
                        .setDestination(R.id.FirstFragment)
                        .setArguments(getArguments())
                        .createPendingIntent();

                NotificationCompat.Builder b = new NotificationCompat.Builder(getActivity(), MainActivity.CHANNEL);
                b.setSmallIcon(R.drawable.ic_launcher_foreground);
                b.setContentText("jifeowjioefw");
                b.setContentTitle("jie88888");
                b.setContentIntent(pendingIntent);
                b.setAutoCancel(true);
                ((MainActivity)getActivity()).man.notify(new java.util.Random().nextInt(), b.build());
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
        binding = null;
    }

}
package com.example.testapp;

import android.app.PendingIntent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDeepLinkBuilder;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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

        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "https://xn--yh8hfqgj.ws";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                binding.networkText.setText(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                binding.networkText.setText(error.toString());
            }
        });

        queue.add(stringRequest);

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
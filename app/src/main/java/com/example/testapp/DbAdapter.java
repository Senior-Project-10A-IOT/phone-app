package com.example.testapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DbAdapter extends RecyclerView.Adapter<DbAdapter.ViewHolder> {
    private List<DbItem> data;
    private LayoutInflater inflater;
    private AdapterView.OnItemClickListener itemClickListener;
    public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.SSSSSSz";

    DbAdapter(Context context, List<DbItem> data) {
        this.inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.db_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DbItem item = data.get(position);

        String timestamp = item.timestamp.replace('T', ' ');
        DateTimeFormatter timestampFormatter = DateTimeFormatter.ofPattern(TIMESTAMP_FORMAT);
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(timestamp, timestampFormatter);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
        String time = zonedDateTime.toLocalDate() + " " + zonedDateTime.format(timeFormatter);

        PrettyTime prettyTime = new PrettyTime();
        holder.hello.setText(time + "\n" + prettyTime.format(zonedDateTime));

        String url = "http://gang-and-friends.com:8764/" + item.photo;
        SecurityApplication.logDebug("onbindviewhodl " + url);
        Picasso.get().load(url).into(holder.photo);
    }

    @Override
    public int getItemCount() {
        SecurityApplication.logDebug("getIteMCount() = " + data.size());
        return data.size();
    }

    public static class DbItem {
        String timestamp;
        int photo;

        DbItem(String timestamp, int photo) {
            this.timestamp = timestamp;
            this.photo = photo;
        }

        @Override
        public String toString() {
            return "DbItem{" +
                    "timestamp='" + timestamp + '\'' +
                    ", photo='" + photo + '\'' +
                    '}';
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView hello;
        ImageView photo;

        ViewHolder(View itemView) {
            super(itemView);
            hello = itemView.findViewById(R.id.hello);
            photo = itemView.findViewById(R.id.photo);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            SecurityApplication.logDebug("clicky");
            if (itemClickListener != null) {
                // ???
                //itemClickListener.onItemClick(view, getBindingAdapterPosition());
            }
        }
    }
}
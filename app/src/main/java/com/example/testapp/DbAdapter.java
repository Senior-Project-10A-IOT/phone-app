package com.example.testapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DbAdapter extends RecyclerView.Adapter<DbAdapter.ViewHolder> {
    private List<DbItem> data;
    private LayoutInflater inflater;
    private AdapterView.OnItemClickListener itemClickListener;

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
        holder.hello.setText(item.timestamp);
        holder.world.setText("" + position);
    }

    @Override
    public int getItemCount() {
        Log.e("", "" + data.size());
        return data.size();
    }

    public static class DbItem {
        String timestamp;
        String photo;

        DbItem(String timestamp, String photo) {
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
        TextView world;

        ViewHolder(View itemView) {
            super(itemView);
            hello = itemView.findViewById(R.id.hello);
            world = itemView.findViewById(R.id.world);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.e("idk","lolsdfklj;fsalk;");
            if (itemClickListener != null) {
                // ???
                //itemClickListener.onItemClick(view, getBindingAdapterPosition());
            }
        }
    }
}

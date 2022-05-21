package com.beaters.musicbeat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beaters.musicbeat.Models.Playlist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

    private static RecyclerViewOnClickListenner onClickListenner = null;

    ArrayList<Playlist> playlists;
    Context context;
    Bundle argument;
    Activity activity;

    public PlaylistAdapter(Context context, ArrayList<Playlist> playlists,RecyclerViewOnClickListenner onClickListenner,Activity activity, Bundle argument){
        this.context = context;
        this.playlists = playlists;
        this.onClickListenner = onClickListenner;
        this.activity = activity;
        this.argument = argument;
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.playlist;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(viewType,parent,false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Playlist playlist = playlists.get(position);
        holder.name.setText(playlist.getName());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String token = "Bearer " + argument.getString("accessToken");
                removePlaylist(playlist.getId(), token);
                activity.recreate();
            }
        });
    }

    public void removePlaylist(Long id, String token){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://music-beats32.herokuapp.com/playlist/delete?id="+id)
                .delete()
                .addHeader("Authorization", token)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast toast = Toast.makeText(activity,"Error!",Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    final String myResponse = Objects.requireNonNull(response.body()).string();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(myResponse.equals("Ваш плейлист был успешно удален!")){
                                Toast.makeText(activity,"Successfully deleted!",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(activity,"Mistake with delete playlist!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public static class PlaylistViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        Button delete;

        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name_playlist);
            delete = itemView.findViewById(R.id.playlist_delete);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onClickListenner != null){
                        int pos = getAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION){
                            onClickListenner.onClick(pos);
                        }
                    }
                }
            });
        }
    }
}

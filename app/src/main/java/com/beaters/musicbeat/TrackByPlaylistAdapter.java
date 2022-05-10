package com.beaters.musicbeat;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.beaters.musicbeat.Models.Track;
import com.bumptech.glide.Glide;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TrackByPlaylistAdapter extends RecyclerView.Adapter<TrackByPlaylistAdapter.PlaylistViewHolder> {

    private static RecyclerViewOnClickListenner onClickListenner = null;

    ArrayList<Track> tracks;
    Context context;
    Activity activity;
    Bundle arguments;
    public TrackByPlaylistAdapter(Context context, ArrayList<Track> tracks, RecyclerViewOnClickListenner onClickListenner, Activity activity, Bundle arguments){
        this.context = context;
        this.tracks = tracks;
        TrackByPlaylistAdapter.onClickListenner = onClickListenner;
        this.activity = activity;
        this.arguments = arguments;
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.playlist_track;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.playlist_track,parent,false);
        return new PlaylistViewHolder(view,onClickListenner);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        Track track = tracks.get(position);
        Glide.with(context).load(track.getImgUrl()).into(holder.imgTrack);
        holder.trackName.setText(track.getName());
        holder.trackAuthor.setText(track.getAuthor());
        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DownloadManager manager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri = Uri.parse(track.getUrl());
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,track.getName() + ".mp3");
                manager.enqueue(request);
            }
        });
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Long playlistId = arguments.getLong("playlistId");
                String token = arguments.getString("accessToken");
                removeTrackFromPlaylist(track.getId(), playlistId, token);
                activity.recreate();
            }
        });
    }

    public void removeTrackFromPlaylist(Long musicId, Long playlistId, String token){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://music-beats32.herokuapp.com/playlist/delete/music?musicId="+musicId+"&playlistId="+playlistId)
                .delete()
                .addHeader("Authorization", "Bearer " + token)
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
                            if(myResponse.equals("Ваша музыка  была успешно удалена!")){
                                Toast.makeText(activity,"Successfully deleted!",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(activity,"Mistake with delete music from playlist!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    public static class PlaylistViewHolder extends RecyclerView.ViewHolder {

        TextView trackName, trackAuthor;
        ImageView imgTrack;
        ImageButton download, remove;
        public PlaylistViewHolder(@NonNull View itemView, RecyclerViewOnClickListenner onClickListenner) {
            super(itemView);
            trackName = itemView.findViewById(R.id.name_track_by_playlist);
            trackAuthor = itemView.findViewById(R.id.author_track_by_playlist);
            imgTrack = itemView.findViewById(R.id.img_track_by_playlist);
            download = itemView.findViewById(R.id.track_down_by_playlist);
            remove = itemView.findViewById(R.id.del_track_by_playlist);
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
package com.beaters.musicbeat;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.beaters.musicbeat.Models.Track;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private static RecyclerViewOnClickListenner onClickListenner = null;

    ArrayList<Track> tracks;
    Context context;
    Activity activity;
    public SearchAdapter(Context context, ArrayList<Track> tracks, RecyclerViewOnClickListenner onClickListenner, Activity activity){
        this.context = context;
        this.tracks = tracks;
        this.onClickListenner = onClickListenner;
        this.activity = activity;
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.track_not_play;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(viewType,parent,false);
        return new ViewHolder(view,onClickListenner);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,track.getName() + ".mp3");
                manager.enqueue(request);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView trackName, trackAuthor;
        ImageView imgTrack;
        ImageButton download;

        public ViewHolder(@NonNull View itemView, RecyclerViewOnClickListenner onClickListenner) {
            super(itemView);
            trackName = itemView.findViewById(R.id.name_track_by_playlist);
            trackAuthor = itemView.findViewById(R.id.author_track_by_playlist);
            imgTrack = itemView.findViewById(R.id.img_track_by_playlist);
            download = itemView.findViewById(R.id.track_down_by_playlist);

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

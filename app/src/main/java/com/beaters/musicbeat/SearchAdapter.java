package com.beaters.musicbeat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
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

    public SearchAdapter(Context context, ArrayList<Track> tracks, RecyclerViewOnClickListenner onClickListenner){
        this.context = context;
        this.tracks = tracks;
        this.onClickListenner = onClickListenner;
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
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView trackName, trackAuthor;
        ImageView imgTrack;

        public ViewHolder(@NonNull View itemView, RecyclerViewOnClickListenner onClickListenner) {
            super(itemView);
            trackName = itemView.findViewById(R.id.name_track);
            trackAuthor = itemView.findViewById(R.id.author_track);
            imgTrack = itemView.findViewById(R.id.imgtrack);

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

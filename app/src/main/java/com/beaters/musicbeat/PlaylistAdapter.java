package com.beaters.musicbeat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beaters.musicbeat.Models.Category;
import com.bumptech.glide.Glide;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

    private static RecyclerViewOnClickListenner onClickListenner = null;

    ArrayList<Category> categories;
    Context context;

    public PlaylistAdapter(Context context, ArrayList<Category> categories,RecyclerViewOnClickListenner onClickListenner){
        this.context = context;
        this.categories = categories;
        this.onClickListenner = onClickListenner;
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.categories;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(viewType,parent,false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        Category category = categories.get(position);
        Glide.with(context).load(category.getImg_url()).into(holder.img);
        holder.cat_name.setText(category.getTitle());
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class PlaylistViewHolder extends RecyclerView.ViewHolder{

        TextView cat_name;
        ImageView img;

        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            cat_name = itemView.findViewById(R.id.cat_name);
            img = itemView.findViewById(R.id.cat_img);

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

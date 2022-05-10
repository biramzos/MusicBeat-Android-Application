package com.beaters.musicbeat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.beaters.musicbeat.Models.Category;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder> {

    private static RecyclerViewOnClickListenner onClickListenner = null;

    ArrayList<Category> categories;
    Context context;

    public HomeAdapter(Context context, ArrayList<Category> categories,RecyclerViewOnClickListenner onClickListenner){
        this.context = context;
        this.categories = categories;
        this.onClickListenner = onClickListenner;
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.home_category;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(viewType,parent,false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        Category category = categories.get(position);
        Glide.with(context).load(category.getImg_url()).into(holder.img);
        holder.cat_name.setText(category.getTitle());
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class HomeViewHolder extends RecyclerView.ViewHolder{

        TextView cat_name;
        ImageView img;

        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            cat_name = itemView.findViewById(R.id.category_name_home);
            img = itemView.findViewById(R.id.category_img_home);

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

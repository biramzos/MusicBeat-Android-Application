package com.beaters.musicbeat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beaters.musicbeat.Models.Category;
import com.beaters.musicbeat.Models.Track;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PlaylistFragment extends Fragment implements RecyclerViewOnClickListenner{

    RecyclerView recyclerView;
    ArrayList<Category> categories = new ArrayList<>();
    RecyclerViewOnClickListenner onClickListenner = this;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        getCategories();
        return view;
    }

    public void getCategories(){
        categories.clear();
        OkHttpClient client = new OkHttpClient();
        String url = "https://music-beats32.herokuapp.com/music/get/categories";
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast toast = Toast.makeText(requireContext(), "Something wrong!", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = Objects.requireNonNull(response.body()).string();
                    try {
                        JSONArray JsonData = new JSONArray(myResponse);
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for(int i = 0; i < JsonData.length();i++){
                                    try {
                                        JSONObject obj = JsonData.getJSONObject(i);
                                        Category category = new Category(obj.getLong("categoryId"),obj.getString("categoryImg"),obj.getString("categoryName"));
                                        categories.add(category);
                                        PlaylistAdapter adapter = new PlaylistAdapter(getContext(),categories,onClickListenner);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                        recyclerView.setAdapter(adapter);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(requireContext(),"Something wrong!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    public void onClick(int position) {
        Category category = categories.get(position);
        System.out.println(category);
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        Bundle data = new Bundle();
        Fragment fragment = new TrackByCategoryFragment();
        data.putString("name",category.getTitle());
        data.putString("id", String.valueOf(category.getId()));
        fragment.setArguments(data);
        transaction.replace(R.id.fragment,fragment).addToBackStack(null).commit();
    }
}

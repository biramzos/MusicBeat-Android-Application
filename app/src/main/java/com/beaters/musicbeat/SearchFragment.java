package com.beaters.musicbeat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.beaters.musicbeat.Models.Track;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchFragment extends Fragment implements RecyclerViewOnClickListenner{

    EditText text;
    ImageButton btn;
    RecyclerView recycler;
    ArrayList<Track> tracks;
    RecyclerViewOnClickListenner onClickListenner = this;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        requireActivity().getOnBackPressedDispatcher();
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        text = (EditText) view.findViewById(R.id.search);
        btn = (ImageButton) view.findViewById(R.id.sBtn);
        recycler = (RecyclerView) view.findViewById(R.id.recycler);
        tracks = new ArrayList<>();
        getAllTracks();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String search = text.getText().toString();
                System.out.println(search);
                if(!search.isEmpty()){
                    searchTracks(search);
                }
                else{
                    Toast.makeText(requireContext(),"You do not find anything!",Toast.LENGTH_SHORT).show();
                    getAllTracks();
                }
            }
        });
        return view;
    }

    public void getAllTracks(){
        tracks.clear();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://music-beats32.herokuapp.com/music/get/all")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                System.out.println(e);
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast toast = Toast.makeText(requireActivity(),"There is no track with  name!",Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String res = Objects.requireNonNull(response.body()).string();
                    try {
                        JSONArray datas = new JSONArray(res);
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for(int i = 0; i < datas.length();i++){
                                    try {
                                        JSONObject data = datas.getJSONObject(i);
                                        Track track = new Track(
                                                data.getLong("musicId"),
                                                data.getString("name"),
                                                data.getString("author"),
                                                data.getString("duration"),
                                                data.getString("imageUrl"),
                                                data.getString("url"));
                                        tracks.add(track);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                SearchAdapter adapter = new SearchAdapter(getContext(),tracks, onClickListenner);
                                recycler.setLayoutManager(new LinearLayoutManager(getContext()));
                                recycler.setAdapter(adapter);
                            }
                        });
                    } catch (JSONException e) {
                        System.out.println(e);
                        Toast toast = Toast.makeText(requireActivity(),"There is no track with  name!",Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            }
        });
    }

    public void searchTracks(String search){
        ArrayList<Track> finded = new ArrayList<>();
        for(int i = 0; i < tracks.size();i++){
            if(tracks.get(i).getName().toLowerCase().contains(search.toLowerCase()) || tracks.get(i).getAuthor().toLowerCase().contains(search.toLowerCase())){
                finded.add(tracks.get(i));
            }
        }
        if(finded.size() == 0){
            Toast.makeText(requireContext(),"Do not find " + search + " artist or track!",Toast.LENGTH_LONG).show();
        }
        SearchAdapter adapter = new SearchAdapter(getContext(),finded, onClickListenner);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(adapter);
    }

    @Override
    public void onClick(int position) {
        Track track = tracks.get(position);
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        Bundle data = new Bundle();
        Fragment fragment = new PlayFragment();
        data.putString("name",track.getName());
        data.putString("author",track.getAuthor());
        data.putString("duration",track.getDuration());
        data.putString("image",track.getImgUrl());
        data.putString("url",track.getUrl());
        data.putString("fragment",getClass().getSimpleName());
        fragment.setArguments(data);
        transaction.replace(R.id.fragment,fragment).addToBackStack(null).commit();
    }
}
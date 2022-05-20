package com.beaters.musicbeat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beaters.musicbeat.Models.Track;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TrackByCategoryFragment extends Fragment implements RecyclerViewOnClickListenner{

    TextView title;
    ArrayList<Track> tracks = new ArrayList<>();
    RecyclerView recycler;
    RecyclerViewOnClickListenner onClickListenner = this;
    Button back;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_track_by_category, container, false);
        title = (TextView) view.findViewById(R.id.category_name);
        recycler = (RecyclerView) view.findViewById(R.id.recycler_view);
        back = (Button) view.findViewById(R.id.back_btn);
        assert getArguments() != null;
        String name = getArguments().getString("name");
        title.setText(name);
        getTracksByCategory(getArguments().getString("id"));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = requireActivity().getSupportFragmentManager();
                fm.popBackStack();
            }
        });
        return view;
    }

    public void getTracksByCategory(String id){
        tracks.clear();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://music-beats32.herokuapp.com/music/get/musicByCategories?categoryId="+id)
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
                                        JSONObject music = datas.getJSONObject(i);
                                        JSONObject data = music.getJSONObject("music");
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
                                TrackByCategoryAdapter adapter = new TrackByCategoryAdapter(getContext(),tracks, onClickListenner);
                                recycler.setLayoutManager(new LinearLayoutManager(getContext()));
                                recycler.setAdapter(adapter);
                            }
                        });
                    } catch (JSONException e) {
                        System.out.println(e);
                        Toast toast = Toast.makeText(requireActivity(),"Error!",Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            }
        });
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

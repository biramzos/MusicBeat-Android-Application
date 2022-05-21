package com.beaters.musicbeat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

public class TrackByPlaylistFragment extends Fragment implements RecyclerViewOnClickListenner {
    TextView title;
    ArrayList<Track> tracks;
    RecyclerView recycler;
    RecyclerViewOnClickListenner onClickListenner = this;
    Button back, add, refresh;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_track_by_playlist, container, false);
        title = (TextView) view.findViewById(R.id.playlist_name);
        recycler = (RecyclerView) view.findViewById(R.id.playlist_view);
        back = (Button) view.findViewById(R.id.back_btn_playlist);
        add = (Button) view.findViewById(R.id.add_track_btn);
        refresh = (Button) view.findViewById(R.id.refresh_btn);
        assert getArguments() != null;
        String name = getArguments().getString("name_playlist");
        String uniqueAddress = getArguments().getString("UniqueAddress");
        String token = getArguments().getString("accessToken");
        String email = getArguments().getString("email");
        title.setText(name);
        tracks = new ArrayList<>();

        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getTracksByPlaylist(uniqueAddress);
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getTracksByPlaylist(uniqueAddress);
                    }
                });
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                Bundle data = new Bundle();
                Fragment fragment = new PlaylistFragment();
                assert getArguments() != null;
                data.putString("email",email);
                fragment.setArguments(data);
                transaction.replace(R.id.fragment,fragment).commit();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                Bundle data = new Bundle();
                Fragment fragment = new AddMusicToPlaylistFragment();
                data.putString("accessToken",token);
                System.out.println(token);
                data.putString("playlistName", name);
                data.putString("playlistUniqueAddress",uniqueAddress);
                data.putString("email",email);
                fragment.setArguments(data);
                transaction.replace(R.id.fragment,fragment).addToBackStack(null).commit();
            }
        });
        return view;
    }

    public void getTracksByPlaylist(String uniqueAddress){
        tracks.clear();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://music-beats32.herokuapp.com/playlist/get/music/all?uniqueAddress="+uniqueAddress)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(requireActivity(), "There is no track with  name!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String res = Objects.requireNonNull(response.body()).string();
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONArray datas = new JSONArray(res);
                                for(int i = 0; i < datas.length(); i++){
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
                                    TrackByPlaylistAdapter adapter = new TrackByPlaylistAdapter(requireContext(), tracks, onClickListenner, getActivity(), getArguments());
                                    recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
                                    recycler.setAdapter(adapter);
                                }
                            } catch (JSONException e) {
                                Toast.makeText(requireContext(),"Error!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
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
        assert getArguments() != null;
        data.putString("email", getArguments().getString("email"));
        fragment.setArguments(data);
        transaction.replace(R.id.fragment,fragment).addToBackStack(null).commit();
    }
}
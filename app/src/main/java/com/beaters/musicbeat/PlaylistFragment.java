package com.beaters.musicbeat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beaters.musicbeat.Models.Category;
import com.beaters.musicbeat.Models.Playlist;
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
    ArrayList<Playlist> playlists = new ArrayList<>();
    RecyclerViewOnClickListenner onClickListenner = this;
    Button add;
    EditText name;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.playlists);
        assert getArguments() != null;
        String email = getArguments().getString("email");
        add = (Button) view.findViewById(R.id.playlist_add);
        name = (EditText) view.findViewById(R.id.playlistname);
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getPlaylists(email);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String playlist = name.getText().toString();
                assert getArguments() != null;
                String token = getArguments().getString("accessToken");
                if(!playlist.isEmpty()){
                    addPlaylist(playlist, token);
                    name.setText("");
                }
            }
        });


        return view;
    }

    public void getPlaylists(String email){
        playlists.clear();
        OkHttpClient client = new OkHttpClient();
        String url = "https://music-beats32.herokuapp.com/playlist/get?email=" + email;
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
                                        Playlist playlist = new Playlist(obj.getLong("playListId"),obj.getString("playListName"),obj.getString("uniqueAddress"));
                                        playlists.add(playlist);
                                        PlaylistAdapter adapter = new PlaylistAdapter(getContext(),playlists,onClickListenner,requireActivity(),getArguments());
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

    public void addPlaylist(String name, String token){
        assert getArguments() != null;
        OkHttpClient client = new OkHttpClient();
        MediaType type = MediaType.parse("application/json");
        JSONObject send = new JSONObject();
        try {
            send.put("name",name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(type,send.toString());
        String url = "https://music-beats32.herokuapp.com/playlist/create";
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization","Bearer " + token)
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
                    assert getArguments() != null;
                    String email = getArguments().getString("email");
                    if(myResponse.equals("Ваш плейлист был успешно создан!")){
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(requireContext(),"Successfully added!",Toast.LENGTH_SHORT).show();
                                getPlaylists(email);
                            }
                        });
                    }else {
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(requireContext(), "Something is mistake!", Toast.LENGTH_SHORT).show();
                                getPlaylists(email);
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    public void onClick(int position) {
        Playlist playlist = playlists.get(position);
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        Bundle data = new Bundle();
        Fragment fragment = new TrackByPlaylistFragment();
        data.putString("name_playlist",playlist.getName());
        data.putString("UniqueAddress",playlist.getUniqueAddress());
        data.putLong("playlistId",playlist.getId());
        assert getArguments() != null;
        String token = getArguments().getString("accessToken");
        data.putString("accessToken", token);
        assert getArguments() != null;
        data.putString("email",getArguments().getString("email"));
        fragment.setArguments(data);
        transaction.replace(R.id.fragment,fragment).addToBackStack(null).commit();
    }
}

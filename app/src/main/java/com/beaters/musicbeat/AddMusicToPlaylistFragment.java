package com.beaters.musicbeat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.util.Objects;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddMusicToPlaylistFragment extends Fragment implements RecyclerViewOnClickListenner{

    Button back;
    RecyclerView recycler;
    ArrayList<Track> tracks = new ArrayList<>();
    RecyclerViewOnClickListenner onClickListenner = this;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_music_to_playlist, container, false);
        back = (Button) view.findViewById(R.id.add_music_back_btn);
        recycler = (RecyclerView) view.findViewById(R.id.add_music_view);
        getAllTracks();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                Bundle data = new Bundle();
                assert getArguments() != null;
                String uniqueAddress = getArguments().getString("playlistUniqueAddress");
                String token = getArguments().getString("accessToken");
                String name = getArguments().getString("playlistName");
                Fragment fragment = new TrackByPlaylistFragment();
                data.putString("name_playlist", name);
                data.putString("UniqueAddress", uniqueAddress);
                data.putString("accessToken",token);
                data.putString("email",getArguments().getString("email"));
                fragment.setArguments(data);
                transaction.replace(R.id.fragment,fragment).commit();
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
                        Toast.makeText(requireActivity(),"There is no track with  name!",Toast.LENGTH_SHORT).show();
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
                                AddMusicToPlaylistAdapter adapter = new AddMusicToPlaylistAdapter(getContext(),tracks, onClickListenner);
                                recycler.setLayoutManager(new LinearLayoutManager(getContext()));
                                recycler.setAdapter(adapter);
                            }
                        });
                    } catch (JSONException e) {
                        Toast.makeText(requireActivity(),"There is no track!",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void addMusicToPlaylist(String uniqueAddress, Long music_id, String token){
        OkHttpClient client = new OkHttpClient();
        MediaType type = MediaType.parse("application/json");
        JSONObject send = new JSONObject();
        try {
            send.put("musicId", music_id.longValue());
            send.put("uniqueAddress", uniqueAddress);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(type,send.toString());
        Request request = new Request.Builder()
                .url("https://music-beats32.herokuapp.com/playlist/add/music")
                .post(body)
                .addHeader("Authorization","Bearer " + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(requireActivity(),"There is some error!",Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    final String res = Objects.requireNonNull(response.body()).string();
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(res.equals("Музыка была успешна добавлена в плейлист")){
                                Toast.makeText(requireContext(),"Successfully added!",Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(requireContext(),"Something error!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onClick(int position) {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        Bundle data = new Bundle();
        assert getArguments() != null;
        String uniqueAddress = getArguments().getString("playlistUniqueAddress");
        String token = getArguments().getString("accessToken");
        String name = getArguments().getString("playlistName");
        Track track = tracks.get(position);
        addMusicToPlaylist(uniqueAddress,track.getId(),token);
        Fragment fragment = new TrackByPlaylistFragment();
        data.putString("name_playlist", name);
        data.putString("UniqueAddress", uniqueAddress);
        data.putString("email",getArguments().getString("email"));
        fragment.setArguments(data);
        transaction.replace(R.id.fragment,fragment).commit();
    }
}

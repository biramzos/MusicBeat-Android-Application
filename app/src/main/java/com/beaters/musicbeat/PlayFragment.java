package com.beaters.musicbeat;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.beaters.musicbeat.Authentication.Database;
import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PlayFragment extends Fragment {

    ImageView img;
    TextView name, author, total, time;
    Button back;
    ImageButton play;
    SeekBar bar;
    MediaPlayer player;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play, container, false);
        img = (ImageView) view.findViewById(R.id.track_img_play);
        name = (TextView) view.findViewById(R.id.track_title_play);
        author = (TextView) view.findViewById(R.id.track_author_play);
        total = (TextView) view.findViewById(R.id.total);
        time = (TextView) view.findViewById(R.id.time);
        play = (ImageButton) view.findViewById(R.id.playbtn);
        back = (Button) view.findViewById(R.id.back);
        bar = (SeekBar) view.findViewById(R.id.seekBar);

        Intent data = requireActivity().getIntent();
        assert getArguments() != null;
        name.setText(getArguments().getString("name"));
        author.setText(getArguments().getString("author"));
        total.setText(getArguments().getString("duration"));
        Glide.with(requireContext()).load(getArguments().getString("image")).into(img);
        Resources res = getResources();
        Drawable drawable = ResourcesCompat.getDrawable(res, R.drawable.ic_baseline_pause_24, null);
        play.setBackground(drawable);
        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            player.setDataSource(getArguments().getString("url"));
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(player.isPlaying()){
                    Resources res = getResources();
                    Drawable drawable = ResourcesCompat.getDrawable(res, R.drawable.ic_baseline_play_arrow_24, null);
                    play.setBackground(drawable);
                    player.pause();
                }else{
                    Resources res = getResources();
                    Drawable drawable = ResourcesCompat.getDrawable(res, R.drawable.ic_baseline_pause_24, null);
                    play.setBackground(drawable);
                    player.start();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = requireActivity().getSupportFragmentManager();
                fm.popBackStack();
            }
        });

        bar.setMax(player.getDuration());
        bar.setProgress(0);

        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(player != null){
                    bar.setProgress(player.getCurrentPosition());
                    time.setText(convertToMMSS(player.getCurrentPosition() + ""));
                }
                new Handler().postDelayed(this,100);
            }
        });

        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(player != null && b){
                    player.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        return view;
    }

    public static String convertToMMSS(String duration){
        Long millis = Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }

    @Override
    public void onDestroyView() {
        if(player.isPlaying()){
            player.pause();
            player = null;
        }
        super.onDestroyView();
    }
}

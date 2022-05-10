package com.beaters.musicbeat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MicFragment extends Fragment {

    ImageButton image;
    ImageView art;
    TextView trackname, trackauthor;
    boolean isRecording = false;
    MediaRecorder mediaRecorder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mic, container, false);
        image = (ImageButton) view.findViewById(R.id.view);
        trackname = (TextView) view.findViewById(R.id.trackname);
        trackauthor = (TextView) view.findViewById(R.id.trackauthor);
        Resources res = getResources();
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkPermissions()) {
                    if (!isRecording) {
                        Drawable drawable = ResourcesCompat.getDrawable(res, R.drawable.mic, null);
                        image.setBackground(drawable);
                        startRecord();
                    } else {
                        Drawable drawable = ResourcesCompat.getDrawable(res, R.drawable.logo, null);
                        image.setBackground(drawable);
                        stopRecording();
                    }
                }else{
                    ActivityCompat.requestPermissions(getActivity(),new String[]{
                            Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },1);
                }
            }
        });
        return view;
    }

    public void ShazamRecognizeAPI(){
        final MediaType MEDIA_TYPE_MP3 = MediaType.get("audio/mpeg; charset=utf-8");
        File file = new File(getFilePath());
        OkHttpClient client = new OkHttpClient();
        RequestBody data = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("api_token", "c2b6fbf39a6c7dd82ea6142812d97913")
                .addFormDataPart("file", file.getName(),
                        RequestBody.Companion.create(file, MEDIA_TYPE_MP3))
                .addFormDataPart("return", "apple_music,spotify").build();
        Request request = new Request.Builder()
                .url("https://api.audd.io/")
                .post(data).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast toast = Toast.makeText(requireActivity().getApplicationContext(),"Can't detect the track",Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    final String myResponse = Objects.requireNonNull(response.body()).string();
                    try {
                        JSONObject JsonData = new JSONObject(myResponse);
                        if(!JsonData.isNull("result")){
                            JSONObject res = (JSONObject) JsonData.get("result");
                            String title = (String) res.get("title");
                            String artist = (String) res.get("artist");
                            requireActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    trackname.setText("Name: " + title);
                                    trackauthor.setText("Author: " + artist);
                                }
                            });
                        }
                        else{
                            requireActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast toast = Toast.makeText(requireActivity().getApplicationContext(),"Could not recognize!", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast toast = Toast.makeText(requireActivity().getApplicationContext(),"Could not recognize!", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        });
                    }
                }
            }
        });
    }

    @SuppressLint("WrongConstant")
    public void startRecord(){
        isRecording = true;
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setOutputFile(getFilePath());
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(getActivity(), "Mic start...", Toast.LENGTH_SHORT).show();
    }

    public void stopRecording(){
        isRecording = false;
        mediaRecorder.reset();
        mediaRecorder.release();
        mediaRecorder = null;
        ShazamRecognizeAPI();
        Toast.makeText(getActivity(), "Detecting...", Toast.LENGTH_SHORT).show();
    }

    private boolean checkPermissions() {
        int first = ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(),
                Manifest.permission.RECORD_AUDIO);
        int second = ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        return first == PackageManager.PERMISSION_GRANTED &&
                second == PackageManager.PERMISSION_GRANTED;
    }

    public String getFilePath(){
        ContextWrapper wrapper = new ContextWrapper(getActivity().getApplicationContext());
        File musicDirectory = wrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(musicDirectory,"mic.raw");
        return file.getPath();
    }

}
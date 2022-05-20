package com.beaters.musicbeat;

import static android.content.Intent.getIntent;

import android.content.Intent;
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

import com.beaters.musicbeat.Authentication.Database;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

public class ProfileFragment extends Fragment {

    TextView username, email;
    Button btn_exit;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View returnView = inflater.inflate(R.layout.fragment_profile, container, false);
        username = (TextView) returnView.findViewById(R.id.user_full_name);
        email = (TextView) returnView.findViewById(R.id.user_email);
        btn_exit = (Button) returnView.findViewById(R.id.exit);
        assert getArguments() != null;
        username.setText(getArguments().getString("username"));
        email.setText(getArguments().getString("email"));
        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Database myDB = new Database(requireContext());
                myDB.delete(requireArguments().getString("username"));
                Intent userData = requireActivity().getIntent();
                Long id = userData.getLongExtra("id",0);
                logout(id);
            }
        });
        return returnView;
    }
    public void logout(Long id){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://music-beats32.herokuapp.com/auth/logout?userId="+id)
                .delete(new RequestBody() {
                    @Nullable
                    @Override
                    public MediaType contentType() {
                        return null;
                    }

                    @Override
                    public void writeTo(@NonNull BufferedSink bufferedSink) throws IOException {

                    }
                })
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast toast = Toast.makeText(requireActivity(),"Error!",Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    final String myResponse = Objects.requireNonNull(response.body()).string();
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(requireContext(),RegisterActivity.class);
                            startActivity(intent);
                        }
                    });
                }
            }
        });
    }
}
package com.beaters.musicbeat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    TextView fullName, email;
    Button btn_exit;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View returnView = inflater.inflate(R.layout.fragment_profile, container, false);
        fullName = (TextView) returnView.findViewById(R.id.user_full_name);
        email = (TextView) returnView.findViewById(R.id.user_email);
        btn_exit = (Button) returnView.findViewById(R.id.exit);
        fullName.setText(getArguments().getString("fullName"));
        email.setText(getArguments().getString("email"));
        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),LoginActivity.class);
                fullName = null;
                email = null;
                startActivity(intent);
            }
        });
        return returnView;
    }
}
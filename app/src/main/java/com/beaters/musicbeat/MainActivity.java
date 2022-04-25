package com.beaters.musicbeat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.beaters.musicbeat.Models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView nav;
    User user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent userData = getIntent();
        user = new User(userData.getStringExtra("fullName"),userData.getStringExtra("email"),userData.getStringExtra("password"));
        if(userData.getStringExtra("email") == null){
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        }
        nav = findViewById(R.id.bottom_navigation);
        nav.setOnNavigationItemSelectedListener(navListener);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment,
                    new HomeFragment()).commit();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    Intent userData = getIntent();
                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.nav_search:
                            selectedFragment = new SearchFragment();
                            break;
                        case R.id.nav_mic:
                            selectedFragment = new MicFragment();
                            break;
                        case R.id.nav_favorite:
                            selectedFragment = new FavoriteFragment();
                            break;
                        case R.id.nav_profile:
                            selectedFragment = new ProfileFragment();
                    }

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    Bundle data = new Bundle();
                    data.putString("fullName", userData.getStringExtra("fullName"));
                    data.putString("email", userData.getStringExtra("email"));
                    selectedFragment.setArguments(data);
                    transaction.replace(R.id.fragment,selectedFragment).commit();
                    return true;
                }
            };

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        Intent userData = getIntent();
        user = new User(userData.getStringExtra("fullName"),userData.getStringExtra("email"),userData.getStringExtra("password"));
        if(user == null) {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        }
        super.onResume();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }
}
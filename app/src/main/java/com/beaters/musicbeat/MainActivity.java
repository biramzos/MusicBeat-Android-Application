package com.beaters.musicbeat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nav = findViewById(R.id.bottom_navigation);
        nav.setOnNavigationItemSelectedListener(navListener);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment,
                    new HomeFragment()).commit();
        }

        if(this.isFinishing()){
            Intent userData = getIntent();
            Long id = userData.getLongExtra("id",0);
            logout(id);
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
                        case R.id.nav_playlist:
                            selectedFragment = new PlaylistFragment();
                            break;
                        case R.id.nav_profile:
                            selectedFragment = new ProfileFragment();
                    }

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    Bundle data = new Bundle();
                    data.putString("username", userData.getStringExtra("username"));
                    data.putString("email", userData.getStringExtra("email"));
                    data.putLong("id",userData.getLongExtra("id",0));
                    assert selectedFragment != null;
                    selectedFragment.setArguments(data);
                    transaction.replace(R.id.fragment,selectedFragment).addToBackStack(null).commit();
                    return true;
                }
            };

    public void logout(Long id){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://music-beats32.herokuapp.com/auth/logout?userId="+id)
                .delete()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast toast = Toast.makeText(MainActivity.this,"Error!",Toast.LENGTH_SHORT);
                        toast.show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    final String myResponse = Objects.requireNonNull(response.body()).string();
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
                            startActivity(intent);
                            MainActivity.this.finishAndRemoveTask();
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        Intent userData = getIntent();
        Long id = userData.getLongExtra("id",0);
        logout(id);
        System.out.println("OnDestroy");
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        Intent userData = getIntent();
        Long id = userData.getLongExtra("id",0);
        logout(id);
        System.out.println("OnStop");
        super.onStop();
    }
}
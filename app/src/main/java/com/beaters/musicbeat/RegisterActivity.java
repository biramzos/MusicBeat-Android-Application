package com.beaters.musicbeat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.beaters.musicbeat.Authentication.Database;

import org.json.JSONException;
import org.json.JSONObject;
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

public class RegisterActivity extends AppCompatActivity {
    EditText username, email, password;
    Button btn_reg, btn_go_log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Database myDB = new Database(RegisterActivity.this);
        Cursor cursor = myDB.read();
        if(cursor.getCount() == 0){
            ActivityActions();
        }else{
            String email = null;
            String password = null;
            while (cursor.moveToNext()) {
                email = cursor.getString(1);
                password = cursor.getString(2);
            }
            login(email, password);
        }
    }

    public void ActivityActions(){
        username = findViewById(R.id.reg_name);
        email = findViewById(R.id.reg_email);
        password = findViewById(R.id.reg_password);
        btn_reg = findViewById(R.id.btn_reg);
        btn_go_log = findViewById(R.id.btn_go_log);

        btn_reg.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        register(username.getText().toString(),email.getText().toString(),password.getText().toString());
                    }
                }
        );

        btn_go_log.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                        startActivity(intent);
                    }
                }
        );
    }

    public void register(String username, String email, String password) {
        OkHttpClient client = new OkHttpClient();
        MediaType type = MediaType.parse("application/json");
        JSONObject send = new JSONObject();
        try {
            send.put("nickname",username);
            send.put("email",email);
            send.put("password",password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(type,send.toString());
        String url = "https://music-beats32.herokuapp.com/auth/signup";
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                RegisterActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Something wrong!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = Objects.requireNonNull(response.body()).string();
                    try {
                        JSONObject JsonData = new JSONObject(myResponse);
                        String message = JsonData.getString("message");
                        RegisterActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(message.equals("User registered successfully")){
                                    Intent data = new Intent(getApplicationContext(),LoginActivity.class);
                                    startActivity(data);
                                    Toast.makeText(getApplicationContext(),"Successfully!Please, sign in!", Toast.LENGTH_SHORT).show();
                                }else if(message.equals("email and nickname already exist")){
                                    Intent data = new Intent(getApplicationContext(),LoginActivity.class);
                                    startActivity(data);
                                    Toast.makeText(getApplicationContext(),"User already exist!", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(getApplicationContext(),"Something wrong!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                        RegisterActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),"Something wrong!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });
    }

    public void login(String email, String password){
        OkHttpClient client = new OkHttpClient();
        MediaType type = MediaType.parse("application/json");
        JSONObject send = new JSONObject();
        try {
            send.put("email",email);
            send.put("password",password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(type,send.toString());
        String url = "https://music-beats32.herokuapp.com/auth/signin";
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                RegisterActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"Something wrong!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = Objects.requireNonNull(response.body()).string();
                    try {
                        JSONObject JsonData = new JSONObject(myResponse);
                        if(JsonData.getString("message").equals("password or nickname incorrect")){
                            RegisterActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Email or password is incorrect!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else {
                            Long id = JsonData.getLong("id");
                            String emailRes = JsonData.getString("email");
                            String username = JsonData.getString("nickname");
                            String token = String.valueOf(JsonData.get("accessToken"));
                            RegisterActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (emailRes.equals(email)) {
                                        Intent data = new Intent(getApplicationContext(), MainActivity.class);
                                        data.putExtra("id", id);
                                        data.putExtra("accessToken",token);
                                        data.putExtra("username", username);
                                        data.putExtra("email", email);
                                        data.putExtra("password", password);

                                        startActivity(data);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Something wrong!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        RegisterActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),"Something wrong!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });
    }
}
package com.beaters.musicbeat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.beaters.musicbeat.Models.User;

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

public class RegisterActivity extends AppCompatActivity {
    EditText fullName, email, password;
    Button btn_reg, btn_go_log;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ActivityActions();
    }

    public void ActivityActions(){
        fullName = findViewById(R.id.reg_name);
        email = findViewById(R.id.reg_email);
        password = findViewById(R.id.reg_password);
        btn_reg = findViewById(R.id.btn_reg);
        btn_go_log = findViewById(R.id.btn_go_log);

        btn_reg.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        JSONData(fullName.getText().toString(),email.getText().toString(),password.getText().toString());
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

    public void JSONData(String fullName, String email, String password) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://e23d-85-159-27-200.ngrok.io/api/users/add?fullname="+fullName+"&email="+email+"&password="+password;
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                Toast toast = Toast.makeText(getApplicationContext(), "Something wrong!", Toast.LENGTH_SHORT);
                toast.show();
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = Objects.requireNonNull(response.body()).string();
                    try {
                        JSONObject JsonData = new JSONObject(myResponse);
                        String fullName = JsonData.getString("fullName");
                        String email = JsonData.getString("email");
                        String password = JsonData.getString("password");
                        RegisterActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent data = new Intent(getApplicationContext(),MainActivity.class);
                                data.putExtra("fullName",fullName);
                                data.putExtra("email",email);
                                data.putExtra("password",password);
                                System.out.println(fullName + " " + email + " " + password);
                                startActivity(data);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                        RegisterActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast toast = Toast.makeText(getApplicationContext(),"Something wrong!", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        });
                    }
                }
            }
        });
    }
}
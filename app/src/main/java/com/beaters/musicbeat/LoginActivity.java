package com.beaters.musicbeat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    EditText email,password;
    Button btn_log, btn_go_reg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActivityActions();
    }

    public void ActivityActions(){
        email = findViewById(R.id.log_email);
        password = findViewById(R.id.log_password);
        btn_log = findViewById(R.id.btn_log);
        btn_go_reg = findViewById(R.id.btn_go_reg);

        btn_log.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        JSONData(email.getText().toString(),password.getText().toString());
                    }
                }
        );

        btn_go_reg.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    public void JSONData(String email, String password){
        OkHttpClient client = new OkHttpClient();

        String url = "https://e23d-85-159-27-200.ngrok.io/api/users/get?email=" + email + "&password=" + password;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = Objects.requireNonNull(response.body()).string();
                    try {
                        JSONObject JsonData = new JSONObject(myResponse);
                        String fullName = JsonData.getString("fullName");
                        String email = JsonData.getString("email");
                        String password = JsonData.getString("password");
                        LoginActivity.this.runOnUiThread(new Runnable() {
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
                        LoginActivity.this.runOnUiThread(new Runnable() {
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
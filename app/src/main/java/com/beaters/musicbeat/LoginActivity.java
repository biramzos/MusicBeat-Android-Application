package com.beaters.musicbeat;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
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
                        login(email.getText().toString(),password.getText().toString());
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
                System.out.println(e);
                Toast.makeText(getApplicationContext(),"Something wrong!", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = Objects.requireNonNull(response.body()).string();
                    try {
                        JSONObject JsonData = new JSONObject(myResponse);
                        if(JsonData.getString("message").equals("password or nickname incorrect")){
                            LoginActivity.this.runOnUiThread(new Runnable() {
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
                            LoginActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (emailRes.equals(email)) {
                                        Intent data = new Intent(getApplicationContext(), MainActivity.class);
                                        Database myDB = new Database(LoginActivity.this);
                                        myDB.add(username, email, password);
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
                        LoginActivity.this.runOnUiThread(new Runnable() {
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
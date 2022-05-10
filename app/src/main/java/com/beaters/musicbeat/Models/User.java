package com.beaters.musicbeat.Models;

import androidx.annotation.NonNull;

public class User {
    private String username;
    private String email;
    private String password;

    public User(){}

    public User(String username, String email, String password){
        this.username = username;
        this.email = email;
        this.password = password;
    }

    @NonNull
    public String toString(){
        return username + " " + email + " " + password;
    }
}

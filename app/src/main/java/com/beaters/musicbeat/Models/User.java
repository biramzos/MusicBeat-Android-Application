package com.beaters.musicbeat.Models;

import androidx.annotation.NonNull;

public class User {
    private String fullName;
    private String email;
    private String password;

    public User(){}

    public User(String fullName, String email, String password){
        this.fullName = fullName;
        this.email = email;
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @NonNull
    public String toString(){
        return email + "{\nFull name: " + fullName + "\nEmail: " + email + "\nPassword: " + password + "\n}";
    }
}

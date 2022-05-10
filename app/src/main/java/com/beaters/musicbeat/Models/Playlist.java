package com.beaters.musicbeat.Models;

public class Playlist {
    private Long id;
    private String name;
    private String uniqueAddress;

    public Playlist(){}

    public Playlist(Long id, String name, String uniqueAddress){
        this.id = id;
        this.name = name;
        this.uniqueAddress = uniqueAddress;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUniqueAddress() {
        return uniqueAddress;
    }
}

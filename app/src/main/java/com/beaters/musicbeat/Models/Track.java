package com.beaters.musicbeat.Models;


import java.util.ArrayList;


public class Track {
    private Long id;
    private String name;
    private String author;
    private String duration;
    private String imgUrl;
    private String url;

    public Track(){}

    public Track(Long id, String name, String author, String duration, String imgUrl, String url){
        this.id = id;
        this.duration = duration;
        this.name = name;
        this.author = author;
        this.imgUrl = imgUrl;
        this.url = url;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Track{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}

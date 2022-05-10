package com.beaters.musicbeat.Models;

public class Category {
    private Long id;
    private String img_url;
    private String title;

    public Category(Long id,String img_url, String title){
        this.id = id;
        this.img_url = img_url;
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImg_url() {
        return img_url;
    }

    public String getTitle() {
        return title;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Category{" +
                "img_url='" + img_url + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}

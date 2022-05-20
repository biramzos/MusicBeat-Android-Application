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

<<<<<<< HEAD
=======
    public void setId(Long id) {
        this.id = id;
    }

>>>>>>> origin/app
    public String getImg_url() {
        return img_url;
    }

    public String getTitle() {
        return title;
    }

<<<<<<< HEAD
=======
    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

>>>>>>> origin/app
    @Override
    public String toString() {
        return "Category{" +
                "img_url='" + img_url + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}

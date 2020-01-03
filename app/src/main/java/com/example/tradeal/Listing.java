package com.example.tradeal;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class Listing implements Serializable {

    private ArrayList<String> imageUrls;
    private String title, description;
    private User user;

    public Listing(ArrayList<String> images, String title, String description, User user) {
        this.imageUrls = images;
        this.title = title;
        this.description = description;
        this.user = user;
    }

    public Listing() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ArrayList<String> getImages() {
        return imageUrls;
    }

    public void addImage(String... images){
        this.imageUrls.addAll(Arrays.asList(images));
    }
}

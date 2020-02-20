package com.example.tradeal;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class Listing implements Serializable {

    private ArrayList<String> imageUrls;
    private String title, description, userEmail, id;
    private Category category;

    public Listing(ArrayList<String> images, String title, String description, String userEmail, Category category) {
        this.imageUrls = images;
        this.title = title;
        this.description = description;
        this.userEmail = userEmail;
        this.category = category;
    }

    public Listing(Listing listing){
        this.imageUrls = listing.getImageUrls();
        this.title = listing.getTitle();
        this.description = listing.getDescription();
        this.userEmail = listing.getUserEmail();
        this.category = listing.getCategory();
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

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void addImage(String... images){
        this.imageUrls.addAll(Arrays.asList(images));
    }

    public ArrayList<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(ArrayList<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

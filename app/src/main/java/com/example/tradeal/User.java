package com.example.tradeal;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.LinearLayout;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {

    private String username, email;
    private String imageUrl;
    private ArrayList<Listing> listings;

    public User(String username, String email, String imageUrl) {
        this.username = username;
        this.email = email;
        this.imageUrl = imageUrl;
    }

    public User() {
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImage(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public ArrayList<Listing> getListings() {
        return listings;
    }

    public void addListing(Listing listing){
        listings.add(listing);
    }
}

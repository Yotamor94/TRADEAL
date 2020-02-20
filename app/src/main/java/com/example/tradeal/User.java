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
    private ArrayList<String> listingIds, favouriteIds;
    ArrayList<String> usersConversedEmails;
    ArrayList<String> deviceTokens;
    boolean isGroupCreated;

    public User(String username, String email, String imageUrl) {
        this.username = username;
        this.email = email;
        this.imageUrl = imageUrl;
        listingIds = new ArrayList<>();
        favouriteIds = new ArrayList<>();
        deviceTokens = new ArrayList<>();
        isGroupCreated = false;
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

    public void setEmail(String email) {
        this.email = email;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public ArrayList<String> getListingIds() {
        return listingIds;
    }

    public void setListingIds(ArrayList<String> listingIds) {
        this.listingIds = listingIds;
    }

    public void addLisitngId(String id){
        listingIds.add(id);
    }

    public ArrayList<String> getFavouriteIds() {
        return favouriteIds;
    }

    public void addFavouriteId(String id){
        favouriteIds.add(id);
    }

    public void setFavouriteIds(ArrayList<String> favouriteIds) {
        this.favouriteIds = favouriteIds;
    }

    public ArrayList<String> getUsersConversedEmails() {
        return usersConversedEmails;
    }

    public void setUsersConversedEmails(ArrayList<String> usersConversedEmails) {
        this.usersConversedEmails = usersConversedEmails;
    }

    public void addUserConversed(String email){
        usersConversedEmails.add(email);
    }

    public ArrayList<String> getDeviceTokens() {
        return deviceTokens;
    }

    public void setDeviceTokens(ArrayList<String> deviceTokens) {
        this.deviceTokens = deviceTokens;
    }

    public void addDeviceToken(String token){
        deviceTokens.add(token);
    }

    public boolean isGroupCreated() {
        return isGroupCreated;
    }

    public void setGroupCreated(boolean groupCreated) {
        isGroupCreated = groupCreated;
    }
}

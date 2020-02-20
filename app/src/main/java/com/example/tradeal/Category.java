package com.example.tradeal;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Category implements Serializable {

    public static final String SPORTS = "Sports", FASHION = "Fashion", GARDEN = "Garden", CARS = "Cars", ELECTRONICS = "Electronics", FURNITURE = "Furniture";


    private String name;
    private int imageId;

    public Category(String name) {
        this.name = name;
        switch(name){
            case SPORTS:
                imageId = R.drawable.ic_run_black_24dp;
                break;
            case FASHION:
                imageId = R.drawable.hanger;
                break;
            case GARDEN:
                imageId = R.drawable.baseline_local_florist_black_24dp;
                break;
            case CARS:
                imageId = R.drawable.ic_car_black_24dp;
                break;
            case ELECTRONICS:
                imageId = R.drawable.ic_computer_black_24dp;
                break;
            case FURNITURE:
                imageId = R.drawable.ic_sofa_black_24dp;
        }
    }

    public Category(String name, int imageId) {
        this.name = name;
        this.imageId = imageId;
    }

    public Category() {
        this.name = "Not categorized";
        this.imageId = R.drawable.baseline_category_black_24dp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        switch(name){
            case SPORTS:
                imageId = R.drawable.ic_run_black_24dp;
                break;
            case FASHION:
                imageId = R.drawable.hanger;
                break;
            case GARDEN:
                imageId = R.drawable.baseline_local_florist_black_24dp;
                break;
            case CARS:
                imageId = R.drawable.ic_car_black_24dp;
                break;
            case ELECTRONICS:
                imageId = R.drawable.ic_computer_black_24dp;
                break;
            case FURNITURE:
                imageId = R.drawable.ic_sofa_black_24dp;
        }
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
}

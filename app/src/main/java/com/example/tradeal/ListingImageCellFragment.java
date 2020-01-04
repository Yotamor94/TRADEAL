package com.example.tradeal;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ListingImageCellFragment extends Fragment {
    String imageUrl;
    ImageCellListener listener;

    public interface ImageCellListener{
        void deleteListingImage(String ImageUrl);
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    public static ListingImageCellFragment newInstance(String imageUrl){
        Bundle bundle = new Bundle();
        bundle.putString("imageUrl", imageUrl);
        ListingImageCellFragment fragment = new ListingImageCellFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_listing_image_cell, container, false);

        Button deleteBtn = view.findViewById(R.id.deleteImageBtn);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.deleteListingImage(imageUrl);
            }
        });

        ImageView imageView = view.findViewById(R.id.listingImage);

        imageUrl = getArguments().getString("imageUrl");

        Glide.with(ListingImageCellFragment.this).load(imageUrl).into(imageView);

        return view;
    }
}

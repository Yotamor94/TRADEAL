package com.example.tradeal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
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

import java.io.Serializable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ListingImageCellFragment extends Fragment {
    Bitmap image;
    ImageCellListener listener;

    public interface ImageCellListener {
        void deleteListingImage(Bitmap Image);
    }

    public void setListener(ImageCellListener listener){
        this.listener = listener;
    }

    public static ListingImageCellFragment newInstance(Bitmap image){
        Bundle bundle = new Bundle();
        bundle.putParcelable("image", image);
        ListingImageCellFragment fragment = new ListingImageCellFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_listing_image_cell, container, false);

        ImageButton deleteBtn = view.findViewById(R.id.deleteImageBtn);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.deleteListingImage(image);
            }
        });

        ImageView imageView = view.findViewById(R.id.ListingCellImageView);

        if (getArguments().getParcelable("image") != null){
            image = getArguments().getParcelable("image");
        }


        imageView.setImageBitmap(image);

        return view;
    }
}

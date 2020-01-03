package com.example.tradeal;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class UserDialogFragment extends DialogFragment {

    userDialogEventListener listener;

    interface userDialogEventListener{
        void onManageProfileClick();
        void onSignOutClicked();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            listener = (userDialogEventListener)context;
        }catch (ClassCastException ex){
            throw new ClassCastException("The activity must implement userDialogEventListener");
        }
    }

    public static UserDialogFragment newInstance(User user){
        UserDialogFragment fragment = new UserDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_dialog, container, false);

        User user = (User)getArguments().getSerializable("user");

        ImageView userIv = view.findViewById(R.id.userDialogUserImage);
        TextView usernameTv = view.findViewById(R.id.userDialogUsernameTv);
        TextView emailTv = view.findViewById(R.id.userDialogEmailTv);
        Button manageProfileBtn = view.findViewById(R.id.editProfileBtn);
        Button signOutBtn = view.findViewById(R.id.signOutBtn);

        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSignOutClicked();
            }
        });

        manageProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onManageProfileClick();
            }
        });

        if (user != null){
            if (user.getImageUrl() != null && !user.getImageUrl().equals("")){
                Glide.with(getContext()).load(user.getImageUrl()).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.d("loadImage", "onLoadFailed: " + e.getMessage());
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                }).into(userIv);
            }
            usernameTv.setText(user.getUsername());
            emailTv.setText(user.getEmail());
        }


        return view;
    }
}

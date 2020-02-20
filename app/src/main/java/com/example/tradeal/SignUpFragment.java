package com.example.tradeal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;

import java.io.File;

import static android.app.Activity.RESULT_OK;

public class SignUpFragment extends Fragment {

    SignEventListener listener;
    final int CAMERA_REQUEST_CODE = 1;
    ImageButton userPictureBtn;
    Uri lastPictureUri = null;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            listener = (SignEventListener)context;
        }catch(ClassCastException ex){
            throw new ClassCastException("The activity must implement SignEventListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sign_up_dialog, container, false);

        userPictureBtn = view.findViewById(R.id.signUpUserImageBtn);
        Button signUpBtn = view.findViewById(R.id.signUpBtn);
        final EditText emailEt = view.findViewById(R.id.signUpFragmentEmail);
        final EditText usernameEt = view.findViewById(R.id.signUpFragmentUsername);
        final EditText passwordEt = view.findViewById(R.id.signUpFragmentPassword);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usernameEt.getText().toString().equals(""))
                    usernameEt.setError("Can't be blank");
                if (passwordEt.getText().length() < 6)
                    passwordEt.setError("Password must be at least six characters");
                if (!usernameEt.getText().toString().equals("") && passwordEt.getText().length() >= 6)
                    listener.onSignUpClick(emailEt.getText().toString(), usernameEt.getText().toString(), passwordEt.getText().toString(), lastPictureUri);
            }
        });

        userPictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uri = Utils.getImageUri(getActivity());
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        });

        return view;
    }
    Uri uri;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK){
            userPictureBtn.setImageURI(uri);
            lastPictureUri = uri;
           /* Bitmap userPicture = (Bitmap)data.getExtras().get("data");
            userPictureBtn.setImageBitmap(userPicture);
            lastPicture = userPicture;*/
        }
    }
}

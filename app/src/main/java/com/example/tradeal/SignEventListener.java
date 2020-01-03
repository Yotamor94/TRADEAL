package com.example.tradeal;

import android.graphics.Bitmap;

public interface SignEventListener {
    void onSignInClick(String email, String password);
    void onSignUpClick(String Email, String username, String password, Bitmap image);
}

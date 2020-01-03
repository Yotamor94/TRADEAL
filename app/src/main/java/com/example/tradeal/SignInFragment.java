package com.example.tradeal;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class SignInFragment extends Fragment {

    SignEventListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sign_in_dialog, container, false);

        final EditText emailEt = view.findViewById(R.id.signInFragmentEmail);
        final EditText passwordEt = view.findViewById(R.id.signInFragmentPassword);
        Button signInBtn = view.findViewById(R.id.signInBtn);

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSignInClick(emailEt.getText().toString(), passwordEt.getText().toString());
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            listener = (SignEventListener)context;
        }catch (ClassCastException ex){
            throw new ClassCastException("The activity must implement SignEventListener");
        }
    }
}

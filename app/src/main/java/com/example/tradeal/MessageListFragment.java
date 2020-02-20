package com.example.tradeal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MessageListFragment extends Fragment {

    User cUser, oUser;
    ArrayList<Message> messages;
    MessageListEventListener listener;
    RecyclerView recyclerView;

    public static MessageListFragment newInstance(ArrayList<Message> messages, User user, User oUser) {
        Bundle args = new Bundle();
        args.putSerializable("messages", messages);
        args.putSerializable("user", user);
        args.putSerializable("oUser", oUser);
        MessageListFragment fragment = new MessageListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    interface MessageListEventListener {
        void sendMessageClicked(Message message, ArrayList<Message> messages, User oUser);

        void backBtnClicked();

        void deleteConversation(User oUser);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try{
            listener = (MessageListEventListener)context;
        }catch (ClassCastException e){
            throw new ClassCastException("the activity must implement MessageListEventListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_conversation, container, false);

        messages = (ArrayList<Message>) getArguments().getSerializable("messages");
        cUser = (User)getArguments().getSerializable("user");
        oUser = (User)getArguments().getSerializable("oUser");

        if (messages == null){
            messages = new ArrayList<>();
        }
        Collections.sort(messages, new Comparator<Message>() {
            @Override
            public int compare(Message o1, Message o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        });

        final ImageView oUserIv = view.findViewById(R.id.otherUserImageView);
        final TextView otherUserNameTv = view.findViewById(R.id.otherUserNameTv);

        Glide.with(getContext()).asBitmap().load(oUser.getImageUrl()).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                oUserIv.setImageBitmap(Utils.getCroppedBitmap(resource));
                oUserIv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });
        otherUserNameTv.setText(oUser.getUsername());

        LinearLayout backLL = view.findViewById(R.id.conversationBackLinearLayout);
        backLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.backBtnClicked();
            }
        });

        recyclerView = view.findViewById(R.id.messagesRecyclerView);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        conversationChatRecyclerViewAdapter adapter = new conversationChatRecyclerViewAdapter(messages, cUser.getEmail());
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(messages.size() - 1);

        ImageButton sendMessageIb = view.findViewById(R.id.sendMessageBtn);
        final EditText messageEt = view.findViewById(R.id.messageEt);
        sendMessageIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (messageEt.getText().toString().replaceAll(" ", "").isEmpty()){
                    Toast.makeText(getContext(), "Type something first", Toast.LENGTH_SHORT).show();
                }else{
                    Message message = new Message(cUser.getEmail(), messageEt.getText().toString(), oUser.getEmail(), Calendar.getInstance().getTime());
                    messageEt.setText("");
                    listener.sendMessageClicked(message, messages, oUser);
                    recyclerView.getAdapter().notifyItemInserted(messages.size() - 1);
                    recyclerView.scrollToPosition(messages.size() - 1);
                }
            }
        });

        final ImageButton conversationOptionsBtn = view.findViewById(R.id.conversation_menu_btn);
        conversationOptionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getContext(), conversationOptionsBtn);
                popupMenu.inflate(R.menu.converation_options_menu);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.delete){
                            listener.deleteConversation(oUser);
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        MainActivity.mainActivityListener listener = new MainActivity.mainActivityListener() {
            @Override
            public void messageAdded(Message message) {
                messages.add(message);
                Collections.sort(messages, new Comparator<Message>() {
                    @Override
                    public int compare(Message o1, Message o2) {
                        return o1.getDate().compareTo(o2.getDate());
                    }
                });
                recyclerView.getAdapter().notifyItemInserted(messages.size() - 1);
                recyclerView.scrollToPosition(messages.size() - 1);
            }
        };

        ((MainActivity)getActivity()).setListener(listener);

        return view;
    }

}

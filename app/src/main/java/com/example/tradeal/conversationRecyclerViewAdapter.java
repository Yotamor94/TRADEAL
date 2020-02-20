package com.example.tradeal;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.util.Util;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class conversationRecyclerViewAdapter extends RecyclerView.Adapter<conversationRecyclerViewAdapter.ConversationViewHolder> {

    String currentUserEmail;
    ArrayList<User> oUsers;
    ConversationListListener listener;


    interface ConversationListListener {
        void conversationClicked(User oUser, ArrayList<Message> messages);
    }

    public conversationRecyclerViewAdapter(ArrayList<User> oUsers, String currentUserEmail) {
        this.oUsers = oUsers;
        this.currentUserEmail = currentUserEmail;
    }

    public void setListener(ConversationListListener listener) {
        this.listener = listener;
    }

    public class ConversationViewHolder extends RecyclerView.ViewHolder {
        ImageView otherUserIv;
        TextView otherUserUsernameTv;
        TextView lastMessageTv;

        public ConversationViewHolder(@NonNull View itemView) {
            super(itemView);
            this.otherUserIv = itemView.findViewById(R.id.otherUserImage);
            this.otherUserUsernameTv = itemView.findViewById(R.id.otherUserName);
            this.lastMessageTv = itemView.findViewById(R.id.lastMessageTv);
        }
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_conversation, parent, false);
        ConversationViewHolder viewHolder = new ConversationViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ConversationViewHolder holder, int position) {

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        final User oUser = oUsers.get(position);

        if (oUser.getImageUrl() != null) {
            Glide.with(holder.itemView).asBitmap().load(oUser.getImageUrl()).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    holder.otherUserIv.setImageBitmap(Utils.getCroppedBitmap(resource));
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {

                }
            });
        }
        holder.otherUserUsernameTv.setText(oUser.getUsername());
        db.collection("messages").whereIn("oUserEmail", Arrays.asList( oUser.getEmail(), currentUserEmail)).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                final ArrayList<Message> messages = new ArrayList<>(queryDocumentSnapshots.toObjects(Message.class));
                ArrayList<Message> toRemove = new ArrayList<>();
                for (Message message:messages){
                    if (!message.getUserEmail().equals(currentUserEmail) && !message.getUserEmail().equals(oUser.getEmail())){
                        toRemove.add(message);
                    }
                }
                for (Message m:toRemove){
                    messages.remove(m);
                }
                Collections.sort(messages, new Comparator<Message>() {
                    @Override
                    public int compare(Message o1, Message o2) {
                        return o1.getDate().compareTo(o2.getDate());
                    }
                });
                Message lastMessage = messages.get(messages.size() - 1);
                holder.lastMessageTv.setText(lastMessage.getContent());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.conversationClicked(oUser, messages);
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return oUsers.size();
    }
}

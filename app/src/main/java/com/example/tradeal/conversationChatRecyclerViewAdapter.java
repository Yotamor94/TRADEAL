package com.example.tradeal;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.time.Instant;
import java.time.temporal.TemporalField;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static java.time.temporal.ChronoField.EPOCH_DAY;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;

public class conversationChatRecyclerViewAdapter extends RecyclerView.Adapter<conversationChatRecyclerViewAdapter.MessageViewHolder> {

    private ArrayList<Message> messages;
    private String cUserEmail;

    public conversationChatRecyclerViewAdapter(ArrayList<Message> messages, String cUserEmail) {
        this.messages = messages;
        this.cUserEmail = cUserEmail;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView myContentTv;
        TextView myTimeDateTv;
        TextView otherContentTv;
        TextView otherTimeDateTv;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            myContentTv = itemView.findViewById(R.id.messageContentTv);
            myTimeDateTv = itemView.findViewById(R.id.messageTimeTv);
            otherContentTv = itemView.findViewById(R.id.otherMessageContentTv);
            otherTimeDateTv = itemView.findViewById(R.id.otherMessageTimeTv);
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_message_me, parent, false);
        MessageViewHolder messageViewHolder = new MessageViewHolder(view);
        return messageViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);

        holder.myContentTv.setVisibility(View.GONE);
        holder.myTimeDateTv.setVisibility(View.GONE);
        holder.otherContentTv.setVisibility(View.GONE);
        holder.otherTimeDateTv.setVisibility(View.GONE);

        Date time = message.getDate();

        if (message.getUserEmail().equals(cUserEmail)) {
            holder.myContentTv.setText(message.getContent());
            holder.myContentTv.setVisibility(View.VISIBLE);

            if (Calendar.getInstance().getTime().getTime() - time.getTime() <= 86400000){
                if (time.getHours() < 10 && time.getMinutes() < 10) {
                    holder.myTimeDateTv.setText("0" + (time.getHours() + 1) + ":" + "0" + time.getMinutes());
                }else if (time.getHours() < 10){
                    holder.myTimeDateTv.setText("0" + (time.getHours() + 1) + ":" + time.getMinutes());
                }else if (time.getMinutes() < 10){
                    holder.myTimeDateTv.setText((time.getHours() + 1) + ":" + "0" + time.getMinutes());
                }else
                    holder.myTimeDateTv.setText((time.getHours() + 1) + ":" + time.getMinutes());
            }else{
                holder.otherTimeDateTv.setText(time.getDate() + "/" + (time.getMonth() + 1) + "/" + time.getYear());
            }
            holder.myTimeDateTv.setVisibility(View.VISIBLE);

        } else {
            holder.otherContentTv.setText(message.getContent());
            holder.otherContentTv.setVisibility(View.VISIBLE);

            if (Calendar.getInstance().getTime().getTime() - time.getTime() <= 86400000){
                if (time.getHours() < 10 && time.getMinutes() < 10) {
                    holder.otherTimeDateTv.setText("0" + (time.getHours() + 1) + ":" + "0" + time.getMinutes());
                }else if (time.getHours() < 10){
                    holder.otherTimeDateTv.setText("0" + (time.getHours() + 1) + ":" + time.getMinutes());
                }else if (time.getMinutes() < 10){
                    holder.otherTimeDateTv.setText((time.getHours() + 1) + ":" + "0" + time.getMinutes());
                }else
                    holder.otherTimeDateTv.setText((time.getHours() + 1) + ":" + time.getMinutes());
            }else{
                holder.otherTimeDateTv.setText(time.getDate() + "/" + (time.getMonth() + 1) + "/" + time.getYear());
            }
            holder.otherTimeDateTv.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}

package com.example.tradeal;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.core.app.NotificationCompat;
import androidx.core.app.Person;
import androidx.core.app.RemoteInput;
import androidx.core.graphics.drawable.IconCompat;

import static com.example.tradeal.MessagingService.KEY_TEXT_REPLY;

public class NotificationClickReceiver extends BroadcastReceiver {
    ArrayList<Message> lastMessages;

    @Override
    public void onReceive(final Context context, Intent intent) {
        CharSequence messageText = getMessageText(intent);
        final User oUser = (User)intent.getSerializableExtra("oUser");
        final User user = (User)intent.getSerializableExtra("user");
        lastMessages = (ArrayList<Message>) intent.getSerializableExtra("lastMessages");
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();



        if (messageText != null){

            final Message message = new Message(firebaseAuth.getCurrentUser().getEmail(), messageText.toString(), oUser.getEmail(), Calendar.getInstance().getTime());

            NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (lastMessages == null){
                lastMessages = new ArrayList<>();
            }
            lastMessages.add(message);

            String channelId = null;
            if (Build.VERSION.SDK_INT >= 26) {
                channelId = "new_message_channel";
                CharSequence channelName = "New Message";
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
                notificationChannel.enableLights(true);
                notificationChannel.enableVibration(true);
                notificationChannel.setDescription("New messages received by traDeal");
                notificationChannel.setLightColor(Color.CYAN);

                manager.createNotificationChannel(notificationChannel);
            }
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);
            IconCompat icon;
            if (oUser.getImageUrl() == null) {
                icon = IconCompat.createWithResource(context, R.drawable.ic_person_black_24dp);
            } else {
                icon = IconCompat.createWithContentUri(oUser.getImageUrl());
            }
            IconCompat icon1;
            if (oUser.getImageUrl() == null) {
                icon1 = IconCompat.createWithResource(context, R.drawable.ic_person_black_24dp);
            } else {
                icon1 = IconCompat.createWithContentUri(oUser.getImageUrl());
            }
            Person me = new Person.Builder().setName("you").setIcon(icon1).build();
            Person person = new Person.Builder().setName(oUser.getUsername()).setIcon(icon).build();
            NotificationCompat.MessagingStyle notifStyle = new NotificationCompat.MessagingStyle(me);
            for (Message message1 :lastMessages) {
                NotificationCompat.MessagingStyle.Message message2;
                if (message1.getUserEmail() == user.getEmail()){
                     message2 = new NotificationCompat.MessagingStyle.Message(message1.getContent(), message1.getDate().getTime(), me);
                }else{
                     message2 = new NotificationCompat.MessagingStyle.Message(message1.getContent(), message1.getDate().getTime(), person);
                }
                notifStyle.addMessage(message2);

            }
            builder.setSmallIcon(android.R.drawable.sym_def_app_icon).setStyle(notifStyle).setAutoCancel(true);

            Intent intent1 = new Intent("com.example.MESSAGE_CLICKED");
            intent1.putExtra("user", oUser);
            intent1.putExtra("lastMessages", lastMessages);
            intent1.setComponent(new ComponentName(context.getPackageName(), "com.example.tradeal.NotificationClickReceiver"));
            PendingIntent pendingIntent = PendingIntent.getActivity(context, oUser.getEmail().hashCode(), intent1, PendingIntent.FLAG_UPDATE_CURRENT);
            RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY).setLabel("Reply").build();
            NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.ic_send_black_24dp, "Reply", pendingIntent).addRemoteInput(remoteInput).setAllowGeneratedReplies(true).build();
            builder.addAction(action);
            Intent intent2 = new Intent(context, MainActivity.class);
            intent2.putExtra("oUser", oUser);
            intent2.putExtra("user", user);
            PendingIntent pendingIntent1 = PendingIntent.getActivity(context, oUser.getEmail().hashCode() / 2, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent1);
            manager.notify(oUser.getEmail() + user.getEmail(), oUser.getEmail().hashCode(), builder.build());

            Intent serviceIntent = new Intent(context, SendMessageService.class);
            serviceIntent.putExtra("message", message);
            serviceIntent.putExtra("user", user);
            serviceIntent.putExtra("oUser", oUser);
            context.startService(serviceIntent);
        }

    }

    private CharSequence getMessageText(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(KEY_TEXT_REPLY);
        }
        return null;
    }
}

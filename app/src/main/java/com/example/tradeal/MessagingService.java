package com.example.tradeal;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.Person;
import androidx.core.app.RemoteInput;
import androidx.core.graphics.drawable.IconCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MessagingService extends FirebaseMessagingService {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    User user;
    public static String KEY_TEXT_REPLY = "key_text_reply";
    NotificationManager manager;

    @Override
    public void onMessageReceived(@NonNull final RemoteMessage remoteMessage) {

        //Toast.makeText(this, "message received", Toast.LENGTH_LONG).show();
        Log.d("message", "onMessageReceived: ");

        /*Intent intent = new Intent(MessagingService.this, HandleMessageService.class);
        intent.putExtra("remoteMessage", remoteMessage);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        }else {
            startService(intent);
        }*/

        db.collection("users").document(firebaseAuth.getCurrentUser().getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user = documentSnapshot.toObject(User.class);
                Log.d("User got", "user has been successfully been gotten");

                if (remoteMessage.getData().size() > 0) {

                    final Message message = new Gson().fromJson(remoteMessage.getData().get("message"), Message.class);
                    if (!user.getUsersConversedEmails().contains(message.getUserEmail())) {
                        user.addUserConversed(message.getUserEmail());
                        db.collection("users").document(user.getEmail()).update("usersConversedEmail", user.getUsersConversedEmails());
                    }
                    final Intent intent = new Intent("messageReceived");
                    intent.putExtra("message", message);
                    LocalBroadcastManager.getInstance(MessagingService.this).sendBroadcast(intent);

                    SharedPreferences sp = getSharedPreferences("mainActivity", MODE_PRIVATE);
                    if (!applicationInForeground()) {

                        db.collection("users").document(message.getUserEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                final User oUser = documentSnapshot.toObject(User.class);
                                Log.d("User got", "oUser has been successfully been gotten");
/*                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {*/


                                        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
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
                                        NotificationCompat.Builder builder = new NotificationCompat.Builder(MessagingService.this, channelId);
                                        IconCompat icon;
                                        if (oUser.getImageUrl() == null) {
                                            icon = IconCompat.createWithResource(MessagingService.this, R.drawable.ic_person_black_24dp);
                                        } else {
                                            icon = IconCompat.createWithContentUri(oUser.getImageUrl());
                                        }

                                        Person person = new Person.Builder().setName(oUser.getUsername()).setIcon(icon).build();
                                        NotificationCompat.MessagingStyle.Message message1 = new NotificationCompat.MessagingStyle.Message(message.getContent(), message.getDate().getTime(), person);
                                        IconCompat icon1;
                                        if (oUser.getImageUrl() == null) {
                                            icon1 = IconCompat.createWithResource(MessagingService.this, R.drawable.ic_person_black_24dp);
                                        } else {
                                            icon1 = IconCompat.createWithContentUri(oUser.getImageUrl());
                                        }
                                        Person me = new Person.Builder().setName("you").setIcon(icon1).build();
                                        builder.setSmallIcon(android.R.drawable.sym_def_app_icon).setStyle(new NotificationCompat.MessagingStyle(me).addMessage(message1)).setAutoCancel(true);


                                /*builder.setSmallIcon(android.R.drawable.sym_def_app_icon).setGroup("messages")
                                        .setContentTitle(oUser.getUsername()).setContentText(message.getContent())
                                        .setPriority(NotificationCompat.PRIORITY_MAX);*/

                                        Intent intent1 = new Intent("com.example.MESSAGE_CLICKED");
                                        intent1.putExtra("oUser", oUser);
                                        intent1.putExtra("user", user);
                                        ArrayList<Message> lastMessages = new ArrayList<>();
                                        lastMessages.add(message);
                                        intent1.putExtra("lastMessages", lastMessages);
                                        intent1.setComponent(new ComponentName(getPackageName(), "com.example.tradeal.NotificationClickReceiver"));
                                        PendingIntent pendingIntent = PendingIntent.getBroadcast(MessagingService.this.getApplicationContext(), oUser.getEmail().hashCode(), intent1, PendingIntent.FLAG_UPDATE_CURRENT);
                                        RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY).setLabel("Reply").build();
                                        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.ic_send_black_24dp, "Reply", pendingIntent).addRemoteInput(remoteInput).setAllowGeneratedReplies(true).build();
                                        builder.addAction(action);
                                        Intent intent2 = new Intent(MessagingService.this, MainActivity.class);
                                        intent2.putExtra("oUser", oUser);
                                        intent2.putExtra("user", user);
                                        PendingIntent pendingIntent1 = PendingIntent.getActivity(MessagingService.this.getApplicationContext(), oUser.getEmail().hashCode() / 2, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
                                        builder.setContentIntent(pendingIntent1);
                                        manager.notify(oUser.getEmail() + user.getEmail(), oUser.getEmail().hashCode(), builder.build());
/*                                    }
                                }).start();*/
                            }
                        });
                    } /*else {
                        stopForeground(true);
                        stopSelf();
                    }*/
                }

            }
        });
    }


    @Override
    public void onNewToken(@NonNull final String s) {

    }

    private boolean applicationInForeground() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> services = activityManager.getRunningAppProcesses();
        boolean isActivityFound = false;

        if (services.get(0).processName
                .equalsIgnoreCase(getPackageName()) && services.get(0).importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
            isActivityFound = true;
        }

        return isActivityFound;
    }
}

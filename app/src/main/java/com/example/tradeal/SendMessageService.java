package com.example.tradeal;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class SendMessageService extends Service {

    final int SENDING_MESSAGE_NOTID_ID = -1;

    final String apiTokenKey = "AAAAGOilxxw:APA91bEuM-WjZBbl9-QiHD3eC0s0yPc9e6THVUuIWyfGUsGs7MxQGLeQxsCG4aCFIbc8XezlTJUd5IKCrOkdLLMTKoPIgxBBDHK9fnnkQOz1UBDH0nPW3gSZG_ZTqY1R9LIzyMn7d83U";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    NotificationManager manager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String channelId = null;
        if (Build.VERSION.SDK_INT >= 26) {
            channelId = "sending_message_channel";
            CharSequence channelName = "Sending Message";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
            notificationChannel.setDescription("Notification displayed while sending a message in traDeal");

            manager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);
        builder.setSmallIcon(android.R.drawable.sym_def_app_icon).setContentTitle("Sending message").setContentText("message is being sent").setProgress(100,0,true);
        startForeground(SENDING_MESSAGE_NOTID_ID, builder.build());

        final Message message = (Message)intent.getSerializableExtra("message");
        final User user = (User)intent.getSerializableExtra("user");
        final User oUser = (User)intent.getSerializableExtra("oUser");

        db.collection("messages").add(message).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                message.setMessageId(documentReference.getId());
                db.collection("messages").document(message.getMessageId()).update("messageId", message.getMessageId());

                final String messageJsonString = new Gson().toJson(message);
                Log.d("messageJson", "String: " + messageJsonString);
                if (oUser.getDeviceTokens().size() > 1) {



                    /*String createGroupUrl = "https://fcm.googleapis.com/fcm/notification";

                    if (oUser.isGroupCreated()){
                        String url = createGroupUrl + "?notification_key_name=" + oUser.getEmail();

                        RequestQueue requestQueue3 = Volley.newRequestQueue(SendMessageService.this);
                        StringRequest getRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("VolleyError", "onResponse: " + response);
                                String[] responseParts = response.split(":");
                                final JSONObject messageJson = new JSONObject();
                                try {
                                    messageJson.put("to", responseParts[1].replace("\"", ""));
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("message", messageJsonString);
                                    messageJson.put("data", jsonObject);
                                    sendMessage(jsonObject.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("VolleyError", "onErrorResponse: " + error.toString() + error.networkResponse.statusCode);
                            }
                        }){
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> headers = new HashMap<>();
                                headers.put("Authorization", "key=" + apiTokenKey);
                                headers.put("project_id", "106982393628");
                                return headers;
                            }

                            @Override
                            public String getBodyContentType() {
                                return "application/json";
                            }

                            @Override
                            public byte[] getBody() throws AuthFailureError {
                                return "".getBytes();
                            }
                        };
                        requestQueue3.add(getRequest);
                    }else{
                        final JSONObject rootObject = new JSONObject();

                        try {
                            rootObject.put("operation", "create");
                            rootObject.put("notification_key_name", message.getoUserEmail());
                            rootObject.put("registration_ids", new Gson().toJson(oUser.getDeviceTokens()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



                        Response.Listener<String> listener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                oUser.setGroupCreated(true);
                                db.collection("users").document(oUser.getEmail()).update("isGroupCreated", true);
                                Log.d("VolleyError", "onResponse: " + response);
                                String[] responseParts = response.split(":");
                                final JSONObject messageJson = new JSONObject();
                                try {
                                    messageJson.put("to", responseParts[1].replace("\"", ""));
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("message", messageJsonString);
                                    messageJson.put("data", jsonObject);
                                    sendMessage(jsonObject.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                                // requestQueue1.start();
                            }
                        };

                        final RequestQueue requestQueue = Volley.newRequestQueue(SendMessageService.this);
                        StringRequest request = new StringRequest(Request.Method.POST, createGroupUrl, listener, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("VolleyError", "onErrorResponse: " + error.toString() + error.networkResponse.statusCode);
                            }
                        }) {
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> headers = new HashMap<>();
                                headers.put("Authorization", "key=" + apiTokenKey);
                                headers.put("project_id", "106982393628");
                                return headers;
                            }

                            @Override
                            public String getBodyContentType() {
                                return "application/json";
                            }

                            @Override
                            public byte[] getBody() throws AuthFailureError {
                                return rootObject.toString().getBytes();
                            }
                        };
                        requestQueue.add(request);
                    }


*/
                    //requestQueue.start();

                } //else if (oUser.getDeviceTokens().size() == 1) {
                    JSONObject rootObject = new JSONObject();
                    try {
                        rootObject.put("to", oUser.getDeviceTokens().get(oUser.getDeviceTokens().size() - 1));
                        JSONObject messageJson = new JSONObject();
                        messageJson.put("message", messageJsonString);
                        rootObject.put("data", messageJson);

                        sendMessage(rootObject.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                //}
            }
        });

        return super.onStartCommand(intent, flags, startId);
    }

    public void sendMessage(final String JsonMessage) {
        String sendUrl = "https://fcm.googleapis.com/fcm/send";

        RequestQueue requestQueue1 = Volley.newRequestQueue(SendMessageService.this);
        StringRequest request1 = new StringRequest(Request.Method.POST, sendUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("VolleyError", "onResponse: " + response);
                stopForeground(true);
                stopSelf();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VolleyError", "onResponse: " + error.toString() + error.networkResponse.statusCode);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                // headers.put("Content-Type", "application/json");
                headers.put("Authorization", "key=" + apiTokenKey);
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return JsonMessage.getBytes();
            }
        };
        requestQueue1.add(request1);
    }
}

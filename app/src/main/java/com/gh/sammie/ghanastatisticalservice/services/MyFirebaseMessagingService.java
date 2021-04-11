package com.gh.sammie.ghanastatisticalservice.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.gh.sammie.ghanastatisticalservice.MainActivity;
import com.gh.sammie.ghanastatisticalservice.R;
import com.gh.sammie.ghanastatisticalservice.model.NotificationsModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String LESSON_PLAN_CHANNEL_ID = "com.gh.sammie.ghanastatisticalservice.services";
    private static final String TAG = "MessagingService";
    public static final String NOTIFICATION_DATABASE = "Ghana_statistiscal_broadscast";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("newToken", token);
        getSharedPreferences("_", MODE_PRIVATE).edit().putString("token", token).apply();

//        sendToServer(token);

//        if (!TextUtils.isEmpty(token)) {
//            Log.d("newToken", token);
//            sendToServer(token);
//
//            getSharedPreferences("_", MODE_PRIVATE).edit().putString("token", token).apply();
//        }

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "From FCM: " + remoteMessage.getFrom());
        if (remoteMessage.getData().size() > 0) {
            if (remoteMessage.getData().get("isAlert") != null) {
                Log.d(TAG, "FCM Title: " + remoteMessage.getData().get("title"));
                Log.d(TAG, "FCM Body: " + remoteMessage.getData().get("body"));//content

                Map<String, String> notification = remoteMessage.getData();
                String title = notification.get("title");
                String message = notification.get("body");

//                storeNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("body"));
                storeNotification(title, message);
                sendNotification(title, message);


            }
        } else if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String message = remoteMessage.getNotification().getBody();
            storeNotification(title, message);
            Log.d(TAG, "Console Title: " + remoteMessage.getNotification().getTitle());
            Log.d(TAG, "Console Body: " + remoteMessage.getNotification().getBody());


        }
    }

    private void sendNotification(String title, String message) {
        //sounds
        long[] pattern = {0, 100, 1000, 300, 200, 100, 500, 200, 100};
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

//        Log.d("msg", "onMessageReceived: " + remoteMessage.getData().get("body"));
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        String channelId = "Default";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setVibrate(pattern)
                .setColor(Color.parseColor("#ffffff"))
                .setLights(Color.RED, 1000, 300)
                .setAutoCancel(true)
                .setSound(sound)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true).setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, LESSON_PLAN_CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
        if (manager != null) {
            manager.notify(0, builder.build());
        }
    }

    private void storeNotification(String title, String message) {
        Log.d(TAG, "storeNotification: " + title + message);
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            NotificationsModel notificationLoad = new NotificationsModel();
            notificationLoad.setTitle(title);
            notificationLoad.setMessage(message);
//            notificationLoad.setTimeStamp(ServerValue.TIMESTAMP);
//        Map<String, Object> payloadMap = new HashMap<>();
//        payloadMap.put("title", title);
//        payloadMap.put("message", message);

            String uid = mAuth.getCurrentUser().getUid();
//            String name = mAuth.getCurrentUser().getDisplayName();

            DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference(NOTIFICATION_DATABASE).child(uid);
            mDatabaseRef.keepSynced(true);
            String key = mDatabaseRef.push().getKey(); //this wll create a unique key but

            Map<String, Object> value = new HashMap<>();
            value.put("title", title);
            value.put("message", message);
            value.put("timeStampValue", ServerValue.TIMESTAMP);
            if (key != null) {
                mDatabaseRef.child(key).setValue(value).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Success: stored notification");
                        } else {
                            Toast.makeText(MyFirebaseMessagingService.this, "Failed to store message", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

//            if (key != null) {
//                //remove key for now
//                mDatabaseRef.child(key).setValue(notificationLoad).addOnSuccessListener(aVoid -> Log.d(TAG, "store to database =>: Success")).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.d(TAG, MessageFormat.format("fail{0}", e.getMessage()));
//                    }
//                });
//            }
        }
        else
            Log.d(TAG, "storeNotification: " + "failed to store to database");

    }

    private static void sendToServer(String token) {
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
//            String deviceToken = instanceIdResult.getToken();
        Log.d("Token", "OfflineToken: " + token);

        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            String name = mAuth.getCurrentUser().getUid();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference tokenStore = database.getReference("TokenStore");
            tokenStore.keepSynced(true);
            if (!TextUtils.isEmpty(name)) {
                tokenStore.child(userId).child(name)
                        .child("device_token")
                        .setValue(token).addOnSuccessListener(aVoid -> Log.d("Token", "" + token))
                        .addOnFailureListener(e -> Log.d("Token", "Failed to save token: "));
            } else
                tokenStore.child(userId).child("Anonymous")
                        .child("device_token")
                        .setValue(token).addOnSuccessListener(aVoid -> Log.d("Token", MessageFormat.format("{0}", token)))
                        .addOnFailureListener(e -> Log.d("Token", "Failed to save token: "));

        }

    }

    public static String getToken(Context context) {
        String token_value = context.getSharedPreferences("_", MODE_PRIVATE).getString("token", "defaultNullValue");
//        String s = context.getSharedPreferences("_", MODE_PRIVATE).getString("token", "default");
        Log.d("token_value", "getToken: " + token_value);
        sendToServer(token_value);
        return context.getSharedPreferences("_", MODE_PRIVATE).getString("token", "defaultNullValue");
    }


//    public static void getToken(Context context) {
//        getToken(context);
////        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
////        String token = prefs.getString("token", "defaultStringIfNothingFound");
////
////
//////        String s = context.getSharedPreferences("_", MODE_PRIVATE).getString("token", "default");
////        Log.d("newToken", "getToken: " + token);
//        sendToServer(defaultString);
//
//    }
}

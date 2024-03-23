package com.example.coffeediasfp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.Firebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NotificationBackgroundService extends Service {


    private static final String CHANNEL_ID = "ForegroundServiceChannel";
    private static final int NOTIFICATION_ID = 1;

    private DatabaseReference  notificationRef;
    @Override
    public void onCreate() {
        super.onCreate();
        // initialize firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        notificationRef = database.getReference("DiseaseNotifications");

        //create a notification channel for Android OReo and higher

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel  = new NotificationChannel(CHANNEL_ID, "Foreground Service Channel", NotificationManager.IMPORTANCE_DEFAULT);
            
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
        // send Sms
//        sendSms();
//        // send Email
//        sendEmail();
//        // send notifications
//        sendNotifications();
//        //save notifications
//        saveNotification();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendSms(){}
    private void sendEmail(){}
    private void sendNotifications(){
        Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setContentTitle("Hello World")
                .setContentTitle("Your Message")
                .build();
        startForeground(NOTIFICATION_ID, notification);
    }
    private void saveNotification(){}
}

package com.pivotal.bootcamp;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import io.pivotal.android.push.service.GcmService;

public class MyPushService extends GcmService {

    @Override
    public void onReceiveMessage(Bundle payload) {
        if (payload.containsKey("message")) {
            final String message = payload.getString("message");
            handleMessage(message);
        }
    }

    private void handleMessage(String msg) {
        // Your code here. Display the message
        // on the device's bar as a notification.
        Log.i("MyLogTag", "Received message: " + msg);
        showNotificationOnStatusBar(msg);

        final String pushMsg = msg;

        Handler mHandler = new Handler(getMainLooper());
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), pushMsg, Toast.LENGTH_LONG).show();
//                new AlertDialog.Builder(getApplicationContext())
//                        .setTitle("Push Notification")
//                        .setMessage(pushMsg)
//                        .create()
//                        .show();
            }
        });

    }

    private void showNotificationOnStatusBar(String msg) {
        final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        final PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Push Workshop")
                .setContentIntent(contentIntent)
                .setContentText(msg);

        notificationManager.notify(1, builder.build());
    }
}
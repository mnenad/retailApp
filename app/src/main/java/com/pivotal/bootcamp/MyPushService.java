package com.pivotal.bootcamp;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import io.pivotal.android.push.service.GcmService;

public class MyPushService extends GcmService {

    @Override
    public void onReceiveMessage(Bundle payload) {
        Log.i("onReceiveMessage", "Received payload: " + payload);
        if (payload.containsKey("message")) {
            final String message = payload.getString("message");
            Log.i("onReceiveMessage", "message: " + message);
            handleMessage(message);
        }
    }

    private void handleMessage(String msg) {
        // Your code here. Display the message
        // on the device's bar as a notification.
        Log.i("handleMessage", "message: " + msg);
        if (msg.indexOf("get/curators.json")>0){
            msg=msg.substring(0,msg.indexOf("get/curators.json"));
            Log.i("handleMessage", "Cleaned up message: " + msg);

        }
        showNotificationOnStatusBar(msg);

        final String pushMsg = msg;

        if (pushMsg.indexOf("http")>=0) {
            Log.i("handleMessage", "http found in the message: " + pushMsg);
            final String pushUrl = msg.substring(msg.indexOf("http"), msg.length()-1);
            Handler mHandler = new Handler(getMainLooper());
            mHandler.post(new Runnable() {
                @Override
                public void run() {

                    AlertDialog pushAlert = new AlertDialog.Builder(getApplicationContext())
                            .setTitle("Check Weekly Ad & Store Details")
                            .setMessage(pushMsg)
                            .setPositiveButton("GO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse(pushUrl));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                    .create();
                    pushAlert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                    pushAlert.show();

                }
            });
        }else{
            //Toast.makeText(getApplicationContext(), pushMsg, Toast.LENGTH_LONG).show();
            //my change : todo: test this
            Log.i("handleMessage", "http NOT found in the message: " + pushMsg);
            Handler mHandler = new Handler(getMainLooper());
            mHandler.post(new Runnable() {
                @Override
                public void run() {

                    AlertDialog pushAlert = new AlertDialog.Builder(getApplicationContext())
                            .setTitle("Push Notification")
                            .setMessage(pushMsg)
                            .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create();
                    pushAlert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                    pushAlert.show();

                }
            });

            //my change end

        }

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
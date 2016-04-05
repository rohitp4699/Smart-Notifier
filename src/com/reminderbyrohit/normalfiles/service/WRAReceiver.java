package com.reminderbyrohit.normalfiles.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.reminderbyrohit.normalfiles.MainActivity;
import com.rohit.smartnotifier.R;

public class WRAReceiver extends BroadcastReceiver {

    private static final int WATER_REMINDER_TASK_NOTIFICATION_ID = 0;
    static int notificationCount = 1;

    private String TAG = WRAReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "AlarmReceiver -> onReceive called");
        sendNotification(context, "Water Alarm", "Drink some water alarm");
    }
    // Put the message into a notification and post it.
    private void sendNotification(Context context, String title, String msg) {
        Log.d(TAG, "sendNotification called with msg " + msg);
        NotificationManager waterReminderNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);


        Intent goingIntent = new Intent(context, MainActivity.class);
        goingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, goingIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder waterNotificationBuilder = new NotificationCompat.Builder(
                context).setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg);

        waterNotificationBuilder.setContentIntent(contentIntent);
        waterNotificationBuilder.setNumber(notificationCount++);
        waterReminderNotificationManager.notify(WATER_REMINDER_TASK_NOTIFICATION_ID, waterNotificationBuilder.build());
    }
}

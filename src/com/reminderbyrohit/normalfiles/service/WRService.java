package com.reminderbyrohit.normalfiles.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.reminderbyrohit.normalfiles.MainActivity;
import com.rohit.smartnotifier.R;

import java.util.Timer;
import java.util.TimerTask;

public class WRService extends IntentService {
    private static final int WATER_REMINDER_TASK_NOTIFICATION_ID = 1;
    private final String TAG = "WATER_REMINDER_SERVICE";
    public static final String WATER_REMINDER_TASK = "com.reminderbyrohit.normalfiles.service.action.WATER_REMINDER_TASK";
    public static final String WATER_REMINDER_TASK_REPEATED = "com.reminderbyrohit.normalfiles.service.action.WATER_REMINDER_TASK_REPEATED";
    public static final String WATER_REMINDER_TASK_REPEATED_CANCEL = "com.reminderbyrohit.normalfiles.service.action.WATER_REMINDER_TASK_REPEATED_CANCEL";
    private NotificationManager waterReminderNotificationManager;
    final static long SECOND = 1000;
    final static long MINUTE = 60000;
    private TimerTask waterReminderRepeatedTask;
    private Timer waterReminderRepeatedTimer;

    public WRService() {
        super("WaterReminderService");
        Log.i(TAG, "WaterReminderService constructor");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "onHandleIntent called");
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case WATER_REMINDER_TASK:
                        Log.i(TAG, "WATER_REMINDER_TASK called");
                        sendNotification("Stay Hydrated", "Water or Juice. Your choice.");
                        break;
                    case WATER_REMINDER_TASK_REPEATED:
                        Log.i(TAG, "WATER_REMINDER_TASK_REPEATED called");
                        waterReminderRepeatedTask = new TimerTask() {
                            @Override
                            public void run() {
                                sendNotification("Have some liquid", "Staying hydrated is easy. Drink some water or juice.");
                            }
                        };
                        waterReminderRepeatedTimer = new Timer("waterReminderRepeatedTimer");
                        waterReminderRepeatedTimer.scheduleAtFixedRate(waterReminderRepeatedTask, 0, 15 * SECOND);
                        break;
                    case WATER_REMINDER_TASK_REPEATED_CANCEL:
                        Log.i(TAG, "WATER_REMINDER_TASK_REPEATED_CANCEL called");
                        if (waterReminderRepeatedTimer != null) {
                            Log.i(TAG, "waterReminderRepeatedTask cancelled");
                            waterReminderRepeatedTimer.cancel();
                        } else {
                            Log.i(TAG, "waterReminderRepeatedTimer is null");
                        }
                        break;
                    default:
                        Log.i(TAG, "default case called. This should never happen.");
                        break;
                }
            }
        }
    }

    // Put the message into a notification and post it.
    private void sendNotification(String title, String msg) {
        Log.d(TAG, "sendNotification called with msg " + msg);
        waterReminderNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);


        Intent goingIntent = new Intent(this, MainActivity.class);
        goingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, goingIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        waterReminderNotificationManager.notify(WATER_REMINDER_TASK_NOTIFICATION_ID, mBuilder.build());
    }
}

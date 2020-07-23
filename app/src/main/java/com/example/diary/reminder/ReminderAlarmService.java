package com.example.diary.reminder;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.example.diary.AddReminder;
import com.example.diary.R;
import com.example.diary.data.AlarmReminderContract;

public class ReminderAlarmService extends IntentService {

    private static final String TAG = ReminderAlarmService.class.getSimpleName();
    private static final int NOTIFICATION_ID = 42;
    Cursor cursor;
    public final String CHANNEL_ID = "001";
    //private final static String default_notification_channel_id = "default" ;
    //private final String CHANNEL_ID = "Channel ID";
    //private final Uri soundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

    public static PendingIntent getReminderPendingIntent(Context context, Uri uri)
    {
        Intent action = new Intent(context,ReminderAlarmService.class);
        action.setData(uri);
        return PendingIntent.getService(context,0,action,PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public ReminderAlarmService()
    {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent)
    {
        Uri uri = intent.getData();

        // Displaying notification to view the task details
        Intent action = new Intent(this, AddReminder.class);
        action.setData(uri);
        PendingIntent operation = TaskStackBuilder.create(this)
                .addNextIntentWithParentStack(action)
                .getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        if(uri != null) {
            cursor = getContentResolver().query(uri, null, null, null, null);
        }
        String description = "";
        try {
            if(cursor != null && cursor.moveToFirst())
            {
                description = AlarmReminderContract.getColumnString(cursor,AlarmReminderContract.AlarmReminderEntry.KEY_TITLE);
            }
        }finally {
            if(cursor != null)
            {
                cursor.close();
            }
        }
        //Settings.System.DEFAULT_NOTIFICATION_URI
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,"001", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription(description);

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);

            Notification note = new NotificationCompat.Builder(ReminderAlarmService.this,CHANNEL_ID)
                    .setContentTitle(getString(R.string.reminder_title))
                    .setContentText(description)
                    .setSmallIcon(R.drawable.ic_add_alert_black_24dp)
                    .setContentIntent(operation)
                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setAutoCancel(true)
                    .build();
            manager.notify(NOTIFICATION_ID, note);
        }
        else
        {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            Notification note = new NotificationCompat.Builder(ReminderAlarmService.this)
                    .setContentTitle(getString(R.string.reminder_title))
                    .setContentText(description)
                    .setSmallIcon(R.drawable.ic_add_alert_black_24dp)
                    .setContentIntent(operation)
                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setAutoCancel(true)
                    .build();
            manager.notify(NOTIFICATION_ID, note);
        }
    }
}

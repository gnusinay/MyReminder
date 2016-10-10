package com.mercury.gnusin.myreminder;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ReminderStorage reminderStorage = new ReminderStorage(context);
        Reminder reminder = reminderStorage.restore();

        PendingIntent activityIntent = PendingIntent.getActivity(context, 0, new Intent(context, MyReminder_.class), PendingIntent.FLAG_CANCEL_CURRENT);

        Notification notification = new Notification.Builder(context)
                .setSmallIcon(R.mipmap.ic_notif)
                .setContentTitle(reminder.getTitle())
                .setContentText(reminder.getDescription())
                .setContentIntent(activityIntent)
                .setAutoCancel(true)
                .build();


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(1, notification);
    }
}

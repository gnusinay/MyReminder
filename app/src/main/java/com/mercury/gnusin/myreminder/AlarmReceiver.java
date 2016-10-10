package com.mercury.gnusin.myreminder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by gnusin on 07.10.2016.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        PendingIntent activityIntent = PendingIntent.getActivity(context, 0, new Intent(context, MyReminder_.class), PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder(context)
                .setSmallIcon(R.mipmap.ic_notif)
                .setContentTitle(intent.getStringExtra("title"))
                .setContentText(intent.getStringExtra("description"))
                .setContentIntent(activityIntent)
                .build();


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(1, notification);
    }
}

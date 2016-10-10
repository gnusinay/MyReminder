package com.mercury.gnusin.myreminder;

import android.app.AlarmManager;
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


public class ReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            ReminderStorage reminderStorage = new ReminderStorage(context);
            Reminder reminder = reminderStorage.restore();

            if (!reminder.getTitle().isEmpty()) {
                SimpleDateFormat dateFormat = (SimpleDateFormat) DateFormat.getDateFormat(context);
                SimpleDateFormat timeFormat = (SimpleDateFormat) DateFormat.getTimeFormat(context);

                SimpleDateFormat dateTimeFormat = new SimpleDateFormat(dateFormat.toPattern() + " " + timeFormat.toPattern());
                Date alarmDate = dateTimeFormat.parse(reminder.getDate() + " " + reminder.getTime());

                Intent broadcastIntent = new Intent(context, AlarmReceiver.class);
                PendingIntent broadcastPendingIntent = PendingIntent.getBroadcast(context, 0, broadcastIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(alarmDate.getTime());

                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTime().getTime(), broadcastPendingIntent);
            }
        } catch (Exception e) {

        }
    }
}

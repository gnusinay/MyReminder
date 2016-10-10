package com.mercury.gnusin.myreminder;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Shader;

public class ReminderStorage {

    private static final String TITLE = "title";
    private static final String DATE = "date";
    private static final String TIME = "time";
    private static final String DESCRIPTION = "description";

    private SharedPreferences preferences;

    public ReminderStorage(Context context) {
        preferences = context.getSharedPreferences("com.mercury.gnusin.myreminder.app_prefs", Context.MODE_PRIVATE);
    }

    public void store(Reminder reminder) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(TITLE, reminder.getTitle());
        editor.putString(DATE, reminder.getDate());
        editor.putString(TIME, reminder.getTime());
        editor.putString(DESCRIPTION, reminder.getDescription());
        editor.apply();
    }

    public Reminder restore() {
        Reminder reminder = new Reminder();
        reminder.setTitle(preferences.getString(TITLE, ""));
        reminder.setDate(preferences.getString(DATE, ""));
        reminder.setTime(preferences.getString(TIME, ""));
        reminder.setDescription(preferences.getString(DESCRIPTION, ""));
        return reminder;
    }
}

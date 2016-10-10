package com.mercury.gnusin.myreminder;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FocusChange;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringArrayRes;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

@EActivity(R.layout.a_reminder)
public class MyReminder extends Activity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    @Pref
    MyPrefs_ myPrefs;

    @ViewById(R.id.title_edit_text)
    EditText titleEditText;

    @ViewById(R.id.date_edit_text)
    EditText dateEditText;

    @ViewById(R.id.time_edit_text)
    EditText timeEditText;

    @ViewById(R.id.description_edit_text)
    EditText descriptionEditText;

    @ViewById(R.id.save_button)
    Button saveButton;

    private Reminder reminder;

    @AfterViews
    void init() {
        if(myPrefs.hasReminder().get()) {
            reminder = restoreRemainder();

            titleEditText.setText(reminder.getTitle());
            dateEditText.setText(reminder.getDate());
            timeEditText.setText(reminder.getTime());
            descriptionEditText.setText(reminder.getDescription());
        }
    }

    @FocusChange(R.id.date_edit_text)
    void onFocusChangeDateEdit(boolean hasFocus) {
        if (hasFocus) {
            onClickDateEdit();
        }
    }

    @Click(R.id.date_edit_text)
    void onClickDateEdit() {
        Calendar calendar = Calendar.getInstance();

        if (!dateEditText.getText().toString().isEmpty()) {
            try {
                String dateStr = dateEditText.getText().toString();
                java.text.DateFormat dateFormat = DateFormat.getDateFormat(this);
                Date date = dateFormat.parse(dateStr);
                calendar.setTime(date);
            } catch (ParseException e) {
                Toast.makeText(this, R.string.parsingDateErrorMessage, Toast.LENGTH_LONG).show();
            }
        }

        DatePickerDialog dialog = new DatePickerDialog(this,
                                                       this,
                                                       calendar.get(Calendar.YEAR),
                                                       calendar.get(Calendar.MONTH),
                                                       calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }



    @FocusChange(R.id.time_edit_text)
    void onFocusChangeTimeEdit(boolean hasFocus) {
        if (hasFocus) {
            onClickTimeEdit();
        }
    }

    @Click(R.id.time_edit_text)
    void onClickTimeEdit() {
        Calendar calendar = Calendar.getInstance();

        if (!timeEditText.getText().toString().isEmpty()) {
            try {
                String timeStr = timeEditText.getText().toString();
                java.text.DateFormat timeFormat = DateFormat.getTimeFormat(this);
                Date time = timeFormat.parse(timeStr);
                calendar.setTimeInMillis(time.getTime());
            } catch (ParseException e) {
                Toast.makeText(this, R.string.parsingTimeErrorMessage, Toast.LENGTH_LONG).show();
            }
        }

        TimePickerDialog dialog = new TimePickerDialog(this,
                                                       this,
                                                       calendar.get(Calendar.HOUR_OF_DAY),
                                                       calendar.get(Calendar.MINUTE),
                                                       DateFormat.is24HourFormat(this));
        dialog.show();
    }

    @Click(R.id.save_button)
    void onClickSaveButton() {
        if (titleEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, R.string.titleEmptyErrorMessage, Toast.LENGTH_SHORT).show();
            return;
        } else if (timeEditText.getText().toString().isEmpty()){
            Toast.makeText(this, R.string.timeEmptyErrorMessage, Toast.LENGTH_SHORT).show();
            return;
        } else if (dateEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, R.string.dateEmptyErrorMessage, Toast.LENGTH_SHORT).show();
            return;
        }

        Reminder reminder = new Reminder();
        reminder.setTitle(titleEditText.getText().toString());
        reminder.setDate(dateEditText.getText().toString());
        reminder.setTime(timeEditText.getText().toString());
        reminder.setDescription(descriptionEditText.getText().toString());

        retainRemainder(reminder);
        try {
            generateNotification(reminder);
        } catch (ParseException e) {
            Toast.makeText(this, R.string.parsingDateErrorMessage, Toast.LENGTH_LONG).show();    // TODO
        }

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        SimpleDateFormat timeFormat = (SimpleDateFormat) DateFormat.getTimeFormat(this);
        timeEditText.setText(timeFormat.format(calendar.getTime()));
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        SimpleDateFormat dateFormat = (SimpleDateFormat) DateFormat.getDateFormat(this);
        dateEditText.setText(dateFormat.format(calendar.getTime()));
    }

    private void retainRemainder(Reminder reminder) {
        myPrefs.hasReminder().put(true);
        myPrefs.titleReminder().put(reminder.getTitle());
        myPrefs.dateReminder().put(reminder.getDate());
        myPrefs.timeReminder().put(reminder.getTime());
        myPrefs.descriptionReminder().put(reminder.getDescription());
    }

    private Reminder restoreRemainder() {
        Reminder reminder = new Reminder();
        reminder.setTitle(myPrefs.titleReminder().get());
        reminder.setDate(myPrefs.dateReminder().get());
        reminder.setTime(myPrefs.timeReminder().get());
        reminder.setDescription(myPrefs.descriptionReminder().get());
        return reminder;
    }

    private void generateNotification(Reminder reminder) throws ParseException {
        SimpleDateFormat dateFormat = (SimpleDateFormat) DateFormat.getDateFormat(this);
        SimpleDateFormat timeFormat = (SimpleDateFormat) DateFormat.getTimeFormat(this);

        SimpleDateFormat dateTimeFormat = new SimpleDateFormat(dateFormat.toPattern() + " " + timeFormat.toPattern());
        Date alarmDate = dateTimeFormat.parse(reminder.getDate() + " " + reminder.getTime());

        Intent broadcastIntent = new Intent(this, AlarmReceiver.class);
        broadcastIntent.putExtra("title", reminder.getTitle());
        broadcastIntent.putExtra("description", reminder.getDescription());

        PendingIntent broadcastPendingIntent = PendingIntent.getBroadcast(this, 0, broadcastIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(alarmDate.getTime());

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTime().getTime(), broadcastPendingIntent);
    }
}

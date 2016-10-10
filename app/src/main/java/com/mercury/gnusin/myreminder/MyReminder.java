package com.mercury.gnusin.myreminder;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.DateFormat;
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
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.androidannotations.api.sharedpreferences.SharedPreferencesHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@EActivity(R.layout.a_reminder)
public class MyReminder extends Activity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

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

    private ReminderStorage reminderStorage;


    @AfterViews
    void init() {
        reminderStorage = new ReminderStorage(this);
        Reminder reminder = reminderStorage.restore();

        titleEditText.setText(reminder.getTitle());
        dateEditText.setText(reminder.getDate());
        timeEditText.setText(reminder.getTime());
        descriptionEditText.setText(reminder.getDescription());

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
        } else if (dateEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, R.string.dateEmptyErrorMessage, Toast.LENGTH_SHORT).show();
            return;
        } else if (timeEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, R.string.timeEmptyErrorMessage, Toast.LENGTH_SHORT).show();
            return;
        }

        Reminder reminder = new Reminder();
        reminder.setTitle(titleEditText.getText().toString());
        reminder.setDate(dateEditText.getText().toString());
        reminder.setTime(timeEditText.getText().toString());
        reminder.setDescription(descriptionEditText.getText().toString());

        reminderStorage.store(reminder);
        sendBroadcast(new Intent("com.mercury.gnusin.myreminder.CUSTOM_ACTION"));
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



}

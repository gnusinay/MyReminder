package com.mercury.gnusin.myreminder;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.DefaultString;
import org.androidannotations.annotations.sharedpreferences.SharedPref;


@SharedPref
public interface MyPrefs {

    @DefaultBoolean(false)
    boolean hasReminder();

    @DefaultString("")
    String titleReminder();

    @DefaultString("")
    String dateReminder();

    @DefaultString("")
    String timeReminder();

    @DefaultString("")
    String descriptionReminder();
}

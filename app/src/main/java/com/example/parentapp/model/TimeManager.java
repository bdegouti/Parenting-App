package com.example.parentapp.model;

/**
 * TimeManager class stores the minutes in milliseconds that the user selects for the countdown timer.
 * The time is by default set to 60,000 milliseconds (which is 1 minute)
 */
public class TimeManager {
    private long minuteInMillis;

    public TimeManager(long minute) {
        this.minuteInMillis = minute;
    }

    public long getMinuteInMillis() {
        return minuteInMillis;
    }

    public void setMinuteInMillis(long minuteInMillis) {
        this.minuteInMillis = minuteInMillis;
    }
}


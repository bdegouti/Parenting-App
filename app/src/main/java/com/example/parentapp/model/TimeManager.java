package com.example.parentapp.model;

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


package com.example.parentapp.model;

/**
 * TimeManager class stores the minutes in milliseconds that the user selects for the countdown timer.
 * The time is by default set to 60,000 milliseconds (which is 1 minute)
 */
public class TimeManager {
    private double minuteInMillis;
    private double rateOfSpeed;

    public TimeManager(long minuteInMillis) {
        this.minuteInMillis = minuteInMillis;
        this.rateOfSpeed = 1;
    }

    public double getMinuteInMillis() {
        return minuteInMillis;
    }

    public void setMinuteInMillis(double minuteInMillis) {
        this.minuteInMillis = minuteInMillis;
    }

    public double getRateOfSpeed() {
        return rateOfSpeed;
    }

    public void setRateOfSpeed(double rateOfSpeed) {
        this.rateOfSpeed = rateOfSpeed;
    }
}
package com.example.parentapp.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class WhoseTurn {
    String date;
    Child child;

    public WhoseTurn(Child child){
        setLocalDateTime();
        this.child = child;
    }

    public Child getChild() {
        return child;
    }

    public String getDate() {
        return date;
    }

    private void setLocalDateTime(){
        LocalDateTime creationDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
        this.date = creationDateTime.format(formatter);
    }
}

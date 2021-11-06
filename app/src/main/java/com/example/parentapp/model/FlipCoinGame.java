package com.example.parentapp.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class FlipCoinGame {
    public enum FlipOptions {HEAD, TAIL}
    private LocalDateTime creationDateTime;
    private String creationDateTimeString;
    private String pickerName;
    private int pickerIndex;
    private FlipOptions pickerChoice;
    private FlipOptions result;

    public FlipCoinGame()
    {
        creationDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
        creationDateTimeString = creationDateTime.format(formatter);
        pickerName = null;
        pickerIndex = -1;
        pickerChoice = FlipOptions.HEAD;
        result = null;
    }

    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    public String getCreationDateTimeString() {
        return creationDateTimeString;
    }

    public String getPickerName() {
        return pickerName;
    }

    public FlipOptions getPickerChoice()
    {
        return pickerChoice;
    }

    public int getPickerIndex()
    {
        return pickerIndex;
    }

    public FlipOptions getResult()
    {
        return result;
    }

    public void setCreationDateTime(String dateTimeString)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
        this.creationDateTime = LocalDateTime.parse(dateTimeString, formatter);
    }

    public void setCreationDateTime(LocalDateTime time)
    {
        this.creationDateTime = time;
    }

    public void setPickerName(String name)
    {
        this.pickerName = name;
    }

    public void setPickerIndex(int i)
    {
        this.pickerIndex = i;
    }

    public void setPickerChoice(FlipOptions choice)
    {
        this.pickerChoice = choice;
    }

    public void setResult(FlipOptions result)
    {
        this.result = result;
    }

}

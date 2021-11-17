package com.example.parentapp.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 * FlipCoinGame class represents a flip of a coin, it consists of attributes like the date/time of the flip,
 * the name of the child who gets to pick and his/her choice of heads or tails, and the result of the flip.
 * This class also consists of an inner Enum class FlipOptions that declares HEAD-TAIL.
 */
public class FlipCoinGame {
    public enum FlipOptions {HEAD, TAIL}
    private String creationDateTimeString;
    private String pickerName;
    private FlipOptions pickerChoice;
    private FlipOptions result;

    public FlipCoinGame()
    {
        creationDateTimeString = "";
        pickerName = "Nobody";
        pickerChoice = FlipOptions.HEAD;
        result = null;
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

    public FlipOptions getResult()
    {
        return result;
    }

    public void setPickerName(String name)
    {
        this.pickerName = name;
    }

    public void setPickerChoice(FlipOptions choice)
    {
        this.pickerChoice = choice;
    }

    public void setResult(FlipOptions result)
    {
        this.result = result;
    }

    public void setLocalDateTime(){
        LocalDateTime creationDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
        this.creationDateTimeString = creationDateTime.format(formatter);
    }

}

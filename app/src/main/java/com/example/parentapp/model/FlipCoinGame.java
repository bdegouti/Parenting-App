package com.example.parentapp.model;

import java.time.LocalDateTime;

public class FlipCoinGame {
    public enum FlipOptions {HEAD, TAIL}
    private final LocalDateTime creationDateTime;
    private String pickerName;
    private int pickerIndex;
    private FlipOptions pickerChoice;
    private FlipOptions result;

    public FlipCoinGame()
    {
        creationDateTime = LocalDateTime.now();
        pickerName = null;
        pickerIndex = -1;
        pickerChoice = FlipOptions.HEAD;
        result = null;
    }

    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
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

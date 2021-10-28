package com.example.parentapp.model;

import java.time.LocalDateTime;

public class FlipCoinGame {
    public enum FlipResult {HEAD, TAIL}; //enum is always static
    private LocalDateTime creationDateTime;
    private String pickerName;
    private FlipResult result;

    public FlipCoinGame()
    {
        creationDateTime = LocalDateTime.now();
        pickerName = null;
        result = null;
    }

    public FlipCoinGame(String picker)
    {
        this.creationDateTime = LocalDateTime.now();
        this.pickerName = picker;
        result = null;
    }

    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    public String getPickerName() {
        return pickerName;
    }

    public FlipResult getResult() {
        return result;
    }

    public void setPickerName(String name)
    {
        this.pickerName = name;
    }

    public void setResult(FlipResult result)
    {
        this.result = result;
    }

}

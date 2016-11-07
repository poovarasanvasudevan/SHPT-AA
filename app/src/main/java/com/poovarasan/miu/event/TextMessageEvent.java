package com.poovarasan.miu.event;

/**
 * Created by poovarasanv on 7/11/16.
 */

public class TextMessageEvent {
    String message;
    String number;
    long time;

    public TextMessageEvent(String message, String number, long time) {
        this.message = message;
        this.number = number;
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}

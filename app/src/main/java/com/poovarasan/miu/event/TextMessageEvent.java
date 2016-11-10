package com.poovarasan.miu.event;

/**
 * Created by poovarasanv on 7/11/16.
 */

public class TextMessageEvent {
    String message;
    String number;
    long time;
    String tag;


    public TextMessageEvent(String message, String number, long time, String tag) {
        this.message = message;
        this.number = number;
        this.time = time;
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
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

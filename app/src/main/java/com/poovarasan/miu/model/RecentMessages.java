package com.poovarasan.miu.model;

import java.util.Date;

import fr.xebia.android.freezer.annotations.Id;
import fr.xebia.android.freezer.annotations.Model;

/**
 * Created by poovarasanv on 11/11/16.
 */

@Model
public class RecentMessages {

    @Id
    long id;

    String numbers;
    Date updatedTime;
    String recentMessage;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNumbers() {
        return numbers;
    }

    public void setNumbers(String numbers) {
        this.numbers = numbers;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getRecentMessage() {
        return recentMessage;
    }

    public void setRecentMessage(String recentMessage) {
        this.recentMessage = recentMessage;
    }
}

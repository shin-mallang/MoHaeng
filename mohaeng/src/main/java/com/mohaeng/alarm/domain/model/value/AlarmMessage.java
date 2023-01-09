package com.mohaeng.alarm.domain.model.value;

import jakarta.persistence.Embeddable;

@Embeddable
public class AlarmMessage {

    private String title;
    private String content;

    public AlarmMessage() {
    }

    public AlarmMessage(final String title, final String content) {
        this.title = title;
        this.content = content;
    }

    public String title() {
        return title;
    }

    public String content() {
        return content;
    }
}

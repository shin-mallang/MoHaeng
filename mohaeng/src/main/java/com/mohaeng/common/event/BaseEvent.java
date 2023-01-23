package com.mohaeng.common.event;

import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

public abstract class BaseEvent extends ApplicationEvent {

    protected final LocalDateTime eventDateTime;

    public BaseEvent(final Object source) {
        super(source);
        this.eventDateTime = LocalDateTime.now();
    }

    public abstract BaseEventHistory history();

    public LocalDateTime eventDateTime() {
        return eventDateTime;
    }
}

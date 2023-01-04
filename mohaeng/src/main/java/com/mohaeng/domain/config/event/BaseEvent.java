package com.mohaeng.domain.config.event;

import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

public abstract class BaseEvent extends ApplicationEvent {

    protected LocalDateTime eventDateTime;

    public BaseEvent(final Object source) {
        super(source);
        this.eventDateTime = LocalDateTime.now();
    }

    protected abstract BaseEventHistory history();

    public LocalDateTime eventDateTime() {
        return eventDateTime;
    }
}

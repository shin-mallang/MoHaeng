package com.mohaeng.common.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;

public class Events {

    private static ApplicationEventPublisher applicationEventPublisher;

    private Events() {
    }

    protected static void setApplicationEventPublisher(final ApplicationEventPublisher applicationEventPublisher) {
        Events.applicationEventPublisher = applicationEventPublisher;
    }

    public static void raise(final ApplicationEvent event) {
        if (applicationEventPublisher != null) {
            applicationEventPublisher.publishEvent(event);
        }
    }
}

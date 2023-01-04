package com.mohaeng.common.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;

public class Event {

    private static ApplicationEventPublisher applicationEventPublisher;

    private Event() {
    }

    static void setApplicationEventPublisher(final ApplicationEventPublisher applicationEventPublisher) {
        Event.applicationEventPublisher = applicationEventPublisher;
    }

    public static void publish(final ApplicationEvent event) {
        if (applicationEventPublisher != null) {
            applicationEventPublisher.publishEvent(event);
        }
    }
}

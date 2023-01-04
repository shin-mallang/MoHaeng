package com.mohaeng.common.event;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
public class EventConfig {

    private final ApplicationContext applicationContext;

    public EventConfig(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void intiEvent() {
        Event.setApplicationEventPublisher(applicationContext);
    }
}

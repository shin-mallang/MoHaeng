package com.mohaeng.notification.domain.model;

import com.mohaeng.common.notification.NotificationEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toUnmodifiableMap;

@Component
public class NotificationMakeStrategies {

    private final Map<Class<? extends NotificationEvent>, NotificationMakeStrategy<? extends NotificationEvent>> map;

    public NotificationMakeStrategies(final Set<NotificationMakeStrategy<? extends NotificationEvent>> notificationMakeStrategies) {
        map = notificationMakeStrategies.stream()
                .collect(toUnmodifiableMap(NotificationMakeStrategy::supportEvent, it -> it));
    }

    public List<Notification> make(final NotificationEvent event) {
        return map.get(event.getClass()).make(event);
    }
}
package com.mohaeng.notification.application.eventhandler;

import com.mohaeng.common.notification.NotificationEvent;
import com.mohaeng.notification.domain.model.Notification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class NotificationMakeStrategies {

    private final Set<NotificationMakeStrategy> notificationMakeStrategies;

    public NotificationMakeStrategies(final Set<NotificationMakeStrategy> notificationMakeStrategies) {
        this.notificationMakeStrategies = notificationMakeStrategies;
    }

    public List<Notification> make(final NotificationEvent event) {
        return notificationMakeStrategies
                .stream()
                .filter(it -> it.support(event))  // 처리할 수 있는 경우
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("이벤트에 대응되는 알림을 생성할 수 없습니다."))
                .make(event);  // 처리
    }
}

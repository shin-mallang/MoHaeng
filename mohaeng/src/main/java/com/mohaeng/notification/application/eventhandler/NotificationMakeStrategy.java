package com.mohaeng.notification.application.eventhandler;

import com.mohaeng.common.notification.NotificationEvent;
import com.mohaeng.notification.domain.model.Notification;

import java.util.List;

public abstract class NotificationMakeStrategy {

    /**
     * 처리할 수 있는 이벤트 클래스
     * <p>
     * EX: return (notificationEvent) instanceOf (**Event);
     */
    public abstract Class<? extends NotificationEvent> supportEvent();

    public abstract List<Notification> make(final NotificationEvent notificationEvent);
}

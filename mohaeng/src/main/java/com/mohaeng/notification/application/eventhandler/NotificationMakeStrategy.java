package com.mohaeng.notification.application.eventhandler;

import com.mohaeng.common.notification.NotificationEvent;
import com.mohaeng.notification.domain.model.Notification;

import java.util.List;

public abstract class NotificationMakeStrategy {

    /**
     * 처리할 수 있는지 여부
     *
     * EX: return (notificationEvent) instanceOf (**Event);
     */
    public abstract boolean support(final NotificationEvent notificationEvent);

    public abstract List<Notification> make(final NotificationEvent notificationEvent);
}

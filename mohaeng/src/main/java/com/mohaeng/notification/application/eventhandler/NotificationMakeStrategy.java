package com.mohaeng.notification.application.eventhandler;

import com.mohaeng.common.notification.NotificationEvent;
import com.mohaeng.notification.domain.model.Notification;

import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class NotificationMakeStrategy<T extends NotificationEvent> {

    /**
     * 처리할 수 있는 이벤트 클래스
     * - Overriding 하지 말 것
     * <p>
     * [EX]
     * class Sample extends NotificationMakeStrategy<SampleEvent> {} 인 경우
     * -> Class<SampleEvent> 반환
     */
    @SuppressWarnings("unchecked")
    public final Class<T> supportEvent() {
        return (Class<T>) (((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
    }

    public final List<Notification> make(final NotificationEvent notificationEvent) {
        T event = (T) notificationEvent;
        return makeNotifications(event);
    }

    protected abstract List<Notification> makeNotifications(T event);
}

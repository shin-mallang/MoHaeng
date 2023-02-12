package com.mohaeng.notification.domain.model;

import com.mohaeng.common.notification.NotificationEvent;

import java.lang.reflect.ParameterizedType;
import java.util.List;

public abstract class NotificationMakeStrategy<T extends NotificationEvent> {

    /**
     * 처리할 수 있는 이벤트 클래스
     * <p>
     * [EX]
     * class Sample extends NotificationMakeStrategy<SampleEvent> {} 인 경우
     * -> Class<SampleEvent> 반환
     */
    @SuppressWarnings("unchecked")
    public final Class<T> supportEvent() {
        return (Class<T>) (((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
    }

    @SuppressWarnings("unchecked")
    public final List<Notification> make(final NotificationEvent notificationEvent) {
        T event = (T) notificationEvent;
        return makeNotifications(event);
    }

    protected abstract List<Notification> makeNotifications(T event);
}
package com.mohaeng.common.notification;

import com.mohaeng.common.event.BaseEvent;

import java.util.List;

/**
 * 알람 발행을 위한 이벤트는 BaseEvent 대신 해당 클래스를 상속받는다.
 */
public abstract class NotificationEvent extends BaseEvent {

    protected final List<Long> receiverIds;

    public NotificationEvent(final Object source, final List<Long> receiverIds) {
        super(source);
        this.receiverIds = receiverIds;
    }

    public NotificationEvent(final Object source, final Long receiverId) {
        this(source, List.of(receiverId));
    }

    public List<Long> receiverIds() {
        return receiverIds;
    }

    /**
     * 로깅을 위한 toString
     */
    @Override
    public abstract String toString();
}

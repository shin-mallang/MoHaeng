package com.mohaeng.notification.mock;

import com.mohaeng.common.event.BaseEventHistory;
import com.mohaeng.common.notification.NotificationEvent;

import java.util.List;

public class MockNotificationEvent extends NotificationEvent {

    public MockNotificationEvent(final Object source, final List<Long> receiverIds) {
        super(source, receiverIds);
    }

    @Override
    public BaseEventHistory history() {
        return new MockEventHistory();
    }

    @Override
    public String toString() {
        return null;
    }
}
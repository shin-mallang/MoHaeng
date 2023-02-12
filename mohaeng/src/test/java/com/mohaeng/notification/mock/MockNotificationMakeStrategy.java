package com.mohaeng.notification.mock;

import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.model.NotificationMakeStrategy;
import com.mohaeng.notification.domain.model.Receiver;

import java.util.List;

public class MockNotificationMakeStrategy extends NotificationMakeStrategy<MockNotificationEvent> {

    @Override
    protected List<Notification> makeNotifications(final MockNotificationEvent event) {
        return event.receiverIds().stream()
                .map(it -> (Notification) new MockNotification(Receiver.of(it)))
                .toList();
    }
}

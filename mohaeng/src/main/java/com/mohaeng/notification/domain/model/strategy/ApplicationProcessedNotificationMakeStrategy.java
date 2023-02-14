package com.mohaeng.notification.domain.model.strategy;

import com.mohaeng.club.applicationform.domain.event.ApplicationProcessedEvent;
import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.model.NotificationMakeStrategy;
import com.mohaeng.notification.domain.model.Receiver;
import com.mohaeng.notification.domain.model.type.ApplicationProcessedNotification;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApplicationProcessedNotificationMakeStrategy extends NotificationMakeStrategy<ApplicationProcessedEvent> {

    @Override
    public List<Notification> makeNotifications(final ApplicationProcessedEvent event) {
        return List.of(new ApplicationProcessedNotification(Receiver.of(event.receiverId()), event.clubId(), event.isApproved()));
    }
}
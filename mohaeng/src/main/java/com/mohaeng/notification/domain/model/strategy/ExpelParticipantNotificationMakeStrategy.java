package com.mohaeng.notification.domain.model.strategy;

import com.mohaeng.club.club.domain.event.ExpelParticipantEvent;
import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.model.NotificationMakeStrategy;
import com.mohaeng.notification.domain.model.Receiver;
import com.mohaeng.notification.domain.model.type.ExpelParticipantNotification;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExpelParticipantNotificationMakeStrategy extends NotificationMakeStrategy<ExpelParticipantEvent> {

    @Override
    protected List<Notification> makeNotifications(final ExpelParticipantEvent event) {
        return List.of(new ExpelParticipantNotification(Receiver.of(event.receiverId()), event.clubId()));
    }
}
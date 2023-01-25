package com.mohaeng.notification.domain.model.strategy;

import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.model.NotificationMakeStrategy;
import com.mohaeng.notification.domain.model.kind.ExpelParticipantNotification;
import com.mohaeng.notification.domain.model.value.Receiver;
import com.mohaeng.participant.domain.event.ExpelParticipantEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExpelParticipantNotificationMakeStrategy extends NotificationMakeStrategy<ExpelParticipantEvent> {

    @Override
    protected List<Notification> makeNotifications(final ExpelParticipantEvent event) {
        return List.of(new ExpelParticipantNotification(Receiver.of(event.receiverId()), event.clubId()));
    }
}

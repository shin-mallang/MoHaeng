package com.mohaeng.notification.domain.model.strategy;

import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.model.NotificationMakeStrategy;
import com.mohaeng.notification.domain.model.kind.DeleteParticipantBecauseClubIsDeletedNotification;
import com.mohaeng.notification.domain.model.value.Receiver;
import com.mohaeng.participant.domain.event.DeleteParticipantEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DeleteParticipantBecauseClubIsDeletedNotificationMakeStrategy extends NotificationMakeStrategy<DeleteParticipantEvent> {

    @Override
    protected List<Notification> makeNotifications(final DeleteParticipantEvent event) {
        return event.receiverIds().stream()
                .map(it -> (Notification) new DeleteParticipantBecauseClubIsDeletedNotification(Receiver.of(it), event.clubName(), event.clubDescription()))
                .toList();
    }
}
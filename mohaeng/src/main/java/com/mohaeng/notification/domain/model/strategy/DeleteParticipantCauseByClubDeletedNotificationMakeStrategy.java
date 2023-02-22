package com.mohaeng.notification.domain.model.strategy;

import com.mohaeng.club.club.domain.event.DeleteClubEvent;
import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.model.NotificationMakeStrategy;
import com.mohaeng.notification.domain.model.Receiver;
import com.mohaeng.notification.domain.model.type.DeleteParticipantCauseByClubDeletedNotification;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DeleteParticipantCauseByClubDeletedNotificationMakeStrategy extends NotificationMakeStrategy<DeleteClubEvent> {

    @Override
    protected List<Notification> makeNotifications(final DeleteClubEvent event) {
        return event.receiverIds().stream()
                .map(it -> (Notification) new DeleteParticipantCauseByClubDeletedNotification(Receiver.of(it), event.clubName(), event.clubDescription()))
                .toList();
    }
}
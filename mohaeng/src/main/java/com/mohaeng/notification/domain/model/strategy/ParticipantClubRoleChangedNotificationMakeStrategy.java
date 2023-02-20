package com.mohaeng.notification.domain.model.strategy;

import com.mohaeng.club.club.domain.event.ParticipantClubRoleChangedEvent;
import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.model.NotificationMakeStrategy;
import com.mohaeng.notification.domain.model.Receiver;
import com.mohaeng.notification.domain.model.type.ParticipantClubRoleChangedNotification;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ParticipantClubRoleChangedNotificationMakeStrategy extends NotificationMakeStrategy<ParticipantClubRoleChangedEvent> {

    @Override
    public List<Notification> makeNotifications(final ParticipantClubRoleChangedEvent event) {
        return List.of(new ParticipantClubRoleChangedNotification(
                Receiver.of(event.receiverId()),
                event.clubId(),
                event.clubRoleId(),
                event.clubRoleName(),
                event.clubRoleCategory()));
    }
}
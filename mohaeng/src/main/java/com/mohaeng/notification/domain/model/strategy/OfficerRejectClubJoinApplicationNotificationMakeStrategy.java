package com.mohaeng.notification.domain.model.strategy;

import com.mohaeng.club.applicationform.domain.event.OfficerRejectApplicationEvent;
import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.model.NotificationMakeStrategy;
import com.mohaeng.notification.domain.model.Receiver;
import com.mohaeng.notification.domain.model.type.OfficerRejectApplicationNotification;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OfficerRejectClubJoinApplicationNotificationMakeStrategy extends NotificationMakeStrategy<OfficerRejectApplicationEvent> {

    @Override
    public List<Notification> makeNotifications(final OfficerRejectApplicationEvent event) {
        return List.of(
                new OfficerRejectApplicationNotification(
                        Receiver.of(event.receiverId()),
                        event.officerMemberId(),
                        event.officerParticipantId(),
                        event.applicantMemberId()));
    }
}
package com.mohaeng.notification.domain.model.strategy;

import com.mohaeng.club.applicationform.domain.event.OfficerApproveApplicationEvent;
import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.model.NotificationMakeStrategy;
import com.mohaeng.notification.domain.model.Receiver;
import com.mohaeng.notification.domain.model.type.OfficerApproveApplicationNotification;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OfficerApproveClubJoinApplicationNotificationMakeStrategy extends NotificationMakeStrategy<OfficerApproveApplicationEvent> {

    @Override
    public List<Notification> makeNotifications(final OfficerApproveApplicationEvent event) {
        return List.of(
                new OfficerApproveApplicationNotification(
                        Receiver.of(event.receiverId()),
                        event.officerMemberId(),
                        event.officerParticipantId(),
                        event.applicantMemberId(),
                        event.applicantParticipantId()));
    }
}
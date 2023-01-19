package com.mohaeng.notification.application.eventhandler.strategy;

import com.mohaeng.applicationform.domain.event.OfficerApproveClubJoinApplicationEvent;
import com.mohaeng.common.notification.NotificationEvent;
import com.mohaeng.notification.application.eventhandler.NotificationMakeStrategy;
import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.model.kind.OfficerApproveApplicationNotification;
import com.mohaeng.notification.domain.model.value.Receiver;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 회장 대신 임원진이 가입 신청을 수락한 경우 회장에게 알리기 위함
 */
@Component
public class OfficerApproveClubJoinApplicationNotificationMakeStrategy extends NotificationMakeStrategy {

    @Override
    public boolean support(final NotificationEvent notificationEvent) {
        return notificationEvent instanceof OfficerApproveClubJoinApplicationEvent;
    }

    @Override
    public List<Notification> make(final NotificationEvent notificationEvent) {
        OfficerApproveClubJoinApplicationEvent event = (OfficerApproveClubJoinApplicationEvent) notificationEvent;

        return List.of(
                new OfficerApproveApplicationNotification(
                        Receiver.of(event.receiverId()),
                        event.officerMemberId(),
                        event.officerParticipantId(),
                        event.applicantMemberId(),
                        event.applicantParticipantId()));
    }
}

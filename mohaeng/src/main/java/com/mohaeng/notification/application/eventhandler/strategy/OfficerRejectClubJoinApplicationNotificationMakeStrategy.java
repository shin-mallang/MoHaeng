package com.mohaeng.notification.application.eventhandler.strategy;

import com.mohaeng.applicationform.domain.event.OfficerRejectClubJoinApplicationEvent;
import com.mohaeng.notification.application.eventhandler.NotificationMakeStrategy;
import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.model.kind.OfficerRejectApplicationNotification;
import com.mohaeng.notification.domain.model.value.Receiver;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 회장 대신 임원진이 가입 신청을 거절락한 경우 회장에게 알리기 위함
 */
@Component
public class OfficerRejectClubJoinApplicationNotificationMakeStrategy extends NotificationMakeStrategy<OfficerRejectClubJoinApplicationEvent> {

    @Override
    public List<Notification> makeNotifications(OfficerRejectClubJoinApplicationEvent event) {
        return List.of(
                new OfficerRejectApplicationNotification(
                        Receiver.of(event.receiverId()),
                        event.officerMemberId(),
                        event.officerParticipantId(),
                        event.applicantMemberId()));
    }
}

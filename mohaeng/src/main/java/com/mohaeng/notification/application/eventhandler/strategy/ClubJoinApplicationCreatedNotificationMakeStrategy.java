package com.mohaeng.notification.application.eventhandler.strategy;

import com.mohaeng.applicationform.domain.event.ClubJoinApplicationCreatedEvent;
import com.mohaeng.common.notification.NotificationEvent;
import com.mohaeng.notification.application.eventhandler.NotificationMakeStrategy;
import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.model.kind.ClubJoinApplicationCreatedNotification;
import com.mohaeng.notification.domain.model.value.Receiver;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 모임 가입 신청 생성 시 회장&임원진에게 알리기 위함
 */
@Component
public class ClubJoinApplicationCreatedNotificationMakeStrategy extends NotificationMakeStrategy {

    @Override
    public Class<? extends NotificationEvent> supportEvent() {
        return ClubJoinApplicationCreatedEvent.class;
    }

    @Override
    public List<Notification> make(final NotificationEvent notificationEvent) {
        ClubJoinApplicationCreatedEvent event = (ClubJoinApplicationCreatedEvent) notificationEvent;

        return event.receiverIds().stream()
                .map(id -> (Notification) new ClubJoinApplicationCreatedNotification(Receiver.of(id), event.clubId(), event.applicantId(), event.applicationFormId()))
                .toList();
    }
}
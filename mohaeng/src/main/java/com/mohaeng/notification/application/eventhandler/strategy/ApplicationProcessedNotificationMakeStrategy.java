package com.mohaeng.notification.application.eventhandler.strategy;

import com.mohaeng.applicationform.domain.event.ApplicationProcessedEvent;
import com.mohaeng.common.notification.NotificationEvent;
import com.mohaeng.notification.application.eventhandler.NotificationMakeStrategy;
import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.model.kind.ApplicationProcessedNotification;
import com.mohaeng.notification.domain.model.value.Receiver;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 모임 가입 신청 수락/거절 시 신청자에게 수락/거절되었다는 알림을 보내기 위함
 */
@Component
public class ApplicationProcessedNotificationMakeStrategy extends NotificationMakeStrategy {

    @Override
    public Class<? extends NotificationEvent> supportEvent() {
        return ApplicationProcessedEvent.class;
    }

    @Override
    public List<Notification> make(final NotificationEvent notificationEvent) {
        ApplicationProcessedEvent event = (ApplicationProcessedEvent) notificationEvent;

        return List.of(new ApplicationProcessedNotification(Receiver.of(event.receiverId()), event.clubId(), event.isApproved()));
    }
}

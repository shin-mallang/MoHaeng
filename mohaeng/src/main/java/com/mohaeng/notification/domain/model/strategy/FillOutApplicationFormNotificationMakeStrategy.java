package com.mohaeng.notification.domain.model.strategy;

import com.mohaeng.club.applicationform.domain.event.FillOutApplicationFormEvent;
import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.model.NotificationMakeStrategy;
import com.mohaeng.notification.domain.model.Receiver;
import com.mohaeng.notification.domain.model.type.FillOutApplicationFormNotification;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FillOutApplicationFormNotificationMakeStrategy extends NotificationMakeStrategy<FillOutApplicationFormEvent> {

    @Override
    public List<Notification> makeNotifications(final FillOutApplicationFormEvent event) {
        return event.receiverIds().stream()
                .map(id -> (Notification) new FillOutApplicationFormNotification(Receiver.of(id), event.clubId(), event.applicantId(), event.applicationFormId()))
                .toList();
    }
}
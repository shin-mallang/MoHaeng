package com.mohaeng.notification.domain.model.strategy;

import com.mohaeng.club.applicationform.domain.event.DeleteApplicationFormEvent;
import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.model.NotificationMakeStrategy;
import com.mohaeng.notification.domain.model.Receiver;
import com.mohaeng.notification.domain.model.type.DeleteApplicationFormCauseByClubDeletedNotification;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DeleteApplicationFormCauseByClubDeletedNotificationMakeStrategy extends NotificationMakeStrategy<DeleteApplicationFormEvent> {

    @Override
    protected List<Notification> makeNotifications(final DeleteApplicationFormEvent event) {
        return event.receiverIds().stream()
                .map(it -> (Notification) new DeleteApplicationFormCauseByClubDeletedNotification(Receiver.of(it), event.clubName(), event.clubDescription()))
                .toList();
    }
}
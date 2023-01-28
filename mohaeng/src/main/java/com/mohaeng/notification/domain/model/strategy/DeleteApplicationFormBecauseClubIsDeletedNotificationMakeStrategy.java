package com.mohaeng.notification.domain.model.strategy;

import com.mohaeng.applicationform.domain.event.DeleteApplicationFormEvent;
import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.model.NotificationMakeStrategy;
import com.mohaeng.notification.domain.model.kind.DeleteApplicationFormBecauseClubIsDeletedNotification;
import com.mohaeng.notification.domain.model.value.Receiver;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DeleteApplicationFormBecauseClubIsDeletedNotificationMakeStrategy extends NotificationMakeStrategy<DeleteApplicationFormEvent> {

    @Override
    protected List<Notification> makeNotifications(final DeleteApplicationFormEvent event) {
        return event.receiverIds().stream()
                .map(it -> (Notification) new DeleteApplicationFormBecauseClubIsDeletedNotification(Receiver.of(it), event.clubName(), event.clubDescription()))
                .toList();
    }
}

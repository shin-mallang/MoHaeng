package com.mohaeng.notification.mock;

import com.mohaeng.notification.application.dto.NotificationDto;
import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.model.Receiver;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@DiscriminatorValue(value = "MockNotification")
@Entity
public class MockNotification extends Notification {

    protected MockNotification() {
    }

    public MockNotification(final Receiver receiver) {
        super(receiver);
    }

    @Override
    public NotificationDto toDto() {
        return null;
    }
}
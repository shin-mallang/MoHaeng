package com.mohaeng.notification.domain.model.type;

import com.mohaeng.notification.application.dto.NotificationDto;
import com.mohaeng.notification.application.dto.type.ExpelParticipantNotificationDto;
import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.model.Receiver;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * 모임에서 추방당했다는 알림
 */
@DiscriminatorValue(value = "ExpelParticipantNotification")
@Entity
public class ExpelParticipantNotification extends Notification {

    private Long clubId;  // 추방된 모임의 id

    protected ExpelParticipantNotification() {
        this.clubId = null;
    }

    public ExpelParticipantNotification(final Receiver receiver,
                                        final Long clubId) {
        super(receiver);
        this.clubId = clubId;
    }

    public Long clubId() {
        return clubId;
    }

    @Override
    public NotificationDto toDto() {
        return new ExpelParticipantNotificationDto(id(), createdAt(), isRead(), getClass().getSimpleName(), clubId());
    }
}
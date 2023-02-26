package com.mohaeng.notification.domain.model.type;

import com.mohaeng.notification.application.dto.NotificationDto;
import com.mohaeng.notification.application.dto.type.ApplicationProcessedNotificationDto;
import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.model.Receiver;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * 가입 신청자에게 신청이 수락되었다는 알림
 */
@Entity
@DiscriminatorValue(value = "ApplicationProcessedNotification")
public class ApplicationProcessedNotification extends Notification {

    private Long clubId;  // 가입을 요청한 모임 ID

    private boolean isApproved;

    protected ApplicationProcessedNotification() {
    }

    public ApplicationProcessedNotification(final Receiver receiver,
                                            final Long clubId,
                                            final boolean isApproved) {
        super(receiver);
        this.clubId = clubId;
        this.isApproved = isApproved;
    }

    public Long clubId() {
        return clubId;
    }

    public boolean isApproved() {
        return isApproved;
    }

    @Override
    public NotificationDto toDto() {
        return new ApplicationProcessedNotificationDto(
                id(),
                createdAt(),
                isRead(),
                getClass().getSimpleName(),
                clubId(),
                isApproved()
        );
    }
}
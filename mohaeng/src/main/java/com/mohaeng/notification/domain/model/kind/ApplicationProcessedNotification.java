package com.mohaeng.notification.domain.model.kind;

import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.model.value.Receiver;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * 가입 요청이 수락/거절 된 경우 발생하는 알림
 */
@DiscriminatorValue(value = "application_processed_notification")
@Entity
public class ApplicationProcessedNotification extends Notification {

    @Column(nullable = false)
    private Long clubId;  // 가입을 요청한 모임 ID

    private boolean isApproved;  // 수락된 경우 true, 거절된 경우 false

    protected ApplicationProcessedNotification() {
    }

    public ApplicationProcessedNotification(final Receiver receiver, final Long clubId, final boolean isApproved) {
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
}

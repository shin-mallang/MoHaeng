package com.mohaeng.notification.domain.model;

import com.mohaeng.common.domain.BaseEntity;
import com.mohaeng.notification.application.dto.NotificationDto;
import jakarta.persistence.*;

/**
 * 알림이 필요하면 해당 클래스를 상속받는다.
 */
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "notification_type", length = 60)
@Entity
@Table(name = "notification")
public abstract class Notification extends BaseEntity {

    @Embedded
    private Receiver receiver;  // 수신자

    private boolean isRead;  // 알람 읽음 여부

    protected Notification() {
    }

    protected Notification(final Receiver receiver) {
        this.receiver = receiver;
        this.isRead = false;
    }

    public Receiver receiver() {
        return receiver;
    }

    public boolean isRead() {
        return isRead;
    }

    public void read() {
        this.isRead = true;
    }

    public abstract NotificationDto toDto();
}
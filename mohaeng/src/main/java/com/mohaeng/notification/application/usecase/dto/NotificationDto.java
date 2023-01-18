package com.mohaeng.notification.application.usecase.dto;

import java.time.LocalDateTime;

public abstract class NotificationDto {

    private Long id;
    private LocalDateTime createdAt;  // 알람 발송일
    private boolean isRead;  // 알람 읽음 여부
    private String type;  // getClass().getSimpleName();

    public NotificationDto(final Long id,
                           final LocalDateTime createdAt,
                           final boolean isRead,
                           final String type) {
        this.id = id;
        this.createdAt = createdAt;
        this.isRead = isRead;
        this.type = type;
    }

    public Long id() {
        return id;
    }

    public LocalDateTime createdAt() {
        return createdAt;
    }

    public boolean isRead() {
        return isRead;
    }

    public String type() {
        return type;
    }
}

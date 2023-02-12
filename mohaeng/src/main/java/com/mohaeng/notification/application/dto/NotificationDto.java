package com.mohaeng.notification.application.dto;

import com.mohaeng.notification.presentation.response.NotificationResponse;

import java.time.LocalDateTime;

public abstract class NotificationDto {

    private final Long id;
    private final LocalDateTime createdAt;  // 알람 발송일
    private final boolean isRead;  // 알람 읽음 여부
    private final String type;  // getClass().getSimpleName();

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

    public abstract NotificationResponse toResponse();
}
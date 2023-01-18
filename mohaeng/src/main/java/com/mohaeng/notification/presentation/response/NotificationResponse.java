package com.mohaeng.notification.presentation.response;

import java.time.LocalDateTime;

public abstract class NotificationResponse {

    private Long id;
    private LocalDateTime createdAt;  // 알람 발송일
    private boolean isRead;  // 알람 읽음 여부
    private String type;

    public NotificationResponse(final Long id,
                                final LocalDateTime createdAt,
                                final boolean isRead,
                                final String type) {
        this.id = id;
        this.createdAt = createdAt;
        this.isRead = isRead;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isRead() {
        return isRead;
    }

    public String getType() {
        return type;
    }
}
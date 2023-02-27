package com.mohaeng.notification.presentation.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * Getter는 getXXX의 형태 사용해야 함
 */
public abstract class NotificationResponse {

    private final Long id;
    private final LocalDateTime createdAt;  // 알람 발송일
    private final boolean isRead;  // 알람 읽음 여부
    private final String type;

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

    @JsonProperty(value = "isRead")
    public boolean isRead() {
        return isRead;
    }

    public String getType() {
        return type;
    }
}
package com.mohaeng.notification.presentation.response;

import java.time.LocalDateTime;

public class NotificationResponseTestImpl extends NotificationResponse {

    public NotificationResponseTestImpl(final Long id, final LocalDateTime createdAt, final boolean isRead, final String type) {
        super(id, createdAt, isRead, type);
    }
}
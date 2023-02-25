package com.mohaeng.notification.domain.repository;

import com.mohaeng.notification.domain.model.Notification;

import java.util.Optional;

public interface NotificationQueryRepository {

    Optional<Notification> findById(final Long id);
}

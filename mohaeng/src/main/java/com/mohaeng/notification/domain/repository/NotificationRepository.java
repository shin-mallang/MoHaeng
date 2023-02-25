package com.mohaeng.notification.domain.repository;

import com.mohaeng.notification.domain.model.Notification;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository {

    Notification save(final Notification notification);

    List<Notification> saveAll(final List<Notification> notifications);

    Optional<Notification> findById(final Long id);

    Optional<Notification> findByIdAndReceiverId(final Long id, final Long receiverId);

    List<Notification> findAll();

    List<Notification> findApplicationProcessedNotificationByApplicationFormId(final Long applicationFormId);

    void deleteAllInBatch(final List<Notification> notifications);
}

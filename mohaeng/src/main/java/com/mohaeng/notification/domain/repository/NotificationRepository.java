package com.mohaeng.notification.domain.repository;

import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.model.value.Receiver;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository {

    Notification save(final Notification notification);

    Optional<Notification> findById(final Long id);

    Optional<Notification> findByIdAndReceiver(final Long id, final Receiver receiver);

    List<Notification> findByReceiver(final Receiver receiver);

    List<Notification> findAll();

    List<Notification> saveAll(final List<Notification> notifications);
}

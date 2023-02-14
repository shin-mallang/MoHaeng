package com.mohaeng.notification.infrastructure.persistence.repository;

import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.repository.NotificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JpaNotificationRepository extends JpaRepository<Notification, Long>, NotificationRepository {

    @Override
    default List<Notification> saveAll(final List<Notification> notifications) {
        return saveAll((Iterable<Notification>) notifications);
    }

    @Override
    default void deleteAllInBatch(final List<Notification> notifications) {
        deleteAllInBatch((Iterable<Notification>) notifications);
    }

    @Override
    @Query("select n from FillOutApplicationFormNotification n where n.applicationFormId = :applicationFormId")
    List<Notification> findApplicationProcessedNotificationByApplicationFormId(@Param("applicationFormId") final Long applicationFormId);
}
package com.mohaeng.notification.infrastructure.persistence.repository;

import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.repository.NotificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JpaNotificationRepository extends JpaRepository<Notification, Long>, NotificationRepository {

    @Override
    @Query("select n from Notification n where n.id = :id and n.receiver.receiverId = :receiverId")
    Optional<Notification> findByIdAndReceiverId(@Param("id") final Long id,
                                                 @Param("receiverId") final Long receiverId);

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
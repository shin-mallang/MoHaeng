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
    default List<Notification> saveAll(final List<Notification> notifications) {
        return saveAll((Iterable<Notification>) notifications);
    }

    @Override
    @Query("select a from Notification a where a.id = :alarmId and a.receiver.receiver.id = :receiverId")
    Optional<Notification> findByIdAndReceiverId(@Param("alarmId") final Long alarmId, @Param("receiverId") final Long receiverId);
}

package com.mohaeng.notification.infrastructure.persistence.repository;

import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.repository.NotificationQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.mohaeng.notification.domain.model.QNotification.notification;
import static java.util.Optional.ofNullable;

@Repository
public class JpaNotificationQueryRepository implements NotificationQueryRepository {

    private final JPAQueryFactory query;

    public JpaNotificationQueryRepository(final JPAQueryFactory query) {
        this.query = query;
    }

    @Override
    public Optional<Notification> findById(final Long id) {
        return ofNullable(query.selectFrom(notification)
                .where(notification.id.eq(id))
                .fetchOne());
    }
}

package com.mohaeng.notification.infrastructure.persistence.repository;

import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.model.type.FillOutApplicationFormNotification;
import com.mohaeng.notification.domain.repository.NotificationQueryRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.mohaeng.notification.domain.model.QNotification.notification;

@Repository
public class JpaNotificationQueryRepository implements NotificationQueryRepository {

    private final JPAQueryFactory query;

    public JpaNotificationQueryRepository(final JPAQueryFactory query) {
        this.query = query;
    }

    @Override
    public Page<Notification> findAllByFilter(final NotificationFilter filter, final Pageable pageable) {
        List<Notification> contents = query.selectFrom(notification)
                .where(
                        receiverIdEq(filter.memberId()),
                        readFilter(filter.readFilter())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = query.select(notification.count())
                .from(notification)
                .where(
                        receiverIdEq(filter.memberId()),
                        readFilter(filter.readFilter())
                );

        return PageableExecutionUtils.getPage(contents, pageable, countQuery::fetchOne);
    }

    private BooleanExpression readFilter(final NotificationFilter.ReadFilter readFilter) {
        return switch (readFilter) {
            case ALL -> null;
            case ONLY_READ -> notification.isRead.isTrue();
            case ONLY_UNREAD -> notification.isRead.isFalse();
        };
    }

    @Override
    public Page<FillOutApplicationFormNotification> findAllFillOutApplicationFormNotificationByReceiverId(final Long memberId, final Pageable pageable) {
        List<FillOutApplicationFormNotification> contents = query.selectFrom(notification)
                .where(
                        receiverIdEq(memberId),
                        notification.instanceOf(FillOutApplicationFormNotification.class)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch()
                .stream()
                .map(it -> (FillOutApplicationFormNotification) it)
                .toList();

        JPAQuery<Long> countQuery = query.select(notification.count())
                .from(notification)
                .where(
                        receiverIdEq(memberId),
                        notification.instanceOf(FillOutApplicationFormNotification.class)
                );

        return PageableExecutionUtils.getPage(contents, pageable, countQuery::fetchOne);
    }

    private BooleanExpression receiverIdEq(final Long memberId) {
        return notification.receiver.receiverId.eq(memberId);
    }
}

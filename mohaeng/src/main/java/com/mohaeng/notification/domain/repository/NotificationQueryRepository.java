package com.mohaeng.notification.domain.repository;

import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.model.type.FillOutApplicationFormNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationQueryRepository {

    Page<Notification> findAllByFilter(final NotificationFilter filter, final Pageable pageable);

    Page<FillOutApplicationFormNotification> findAllFillOutApplicationFormNotificationByReceiverId(final Long memberId, final Pageable pageable);

    record NotificationFilter(
            Long memberId,
            ReadFilter readFilter
    ) {
        public enum ReadFilter {
            ALL,
            ONLY_READ,
            ONLY_UNREAD
        }
    }
}

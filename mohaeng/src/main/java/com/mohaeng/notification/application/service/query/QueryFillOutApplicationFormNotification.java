package com.mohaeng.notification.application.service.query;

import com.mohaeng.notification.application.dto.type.FillOutApplicationFormNotificationDto;
import com.mohaeng.notification.application.usecase.query.QueryFillOutApplicationFormNotificationUseCase;
import com.mohaeng.notification.domain.model.type.FillOutApplicationFormNotification;
import com.mohaeng.notification.domain.repository.NotificationQueryRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class QueryFillOutApplicationFormNotification implements QueryFillOutApplicationFormNotificationUseCase {

    private final NotificationQueryRepository notificationQueryRepository;

    public QueryFillOutApplicationFormNotification(final NotificationQueryRepository notificationQueryRepository) {
        this.notificationQueryRepository = notificationQueryRepository;
    }

    @Override
    public Page<FillOutApplicationFormNotificationDto> query(final Query query) {
        return notificationQueryRepository.findAllFillOutApplicationFormNotificationByReceiverId(query.memberId(), query.pageable())
                .map(FillOutApplicationFormNotification::toDto);
    }
}

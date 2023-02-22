package com.mohaeng.notification.application.eventhandler;

import com.mohaeng.club.applicationform.domain.event.ApplicationProcessedEvent;
import com.mohaeng.common.event.EventHandler;
import com.mohaeng.common.event.EventHistoryRepository;
import com.mohaeng.notification.domain.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 누군가 가입 신청서를 처리했을 때, 연관된 가입 신청서 알림 제거를 위한 이벤트 핸들러
 */
@Component
public class DeleteFillOutApplicationNotificationWithApplicationProcessedEventHandler extends EventHandler<ApplicationProcessedEvent> {

    private final NotificationRepository notificationRepository;

    protected DeleteFillOutApplicationNotificationWithApplicationProcessedEventHandler(final EventHistoryRepository eventHistoryRepository,
                                                                                       final NotificationRepository notificationRepository) {
        super(eventHistoryRepository);
        this.notificationRepository = notificationRepository;
    }

    @Override
    @Transactional
    @EventListener
    public void handle(final ApplicationProcessedEvent event) {
        notificationRepository.deleteAllInBatch(
                notificationRepository.findApplicationProcessedNotificationByApplicationFormId(event.applicationFormId())
        );
        process(event);
    }
}

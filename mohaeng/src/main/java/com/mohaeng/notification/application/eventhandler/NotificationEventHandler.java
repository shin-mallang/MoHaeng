package com.mohaeng.notification.application.eventhandler;

import com.mohaeng.common.event.EventHandler;
import com.mohaeng.common.event.EventHistoryRepository;
import com.mohaeng.common.notification.NotificationEvent;
import com.mohaeng.notification.domain.model.NotificationMakeStrategies;
import com.mohaeng.notification.domain.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class NotificationEventHandler extends EventHandler<NotificationEvent> {

    private static final Logger log = LoggerFactory.getLogger(NotificationEventHandler.class);

    private final NotificationRepository notificationRepository;
    private final NotificationMakeStrategies notificationStrategies;

    protected NotificationEventHandler(final EventHistoryRepository eventHistoryRepository,
                                       final NotificationRepository notificationRepository,
                                       final NotificationMakeStrategies notificationStrategies) {
        super(eventHistoryRepository);
        this.notificationRepository = notificationRepository;
        this.notificationStrategies = notificationStrategies;
    }

    @Override
    @Async(value = "asyncTaskExecutor")
    @EventListener
    @Transactional // 어차피 Async 이므로 Thread가 달라 트랜잭션이 다르다. 즉 REQUIRES_NEW 같은게 필요없다.
    public void handle(final NotificationEvent event) {

        log.info("[ALARM EVENT] {}", event);

        notificationRepository.saveAll(notificationStrategies.make(event));

        process(event);
    }
}

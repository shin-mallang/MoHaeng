package com.mohaeng.notification.application.eventhandler;

import com.mohaeng.notification.domain.model.Notification;
import com.mohaeng.notification.domain.model.value.Receiver;
import com.mohaeng.notification.domain.repository.NotificationRepository;
import com.mohaeng.common.notification.NotificationEvent;
import com.mohaeng.common.event.EventHandler;
import com.mohaeng.common.event.EventHistoryRepository;
import com.mohaeng.member.domain.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class NotificationEventHandler extends EventHandler<NotificationEvent> {

    private static final Logger log = LoggerFactory.getLogger(NotificationEventHandler.class);

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;

    protected NotificationEventHandler(final EventHistoryRepository eventHistoryRepository,
                                       final NotificationRepository notificationRepository,
                                       final MemberRepository memberRepository) {
        super(eventHistoryRepository);
        this.notificationRepository = notificationRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    @Async(value = "asyncTaskExecutor")
    @EventListener
    @Transactional // 어차피 Async 이므로 Thread가 달라 트랜잭션이 다르다. 즉 REQUIRES_NEW 같은게 필요없다.
    public void handle(final NotificationEvent event) {
        log.info("[ALARM EVENT] {}", event);

        notificationRepository.saveAll(makeAlarms(event));

        process(event);
    }

    private List<Notification> makeAlarms(NotificationEvent event) {
        // 알람 타입 가져오기
        AlarmType alarmType = AlarmType.ofEvent(event.getClass());

        // 알람 이벤트 타입에 맞는 알람 메세지 생성
        AlarmMessage alarmMessage = generateMessage(event, alarmType);

        // 수신자 조회
        List<Receiver> receivers = getReceivers(event);

        // 알람 만들어 반환
        return receivers.stream()
                .map(it -> Notification.of(it, alarmMessage, alarmType))
                .toList();
    }

    private AlarmMessage generateMessage(final NotificationEvent event, final AlarmType alarmType) {
        AlarmMessageGenerator generator = alarmMessageGenerateFactory.getGenerator(alarmType);
        return generator.generate(event);
    }

    private List<Receiver> getReceivers(final NotificationEvent event) {
        return memberRepository.findByIdIn(event.receiverIds())
                .stream()
                .map(Receiver::of)
                .toList();
    }
}

package com.mohaeng.alarm.application.eventhandler;

import com.mohaeng.alarm.application.service.generator.RequestClubJoinAlarmContentGenerator;
import com.mohaeng.alarm.domain.model.Alarm;
import com.mohaeng.alarm.domain.model.value.AlarmMessage;
import com.mohaeng.alarm.domain.model.value.AlarmType;
import com.mohaeng.alarm.domain.model.value.Receiver;
import com.mohaeng.alarm.domain.repository.AlarmRepository;
import com.mohaeng.applicationform.domain.event.RequestJoinClubEvent;
import com.mohaeng.common.event.EventHandler;
import com.mohaeng.common.event.EventHistoryRepository;
import com.mohaeng.participant.domain.model.Participant;
import com.mohaeng.participant.domain.repository.ParticipantRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RequestClubJoinEventHandler extends EventHandler<RequestJoinClubEvent> {

    private static final Logger log = LoggerFactory.getLogger(RequestClubJoinEventHandler.class);

    private final RequestClubJoinAlarmContentGenerator requestClubJoinAlarmContentGenerator;
    private final AlarmRepository alarmRepository;
    private final ParticipantRepository participantRepository;

    protected RequestClubJoinEventHandler(final EventHistoryRepository eventHistoryRepository,
                                          final RequestClubJoinAlarmContentGenerator requestClubJoinAlarmContentGenerator,
                                          final AlarmRepository alarmRepository,
                                          final ParticipantRepository participantRepository) {
        super(eventHistoryRepository);
        this.requestClubJoinAlarmContentGenerator = requestClubJoinAlarmContentGenerator;
        this.alarmRepository = alarmRepository;
        this.participantRepository = participantRepository;
    }

    @Override
    @Async(value = "asyncTaskExecutor")
    @EventListener
    @Transactional // 어차피 Async 이므로 Thread가 달라 트랜잭션이 다르다. 즉 REQUIRES_NEW 같은게 필요없다.
    public void handle(final RequestJoinClubEvent event) {
        log.info("alarm event = {}", event);

        alarmRepository.saveAll(makeAlarms(event));
    }

    private List<Alarm> makeAlarms(final RequestJoinClubEvent event) {
        AlarmMessage alarmMessage = generateMessage(event);

        List<Receiver> receivers = getReceivers(event);

        AlarmType alarmType = AlarmType.ofEvent(event.getClass());
        return receivers.stream()
                .map(it -> Alarm.of(it, alarmMessage, alarmType))
                .toList();
    }

    private AlarmMessage generateMessage(final RequestJoinClubEvent event) {
        return requestClubJoinAlarmContentGenerator.generate(event);
    }

    private List<Receiver> getReceivers(final RequestJoinClubEvent event) {
        return participantRepository
                // 임원, 회장 모두 조회
                .findAllWithMemberByClubIdWhereClubRoleIsPresidentOrOfficer(event.targetClubId())
                .stream()
                .map(Participant::member)
                .map(Receiver::of)
                .toList();
    }
}

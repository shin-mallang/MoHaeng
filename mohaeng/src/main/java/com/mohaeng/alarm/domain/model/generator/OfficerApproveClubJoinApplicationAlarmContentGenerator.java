package com.mohaeng.alarm.domain.model.generator;

import com.mohaeng.alarm.domain.model.AlarmMessageGenerator;
import com.mohaeng.alarm.domain.model.value.AlarmMessage;
import com.mohaeng.alarm.domain.model.value.AlarmType;
import com.mohaeng.applicationform.domain.event.OfficerApproveClubJoinApplicationEvent;
import com.mohaeng.common.alarm.AlarmEvent;
import com.mohaeng.participant.domain.model.Participant;
import com.mohaeng.participant.domain.repository.ParticipantRepository;
import com.mohaeng.participant.exception.ParticipantException;
import org.springframework.stereotype.Component;

import static com.mohaeng.participant.exception.ParticipantExceptionType.NOT_FOUND_PARTICIPANT;

/**
 * 임원진이 가입 신청에 승인한 것에 대한 회장에게 알림 메세지 생성 전략
 */
@Component
public class OfficerApproveClubJoinApplicationAlarmContentGenerator implements AlarmMessageGenerator {

    private static final String TITLE = "임원진이 모임 가입 신청을 승인하였습니다.";

    // ex: 임원 신동훈({1})(이)가 신말랑({2})의 가입을 수락하였습니다.
    // 임원과 신규 회원 모두 Participant ID
    private static final String MESSAGE_FORMAT = "임원 %s(이)가 %s의 가입을 수락하였습니다.";
    private static final String NAME_WITH_ID_FORMAT = "%s({%s})";  // ex: 신동훈({1})

    private final ParticipantRepository participantRepository;

    public OfficerApproveClubJoinApplicationAlarmContentGenerator(final ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    @Override
    public AlarmMessage generate(final AlarmEvent alarmEvent) {
        OfficerApproveClubJoinApplicationEvent event = (OfficerApproveClubJoinApplicationEvent) alarmEvent;

        Participant manager = participantRepository.findWithMemberById(event.managerId()).orElseThrow(() -> new ParticipantException(NOT_FOUND_PARTICIPANT));
        Participant applicant = participantRepository.findWithMemberById(event.applicantId()).orElseThrow(() -> new ParticipantException(NOT_FOUND_PARTICIPANT));
        String managerMessage = NAME_WITH_ID_FORMAT.formatted(manager.member().name(), manager.member().id());
        String applicantMessage = NAME_WITH_ID_FORMAT.formatted(applicant.member().name(), applicant.member().id());

        return new AlarmMessage(TITLE, MESSAGE_FORMAT.formatted(managerMessage, applicantMessage));
    }

    @Override
    public AlarmType alarmType() {
        return AlarmType.ofEvent(OfficerApproveClubJoinApplicationEvent.class);
    }
}

package com.mohaeng.alarm.application.service.generator;

import com.mohaeng.alarm.application.service.AlarmMessageGenerator;
import com.mohaeng.alarm.domain.model.value.AlarmMessage;
import com.mohaeng.alarm.domain.model.value.AlarmType;
import com.mohaeng.applicationform.domain.event.RequestJoinClubEvent;
import com.mohaeng.applicationform.domain.model.ApplicationForm;
import com.mohaeng.applicationform.domain.repository.ApplicationFormRepository;
import com.mohaeng.applicationform.exception.ApplicationFormException;
import com.mohaeng.club.domain.model.Club;
import com.mohaeng.common.alarm.AlarmEvent;
import com.mohaeng.member.domain.model.Member;
import org.springframework.stereotype.Component;

import static com.mohaeng.applicationform.exception.ApplicationFormExceptionType.NOT_FOUND_APPLICATION_FORM;

/**
 * 가입 신청에 대한 알림 메세지 생성 전략
 */
@Component
public class RequestClubJoinAlarmContentGenerator implements AlarmMessageGenerator {

    private static final String TITLE = "모임 가입 신청이 왔습니다.";
    private static final String NAME_WITH_ID_FORMAT = "%s({%s})";  // ex: 신동훈({1})
    // ex: 신동훈({1})님이 ANA({2})모임에 가입 신청을 보냈습니다.
    private static final String MESSAGE_FORMAT = "%s님이 %s모임에 가입 신청을 보냈습니다. (ApplicationFormId:{%d})";


    private final ApplicationFormRepository applicationFormRepository;

    public RequestClubJoinAlarmContentGenerator(final ApplicationFormRepository applicationFormRepository) {
        this.applicationFormRepository = applicationFormRepository;
    }

    @Override
    public AlarmMessage generate(final AlarmEvent alarmEvent) {
        RequestJoinClubEvent event = (RequestJoinClubEvent) alarmEvent;

        ApplicationForm applicationForm = applicationFormRepository.findWithMemberAndClubById(event.applicationFormId())
                .orElseThrow(() -> new ApplicationFormException(NOT_FOUND_APPLICATION_FORM));

        Member sender = applicationForm.applicant();
        String senderMessage = NAME_WITH_ID_FORMAT.formatted(sender.name(), sender.id());

        Club target = applicationForm.target();
        String clubMessage = NAME_WITH_ID_FORMAT.formatted(target.name(), target.id());

        return new AlarmMessage(TITLE, MESSAGE_FORMAT.formatted(senderMessage, clubMessage, applicationForm.id()));
    }

    @Override
    public AlarmType alarmType() {
        return AlarmType.ofEvent(RequestJoinClubEvent.class);
    }
}

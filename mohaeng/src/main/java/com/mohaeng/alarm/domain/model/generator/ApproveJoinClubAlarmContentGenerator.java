package com.mohaeng.alarm.domain.model.generator;

import com.mohaeng.alarm.domain.model.AlarmMessageGenerator;
import com.mohaeng.alarm.domain.model.value.AlarmMessage;
import com.mohaeng.alarm.domain.model.value.AlarmType;
import com.mohaeng.applicationform.domain.event.ApproveJoinClubEvent;
import com.mohaeng.club.domain.model.Club;
import com.mohaeng.club.domain.repository.ClubRepository;
import com.mohaeng.club.exception.ClubException;
import com.mohaeng.common.alarm.AlarmEvent;
import org.springframework.stereotype.Component;

import static com.mohaeng.club.exception.ClubExceptionType.NOT_FOUND_CLUB;

/**
 * 가입 신청에 승인에 대한 알림 메세지 생성 전략
 */
@Component
public class ApproveJoinClubAlarmContentGenerator implements AlarmMessageGenerator {

    private static final String TITLE = "모임 가입 신청이 승인되었습니다.";

    // ex: ANA({2}) 모임에 가입되셨습니다.
    private static final String MESSAGE_FORMAT = "%s 모임에 가입되었습니다.";
    private static final String NAME_WITH_ID_FORMAT = "%s({%s})";  // ex: 신동훈({1})

    private final ClubRepository clubRepository;

    public ApproveJoinClubAlarmContentGenerator(final ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    @Override
    public AlarmMessage generate(final AlarmEvent alarmEvent) {
        ApproveJoinClubEvent event = (ApproveJoinClubEvent) alarmEvent;

        Club club = clubRepository.findById(event.clubId())
                .orElseThrow(() -> new ClubException(NOT_FOUND_CLUB));

        String clubMessage = NAME_WITH_ID_FORMAT.formatted(club.name(), club.id());

        return new AlarmMessage(TITLE, MESSAGE_FORMAT.formatted(clubMessage));
    }

    @Override
    public AlarmType alarmType() {
        return AlarmType.ofEvent(ApproveJoinClubEvent.class);
    }
}

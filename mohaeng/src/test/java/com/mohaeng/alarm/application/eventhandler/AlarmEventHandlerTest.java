package com.mohaeng.alarm.application.eventhandler;

import com.mohaeng.alarm.domain.model.AlarmMessageGenerateFactory;
import com.mohaeng.alarm.domain.repository.AlarmRepository;
import com.mohaeng.applicationform.domain.event.ApproveJoinClubEvent;
import com.mohaeng.common.EventHandlerTest;
import com.mohaeng.common.alarm.MockAlarmMessageGenerator;
import com.mohaeng.common.repositories.MockAlarmRepository;
import com.mohaeng.common.repositories.MockMemberRepository;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.member.domain.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.mohaeng.common.fixtures.MemberFixture.member;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("AlarmEventHandler 는 ")
class AlarmEventHandlerTest extends EventHandlerTest {

    /**
     * AlarmEvent를 받아 이를 Alram으로 만들어 저장한다.
     */
    private final AlarmMessageGenerateFactory alarmMessageGenerateFactory = mock(AlarmMessageGenerateFactory.class);
    private final AlarmRepository alarmRepository = new MockAlarmRepository();
    private final MemberRepository memberRepository = new MockMemberRepository();

    private AlarmEventHandler alarmEventHandler =
            new AlarmEventHandler(eventHistoryRepository, alarmMessageGenerateFactory, alarmRepository, memberRepository);

    @BeforeEach
    void init() {
        MockAlarmMessageGenerator mockAlarmMessageGenerator = new MockAlarmMessageGenerator();
        when(alarmMessageGenerateFactory.getGenerator(any())).thenReturn(mockAlarmMessageGenerator);
    }

    @Test
    @DisplayName("AlarmEvent를 받아 이를 Alram으로 만들어 저장한다.")
    void test() {
        // given
        ApproveJoinClubEvent approveJoinClubEvent = new ApproveJoinClubEvent(this, 1L, 1L);

        // when
        alarmEventHandler.handle(approveJoinClubEvent);

        // then
        Assertions.assertThat(alarmRepository.findAll().size()).isEqualTo(1);
    }
}
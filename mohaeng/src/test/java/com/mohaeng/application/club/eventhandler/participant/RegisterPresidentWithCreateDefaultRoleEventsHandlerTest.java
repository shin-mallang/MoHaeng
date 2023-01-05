package com.mohaeng.application.club.eventhandler.participant;

import com.mohaeng.participant.application.eventhandler.RegisterPresidentWithCreateDefaultRoleEventHandler;
import com.mohaeng.clubrole.domain.event.CreateDefaultRoleEvent;
import com.mohaeng.clubrole.domain.event.CreateDefaultRoleHistory;
import com.mohaeng.club.domain.model.Club;
import com.mohaeng.participant.domain.model.Participant;
import com.mohaeng.clubrole.domain.model.ClubRole;
import com.mohaeng.clubrole.domain.model.ClubRoleCategory;
import com.mohaeng.participant.domain.repository.ParticipantRepository;
import com.mohaeng.common.event.EventHistoryRepository;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.member.domain.model.enums.Gender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@DisplayName("RegisterPresidentWithCreateDefaultRoleEventHandler 는 ")
class RegisterPresidentWithCreateDefaultRoleEventsHandlerTest {

    private final EventHistoryRepository eventHistoryRepository = mock(EventHistoryRepository.class);
    private final ParticipantRepository participantRepository = mock(ParticipantRepository.class);

    private final RegisterPresidentWithCreateDefaultRoleEventHandler handler =
            new RegisterPresidentWithCreateDefaultRoleEventHandler(eventHistoryRepository, participantRepository);

    @Test
    @DisplayName("기본 역할 생성 이벤트(CreateDefaultRoleEvent) 를 받으면 모임을 생성한 회원을 회장으로 등록하고, 이벤트 기록을 저장한다.")
    void test() {
        // given
        final Member member = new Member(1L, LocalDateTime.now(), LocalDateTime.now(),
                "username", "password", "name", 10, Gender.MAN);
        final Club club = new Club(1L, LocalDateTime.now(), LocalDateTime.now(), "name", "des", 100);
        final ClubRole role = new ClubRole("회장", ClubRoleCategory.PRESIDENT, club);

        CreateDefaultRoleEvent createDefaultRoleEvent = new CreateDefaultRoleEvent(this, member, club, role);
        // when
        handler.handle(createDefaultRoleEvent);

        // then
        assertAll(
                () -> verify(participantRepository, times(1)).save(any(Participant.class)),
                () -> verify(eventHistoryRepository, times(1)).save(any(CreateDefaultRoleHistory.class))
        );
    }
}
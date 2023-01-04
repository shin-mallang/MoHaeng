package com.mohaeng.application.club.eventhandler.participant;

import com.mohaeng.domain.club.event.role.CreateDefaultRoleEvent;
import com.mohaeng.domain.club.event.role.CreateDefaultRoleHistory;
import com.mohaeng.domain.club.model.club.Club;
import com.mohaeng.domain.club.model.participant.Participant;
import com.mohaeng.domain.club.model.role.ClubRole;
import com.mohaeng.domain.club.model.role.ClubRoleCategory;
import com.mohaeng.domain.club.repository.participant.ParticipantRepository;
import com.mohaeng.domain.config.event.EventHistoryRepository;
import com.mohaeng.domain.member.model.Member;
import com.mohaeng.domain.member.model.enums.Gender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@DisplayName("RegisterPresidentWithCreateDefaultRoleEventHandler 는 ")
class RegisterPresidentWithCreateDefaultRoleEventHandlerTest {

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
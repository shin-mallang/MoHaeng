package com.mohaeng.participant.application.eventhandler;

import com.mohaeng.club.domain.model.Club;
import com.mohaeng.clubrole.domain.event.CreateDefaultRoleEvent;
import com.mohaeng.clubrole.domain.model.ClubRole;
import com.mohaeng.common.EventHandlerTest;
import com.mohaeng.common.repositories.MockClubRepository;
import com.mohaeng.common.repositories.MockClubRoleRepository;
import com.mohaeng.common.repositories.MockMemberRepository;
import com.mohaeng.common.repositories.MockParticipantRepository;
import com.mohaeng.member.domain.model.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.mohaeng.common.fixtures.ClubFixture.club;
import static com.mohaeng.common.fixtures.ClubRoleFixture.presidentRole;
import static com.mohaeng.common.fixtures.MemberFixture.member;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("RegisterPresidentWithCreateDefaultRoleEventHandler 는 ")
class RegisterPresidentWithCreateDefaultRoleEventsHandlerTest extends EventHandlerTest {

    private final MockParticipantRepository participantRepository = new MockParticipantRepository();
    private final MockMemberRepository memberRepository = new MockMemberRepository();
    private final MockClubRepository clubRepository = new MockClubRepository();
    private final MockClubRoleRepository clubRoleRepository = new MockClubRoleRepository();

    private final RegisterPresidentWithCreateDefaultRoleEventHandler handler =
            new RegisterPresidentWithCreateDefaultRoleEventHandler(eventHistoryRepository, participantRepository,
                    memberRepository, clubRepository, clubRoleRepository);

    @Test
    @DisplayName("기본 역할 생성 이벤트(CreateDefaultRoleEvent) 를 받으면 모임을 생성한 회원을 회장으로 등록한다.")
    void test() {
        // given
        final Member member = memberRepository.save(member(null));
        final Club club = clubRepository.save(club(null));
        final ClubRole role = clubRoleRepository.save(presidentRole("회장", club));

        CreateDefaultRoleEvent createDefaultRoleEvent = new CreateDefaultRoleEvent(this, member.id(), club.id(), role.id());

        // when
        handler.handle(createDefaultRoleEvent);

        // then
        assertAll(
                () -> Assertions.assertThat(participantRepository.findAll().size()).isEqualTo(1)
        );
    }
}
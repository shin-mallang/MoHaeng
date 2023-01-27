package com.mohaeng.participant.application.eventhandler;

import com.mohaeng.club.domain.model.Club;
import com.mohaeng.club.domain.repository.ClubRepository;
import com.mohaeng.clubrole.domain.event.CreateDefaultRoleEvent;
import com.mohaeng.clubrole.domain.model.ClubRole;
import com.mohaeng.clubrole.domain.repository.ClubRoleRepository;
import com.mohaeng.common.EventHandlerTest;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.member.domain.repository.MemberRepository;
import com.mohaeng.participant.domain.model.Participant;
import com.mohaeng.participant.domain.repository.ParticipantRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.mohaeng.common.fixtures.ClubFixture.club;
import static com.mohaeng.common.fixtures.ClubRoleFixture.presidentRole;
import static com.mohaeng.common.fixtures.MemberFixture.member;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("RegisterPresidentWithCreateDefaultRoleEventHandler 는 ")
class RegisterPresidentWithCreateDefaultRoleEventsHandlerTest extends EventHandlerTest {

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private ClubRoleRepository clubRoleRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private RegisterPresidentWithCreateDefaultRoleEventHandler handler;

    @BeforeEach
    public void init() {
        handler = new RegisterPresidentWithCreateDefaultRoleEventHandler(eventHistoryRepository, participantRepository, memberRepository, clubRepository, clubRoleRepository);
    }

    @Nested
    @DisplayName("성공 테스트")
    class SuccessTest {

        @Test
        @DisplayName("기본 역할 생성 이벤트(CreateDefaultRoleEvent) 를 받으면 모임을 생성한 회원을 회장으로 등록한다.")
        void success_test_1() {
            // given
            final Member member = memberRepository.save(member(null));
            final Club club = clubRepository.save(club(null));
            final ClubRole role = clubRoleRepository.saveAll(List.of(presidentRole("회장", club))).get(0);

            CreateDefaultRoleEvent createDefaultRoleEvent = new CreateDefaultRoleEvent(this, member.id(), club.id(), role.id());

            // when
            handler.handle(createDefaultRoleEvent);

            // then
            assertAll(
                    () -> assertThat(em.createQuery("select p from Participant p", Participant.class).getResultList().size()).isEqualTo(1)
            );
        }
    }
}

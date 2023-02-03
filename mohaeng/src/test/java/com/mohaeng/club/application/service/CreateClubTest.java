package com.mohaeng.club.application.service;

import com.mohaeng.club.application.usecase.CreateClubUseCase;
import com.mohaeng.club.domain.event.CreateClubEvent;
import com.mohaeng.clubrole.domain.model.ClubRole;
import com.mohaeng.common.annotation.ApplicationTest;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.member.domain.repository.MemberRepository;
import com.mohaeng.participant.domain.model.Participant;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.event.ApplicationEvents;

import static com.mohaeng.common.fixtures.ClubFixture.createClubUseCaseCommand;
import static com.mohaeng.common.fixtures.MemberFixture.member;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ApplicationTest
@DisplayName("CreateClub 은 ")
class CreateClubTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CreateClubUseCase clubUseCase;

    @Autowired
    private ApplicationEvents events;

    @Nested
    @DisplayName("성공 테스트")
    class SuccessTest {

        @Test
        @DisplayName("회원 id, 모임 이름, 모임 설명, 최대 인원수를 가지고 모임을 생성한 후, 이벤트를 통해 모임의 기본 역할을 저장한 뒤, 모임을 생성한 회원을 회장으로 만든다.")
        void success_test_1() {
            // when
            Member member = memberRepository.save(member(null));
            Long clubId = clubUseCase.command(createClubUseCaseCommand(member.id()));

            // then
            assertAll(
                    () -> assertThat(clubId).isNotNull(),
                    () -> assertThat(em.createQuery("select p from Participant p", Participant.class).getResultList().size()).isEqualTo(1),
                    () -> assertThat(em.createQuery("select cr from ClubRole cr", ClubRole.class).getResultList().size()).isEqualTo(3)
            );
        }

        @Test
        @DisplayName("모임 생성 시 모임 생성 이벤트를 발행한다.")
        void success_test_2() {
            // when
            Member member = memberRepository.save(member(null));
            Long clubId = clubUseCase.command(createClubUseCaseCommand(member.id()));

            // then
            assertAll(
                    () -> assertThat(clubId).isNotNull(),
                    () -> assertThat(events.stream(CreateClubEvent.class).count()).isEqualTo(1L)
            );
        }
    }
}
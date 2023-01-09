package com.mohaeng.club.application.service;

import com.mohaeng.club.application.usecase.CreateClubUseCase;
import com.mohaeng.clubrole.domain.model.ClubRole;
import com.mohaeng.common.annotation.ApplicationTest;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.member.domain.repository.MemberRepository;
import com.mohaeng.participant.domain.model.Participant;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.mohaeng.common.fixtures.ClubFixture.createClubUseCaseCommand;
import static com.mohaeng.common.fixtures.MemberFixture.member;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ApplicationTest
@DisplayName("CreateClub은 ")
class CreateClubTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CreateClubUseCase clubUseCase;

    @Test
    @DisplayName("회원 id, 모임 이름, 모임 설명, 최대 인원수를 가지고 모임을 생성한 후, 이벤트를 통해 모임의 기본 역할을 저장한 뒤, 모임을 생성한 회원을 회장으로 만든다.")
    void createTest() {
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
}
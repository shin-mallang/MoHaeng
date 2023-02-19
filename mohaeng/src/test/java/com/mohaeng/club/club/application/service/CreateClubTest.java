package com.mohaeng.club.club.application.service;

import com.mohaeng.club.club.application.usecase.CreateClubUseCase;
import com.mohaeng.club.club.domain.model.Club;
import com.mohaeng.club.club.domain.repository.ClubRepository;
import com.mohaeng.common.annotation.ApplicationTest;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.member.domain.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import static com.mohaeng.common.fixtures.MemberFixture.MALLANG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ApplicationTest
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("CreateClub 은")
class CreateClubTest {

    private static final String NAME = "ANA";
    private static final String DESCRIPTION = "알고리즘 동아리";
    private static final int MAX_PARTICIPANT_COUNT = 10;
    private final Member member = MALLANG;

    @Autowired
    private EntityManager em;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private CreateClub clubUseCase;

    @Nested
    @DisplayName("성공 테스트")
    class SuccessTest {

        @Test
        void 모임_생성_시_모임의_기본_역할과_회장을_같이_생성한다() {
            // given
            Member member = memberRepository.save(MALLANG);

            // when
            Long clubId = clubUseCase.command(
                    new CreateClubUseCase.Command(member.id(), NAME, DESCRIPTION, MAX_PARTICIPANT_COUNT)
            );

            // then
            em.flush();
            em.clear();
            Club club = clubRepository.findById(clubId).get();
            assertAll(
                    () -> assertThat(club.name()).isEqualTo(NAME),
                    () -> assertThat(club.currentParticipantCount()).isEqualTo(1),
                    () -> assertThat(club.clubRoles().clubRoles().size()).isEqualTo(3),
                    () -> assertThat(club.participants().participants().size()).isEqualTo(1)
            );
        }
    }
}
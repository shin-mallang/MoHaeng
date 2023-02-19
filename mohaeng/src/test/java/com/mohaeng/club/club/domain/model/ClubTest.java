package com.mohaeng.club.club.domain.model;

import com.mohaeng.club.club.exception.ParticipantException;
import com.mohaeng.common.exception.BaseExceptionType;
import com.mohaeng.member.domain.model.Member;
import org.junit.jupiter.api.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static com.mohaeng.club.club.domain.model.ClubRoleCategory.*;
import static com.mohaeng.club.club.exception.ParticipantExceptionType.PRESIDENT_CAN_NOT_LEAVE_CLUB;
import static com.mohaeng.common.fixtures.ClubFixture.clubWithMember;
import static com.mohaeng.common.fixtures.MemberFixture.member;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("Club 은")
class ClubTest {

    private final Member presidentMember = member(1L);
    private final Club club = clubWithMember(presidentMember);
    private final Participant president = club.findPresident();

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(president, "id", 1L);
    }

    @Test
    void 생성_시_모임의_기본_역할과_회장을_같이_저장한다() {
        // then
        assertThat(club.clubRoles().clubRoles().size()).isEqualTo(3);
        assertThat(club.clubRoles().clubRoles().stream().map(ClubRole::clubRoleCategory).toList())
                .containsExactlyInAnyOrderElementsOf(List.of(GENERAL, OFFICER, PRESIDENT));
        assertThat(club.participants().participants().size()).isEqualTo(1);
        assertThat(club.participants().participants().get(0).clubRole().clubRoleCategory()).isEqualTo(PRESIDENT);
    }

    @Test
    void 생성_시_모임의_회원_수는_1이다() {
        // then
        assertThat(club.currentParticipantCount()).isEqualTo(1);
    }

    @Test
    void memberId_를_통해_참여자를_찾을_수_있다() {
        // given
        Long memberId1 = 2L;
        Long memberId2 = 3L;
        Long memberId3 = 4L;
        Member member1 = member(memberId1);
        Member member2 = member(memberId2);
        Member member3 = member(memberId3);
        club.registerParticipant(member1);
        club.registerParticipant(member2);
        club.registerParticipant(member3);

        // when
        Participant participant1 = club.findParticipantByMemberId(memberId1).orElse(null);
        Participant participant2 = club.findParticipantByMemberId(memberId2).orElse(null);
        Participant participant3 = club.findParticipantByMemberId(memberId3).orElse(null);

        // then
        assertThat(participant1.member()).isEqualTo(member1);
        assertThat(participant2.member()).isEqualTo(member2);
        assertThat(participant3.member()).isEqualTo(member3);
    }

    @Test
    void memberId_를_가진_참여자가_없는_경우() {
        // when
        Optional<Participant> participantByMemberId = club.findParticipantByMemberId(100L);

        // then
        assertThat(participantByMemberId).isEmpty();
    }

    @Test
    void findPresident_는_회장을_반환한다() {
        // when
        Participant president = club.findPresident();

        // then
        assertThat(president.clubRole().clubRoleCategory()).isEqualTo(PRESIDENT);
    }

    @Test
    void findParticipantById_는_참여자_ID_를_통해_참여자를_찾는다() {
        // when & then
        assertThat(club.findParticipantById(1L).get()).isEqualTo(president);
        assertThat(club.findParticipantById(9999L)).isEmpty();
    }

    @Test
    void deleteParticipant_는_회원을_모임에서_제거한다() {
        // given
        Member target = member(10L);
        club.registerParticipant(target);
        Participant participant = club.findParticipantByMemberId(target.id()).orElse(null);
        int before = club.currentParticipantCount();

        // when
        club.deleteParticipant(participant);

        // then
        assertThat(club.findParticipantByMemberId(target.id())).isEmpty();
        assertThat(club.currentParticipantCount()).isEqualTo(before - 1);
    }

    @Test
    void deleteParticipant_시_회장은_모임을_탈퇴할_수_없다() {
        // given
        Participant president = club.findPresident();

        // when
        BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class, () ->
                club.deleteParticipant(president)
        ).exceptionType();

        // then
        assertThat(baseExceptionType).isEqualTo(PRESIDENT_CAN_NOT_LEAVE_CLUB);
    }
}
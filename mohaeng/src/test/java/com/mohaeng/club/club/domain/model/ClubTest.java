package com.mohaeng.club.club.domain.model;

import com.mohaeng.member.domain.model.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static com.mohaeng.club.club.domain.model.ClubRoleCategory.*;
import static com.mohaeng.common.fixtures.ClubFixture.*;
import static com.mohaeng.common.fixtures.MemberFixture.MALLANG;
import static com.mohaeng.common.fixtures.MemberFixture.member;
import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("Club 은")
class ClubTest {

    private final Club club = new Club(ANA_NAME, ANA_DESCRIPTION, ANA_MAX_PARTICIPANT_COUNT, MALLANG);

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
        Long memberId1 = 1L;
        Long memberId2 = 2L;
        Long memberId3 = 3L;
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
        Optional<Participant> participantByMemberId = club.findParticipantByMemberId(1L);

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
}
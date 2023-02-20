package com.mohaeng.club.club.domain.model;

import com.mohaeng.club.club.exception.ClubRoleException;
import com.mohaeng.club.club.exception.ParticipantException;
import com.mohaeng.common.exception.BaseExceptionType;
import com.mohaeng.member.domain.model.Member;
import org.junit.jupiter.api.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.mohaeng.club.club.domain.model.ClubRoleCategory.*;
import static com.mohaeng.club.club.exception.ClubRoleExceptionType.NOT_FOUND_ROLE;
import static com.mohaeng.club.club.exception.ParticipantExceptionType.*;
import static com.mohaeng.common.fixtures.ClubFixture.clubWithMember;
import static com.mohaeng.common.fixtures.MemberFixture.member;
import static com.mohaeng.common.fixtures.ParticipantFixture.participantWithId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("Club 은")
class ClubTest {

    private final Long presidentMemberId = 1L;
    private final Long officerMemberId = 2L;
    private final Long generalMemberId = 3L;
    private final Long presidentId = 1L;
    private final Long officerId = 2L;
    private final Long generalId = 3L;
    private final Member presidentMember = member(presidentMemberId);
    private final Club club = clubWithMember(presidentMember);
    private final Map<ClubRoleCategory, ClubRole> clubRoleMap =
            ClubRole.defaultRoles(club).stream()
                    .collect(Collectors.toMap(ClubRole::clubRoleCategory, it -> it));
    private final Participant president = club.findPresident();
    private final Participant officer = participantWithId(officerId, member(officerMemberId), club, clubRoleMap.get(OFFICER));
    private final Participant general = participantWithId(generalId, member(generalMemberId), club, clubRoleMap.get(GENERAL));

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(president, "id", presidentId);
        club.participants().participants().add(officer);
        club.participants().participants().add(general);
    }

    @Test
    void 생성_시_모임의_기본_역할과_회장을_같이_저장한다() {
        // when
        Club club = clubWithMember(presidentMember);

        // then
        assertThat(club.clubRoles().clubRoles().size()).isEqualTo(3);
        assertThat(club.clubRoles().clubRoles().stream().map(ClubRole::clubRoleCategory).toList())
                .containsExactlyInAnyOrderElementsOf(List.of(GENERAL, OFFICER, PRESIDENT));
        assertThat(club.participants().participants().size()).isEqualTo(1);
        assertThat(club.participants().participants().get(0).clubRole().clubRoleCategory()).isEqualTo(PRESIDENT);
    }

    @Test
    void 생성_시_모임의_회원_수는_1이다() {
        // when
        Club club = clubWithMember(presidentMember);

        // then
        assertThat(club.currentParticipantCount()).isEqualTo(1);
    }

    @Test
    void memberId_를_통해_참여자를_찾을_수_있다() {
        // when
        Participant participant1 = club.findParticipantByMemberId(officerMemberId).orElse(null);
        Participant participant2 = club.findParticipantByMemberId(generalMemberId).orElse(null);

        // then
        assertThat(participant1.member()).isEqualTo(officer.member());
        assertThat(participant2.member()).isEqualTo(general.member());
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
    void findRoleById는_Role의_ID를_통해_해당_역할을_조회한다() {
        // given
        ClubRoles clubRoles = club.clubRoles();
        for (int i = 1; i <= clubRoles.clubRoles().size(); i++) {
            ReflectionTestUtils.setField(clubRoles.clubRoles().get(i - 1), "id", (long) i);
        }

        // when & then
        assertThat(club.findRoleById(1L)).isPresent();
        assertThat(club.findRoleById(2L)).isPresent();
        assertThat(club.findRoleById(3L)).isPresent();
        assertThat(club.findRoleById(4L)).isEmpty();
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

    @Test
    void findAllParticipant_는_모든_참가자를_반환한다() {
        // when
        List<Participant> allParticipant = club.findAllParticipant();

        // then
        assertThat(allParticipant.size()).isEqualTo(club.participants().participants().size());
    }

    @Nested
    @DisplayName("추방(expel)")
    class ExpelTest {

        @Test
        void expel_시_대상_참여자를_모임에서_추방한다() {
            // given
            int before = club.currentParticipantCount();

            // when
            club.expel(president.member().id(), officer.id());
            club.expel(president.member().id(), general.id());

            // then
            assertThat(club.currentParticipantCount()).isEqualTo(before - 2);
            assertThat(club.findParticipantByMemberId(officer.member().id())).isEmpty();
            assertThat(club.findParticipantByMemberId(general.member().id())).isEmpty();
        }

        @Test
        void expel_시_회장이_아닌_경우_추방할_수_없다() {
            // given
            int before = club.currentParticipantCount();

            // when
            BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class, () ->
                    club.expel(officer.member().id(), general.id())
            ).exceptionType();

            // then
            assertThat(club.currentParticipantCount()).isEqualTo(before);
            assertThat(club.findParticipantByMemberId(general.member().id())).isPresent();
            assertThat(baseExceptionType).isEqualTo(NO_AUTHORITY_EXPEL_PARTICIPANT);
        }

        @Test
        void expel_시_대상_참여자와_같은_모임이_아닌_경우_추방할_수_없다() {
            // given
            Long presidentId = 11L;
            Long generalId = 12L;
            Club other = clubWithMember(member(presidentId));
            Participant otherPresident = other.findPresident();
            ReflectionTestUtils.setField(otherPresident, "id", 10L);
            other.participants().participants().add(participantWithId(13L, member(generalId), other, other.clubRoles().findDefaultRoleByCategory(GENERAL)));
            Participant otherGeneral = other.findParticipantByMemberId(generalId).get();

            // when
            BaseExceptionType baseExceptionType1 = assertThrows(ParticipantException.class, () ->
                    club.expel(president.member().id(), otherPresident.id())
            ).exceptionType();

            BaseExceptionType baseExceptionType2 = assertThrows(ParticipantException.class, () ->
                    club.expel(president.member().id(), otherGeneral.id())
            ).exceptionType();
            BaseExceptionType baseExceptionType3 = assertThrows(ParticipantException.class, () ->
                    club.expel(otherPresident.member().id(), president.id())
            ).exceptionType();

            BaseExceptionType baseExceptionType4 = assertThrows(ParticipantException.class, () ->
                    club.expel(otherPresident.member().id(), general.id())
            ).exceptionType();

            // then
            assertThat(baseExceptionType1).isEqualTo(NOT_FOUND_PARTICIPANT);
            assertThat(baseExceptionType2).isEqualTo(NOT_FOUND_PARTICIPANT);
            assertThat(baseExceptionType3).isEqualTo(NOT_FOUND_PARTICIPANT);
            assertThat(baseExceptionType4).isEqualTo(NOT_FOUND_PARTICIPANT);
        }
    }

    @Nested
    @DisplayName("역할 변경(changeParticipantRole)")
    class ChangeParticipantRoleTest {

        private final Long presidentRoleId = 1L;
        private final Long officerRoleId = 2L;
        private final Long generalRoleId = 3L;

        @BeforeEach
        void init() {
            ReflectionTestUtils.setField(club.findDefaultRoleByCategory(PRESIDENT), "id", presidentRoleId);
            ReflectionTestUtils.setField(club.findDefaultRoleByCategory(OFFICER), "id", officerRoleId);
            ReflectionTestUtils.setField(club.findDefaultRoleByCategory(GENERAL), "id", generalRoleId);
        }

        @Test
        void changeParticipantRole_시_대상의_역할을_변경한다() {
            // given
            assertThat(club.findParticipantById(officerId).get().isManager()).isTrue();
            assertThat(club.findParticipantById(generalId).get().isManager()).isFalse();

            // when
            club.changeParticipantRole(presidentMemberId, officerId, generalRoleId);
            club.changeParticipantRole(presidentMemberId, generalId, officerRoleId);

            // then
            assertThat(club.findParticipantById(officerId).get().isManager()).isFalse();
            assertThat(club.findParticipantById(generalId).get().isManager()).isTrue();
        }

        @Test
        void changeParticipantRole_시_회장이_아닌_경우_예외가_발생한다() {
            // when
            BaseExceptionType baseExceptionType1 = assertThrows(ParticipantException.class, () ->
                    club.changeParticipantRole(officerMemberId, generalId, officerRoleId)
            ).exceptionType();
            BaseExceptionType baseExceptionType2 = assertThrows(ParticipantException.class, () ->
                    club.changeParticipantRole(generalMemberId, officerId, generalRoleId)
            ).exceptionType();

            // then
            assertThat(club.findParticipantById(officerId).get().isManager()).isTrue();
            assertThat(club.findParticipantById(generalId).get().isManager()).isFalse();
            assertThat(baseExceptionType1).isEqualTo(NO_AUTHORITY_CHANGE_PARTICIPANT_ROLE);
            assertThat(baseExceptionType2).isEqualTo(NO_AUTHORITY_CHANGE_PARTICIPANT_ROLE);
        }

        @Test
        void changeParticipantRole_시_회장으로_변경하려는_경우_예외가_발생한다() {
            // when
            BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class, () ->
                    club.changeParticipantRole(presidentMemberId, generalId, presidentRoleId)
            ).exceptionType();

            // then
            assertThat(club.findParticipantById(officerId).get().isPresident()).isFalse();
            assertThat(baseExceptionType).isEqualTo(NOT_CHANGE_PRESIDENT_ROLE);
        }

        @Test
        void changeParticipantRole_시_존재하지_않는_참가자인_경우_예외가_발생한다() {
            // when
            BaseExceptionType baseExceptionType1 = assertThrows(ParticipantException.class, () ->
                    club.changeParticipantRole(presidentMemberId + 100L, generalId, officerRoleId)
            ).exceptionType();
            BaseExceptionType baseExceptionType2 = assertThrows(ParticipantException.class, () ->
                    club.changeParticipantRole(presidentMemberId, generalId + 100L, officerRoleId)
            ).exceptionType();

            // then
            assertThat(club.findParticipantById(generalId).get().isManager()).isFalse();
            assertThat(baseExceptionType1).isEqualTo(NOT_FOUND_PARTICIPANT);
            assertThat(baseExceptionType2).isEqualTo(NOT_FOUND_PARTICIPANT);
        }

        @Test
        void changeParticipantRole_시_역할이_없는_경우_혹은_다른_모임의_역할인_경우_예외가_발생한다() {
            // when
            BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class, () ->
                    club.changeParticipantRole(presidentMemberId, generalId, officerRoleId + 100L)
            ).exceptionType();

            // then
            assertThat(baseExceptionType).isEqualTo(NOT_FOUND_ROLE);
        }
    }
}
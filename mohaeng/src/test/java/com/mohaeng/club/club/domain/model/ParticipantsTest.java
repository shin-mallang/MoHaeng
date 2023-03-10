package com.mohaeng.club.club.domain.model;

import com.mohaeng.club.club.exception.ParticipantException;
import com.mohaeng.common.exception.BaseExceptionType;
import com.mohaeng.common.fixtures.ClubFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mohaeng.club.club.domain.model.ClubRoleCategory.*;
import static com.mohaeng.club.club.exception.ParticipantExceptionType.*;
import static com.mohaeng.common.fixtures.ClubFixture.club;
import static com.mohaeng.common.fixtures.MemberFixture.member;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("Participants(참여자들) 은")
class ParticipantsTest {

    private final Club club = club(1L);
    private final Map<ClubRoleCategory, ClubRole> clubRoleMap =
            ClubRole.defaultRoles(club).stream()
                    .collect(Collectors.toMap(ClubRole::clubRoleCategory, it -> it));

    private Participant president = club.findPresident();
    private Participant officer;
    private Participant general;
    private Participants participants;

    @BeforeEach
    void init() {
        participants = club.participants();
        officer = participants.register(member(2L), club, clubRoleMap.get(OFFICER));
        general = participants.register(member(3L), club, clubRoleMap.get(GENERAL));
        ReflectionTestUtils.setField(president, "id", 1L);
        ReflectionTestUtils.setField(officer, "id", 2L);
        ReflectionTestUtils.setField(general, "id", 3L);
    }

    @Nested
    class initWithPresident_로_생성_시 {
        @Test
        void 회장만을_포함한_Participants_를_반환한다() {
            // when
            Participants participants = Participants.initWithPresident(100, president);

            // then
            assertThat(participants.participants().size()).isEqualTo(1);
            assertThat(participants.participants().get(0)).isEqualTo(president);
        }

        @Test
        void participant_가_회장이_아닌_경우_예외를_발생한다() {
            // when
            BaseExceptionType baseExceptionType1 = assertThrows(ParticipantException.class, () ->
                    Participants.initWithPresident(100, officer)
            ).exceptionType();
            BaseExceptionType baseExceptionType2 = assertThrows(ParticipantException.class, () ->
                    Participants.initWithPresident(100, general)
            ).exceptionType();

            // then
            assertThat(baseExceptionType1).isEqualTo(NOT_PRESIDENT);
            assertThat(baseExceptionType2).isEqualTo(NOT_PRESIDENT);
        }
    }

    @Test
    void findByMemberId_는_memberId가_일치하는_참여자를_반환한다() {
        // when & then
        assertAll(
                () -> assertThat(participants.findByMemberId(president.member().id())).isNotNull(),
                () -> assertThat(participants.findByMemberId(officer.member().id())).isNotNull(),
                () -> assertThat(participants.findByMemberId(general.member().id())).isNotNull(),
                () -> assertThrows(ParticipantException.class, () -> participants.findByMemberId(1000L))
        );
    }

    @Test
    void findAllManager_는_모든_임원_혹은_회장을_반환한다() {
        // when
        List<Participant> allManager = participants.findAllManager();

        // then
        assertThat(allManager)
                .containsExactlyInAnyOrder(president, officer);
    }

    @Test
    void findPresident_는_회장을_반환한다() {
        // when
        Participant president = participants.findPresident();

        // then
        assertThat(president.clubRole().clubRoleCategory()).isEqualTo(PRESIDENT);
    }

    @Test
    void findById_는_참여자의_id_를_통해_참여자를_찾는다() {
        // when & then
        assertThat(participants.findById(officer.id())).isEqualTo(officer);
        assertThrows(ParticipantException.class, () -> participants.findById(1000L));
    }

    @Test
    void register_시_회원을_등록한다() {
        // when
        Participant participant = participants.register(member(100L), club, clubRoleMap.get(GENERAL));

        // then
        assertThat(participants.participants()).contains(participant);
    }

    @Test
    void delete_시_참여자를_제거한다() {
        // when
        participants.delete(general);

        // then
        assertThat(participants.participants()).doesNotContain(general);
    }

    @Test
    void delete_시_회장은_모임을_탈퇴할_수_없다() {
        // when
        BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class, () ->
                participants.delete(president)
        ).exceptionType();

        // then
        assertThat(baseExceptionType).isEqualTo(PRESIDENT_CAN_NOT_LEAVE_CLUB);
    }

    @Nested
    @DisplayName("회장 위임(delegatePresident) 테스트")
    class DelegatePresident {

        @Test
        void 회장은_임임의_회원을_차기_회장으로_위임할_수_있다() {
            // when
            participants.delegatePresident(president.member().id(), general.id(), clubRoleMap.get(GENERAL));

            // then
            assertThat(general.clubRole().clubRoleCategory()).isEqualTo(PRESIDENT);
        }

        @Test
        void 기존_회장은_위임_이후_일반_회원이_된다() {
            // when
            회장은_임임의_회원을_차기_회장으로_위임할_수_있다();

            // then
            assertAll(
                    () -> assertThat(president.clubRole().clubRoleCategory()).isEqualTo(GENERAL),
                    () -> assertThat(president.clubRole().isDefault()).isTrue()
            );
        }

        @Test
        void 요청자가_회장이_아닌경우_예외가_발생한다() {
            // when
            BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class, () -> {
                participants.delegatePresident(officer.member().id(), general.id(), clubRoleMap.get(GENERAL));
            }).exceptionType();

            // then
            assertAll(
                    () -> assertThat(baseExceptionType).isEqualTo(NO_AUTHORITY_DELEGATE_PRESIDENT),
                    () -> assertThat(president.clubRole().clubRoleCategory()).isEqualTo(PRESIDENT),
                    () -> assertThat(general.clubRole().clubRoleCategory()).isEqualTo(GENERAL)
            );
        }

        @Test
        void 요청자가_해당_모임의_참여자_목록에_없는경우_예외() {
            // when
            BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class, () -> {
                participants.delegatePresident(112312L, general.id(), clubRoleMap.get(GENERAL));
            }).exceptionType();

            // then
            assertAll(
                    () -> assertThat(baseExceptionType).isEqualTo(NOT_FOUND_PARTICIPANT),
                    () -> assertThat(president.clubRole().clubRoleCategory()).isEqualTo(PRESIDENT),
                    () -> assertThat(general.clubRole().clubRoleCategory()).isEqualTo(GENERAL)
            );
        }

        @Test
        void 대상자가_해당_모임의_참여자_목록에_없는경우_예외() {
            // when
            BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class, () -> {
                participants.delegatePresident(president.member().id(), 112312L, clubRoleMap.get(GENERAL));
            }).exceptionType();

            // then
            assertAll(
                    () -> assertThat(baseExceptionType).isEqualTo(NOT_FOUND_PARTICIPANT),
                    () -> assertThat(president.clubRole().clubRoleCategory()).isEqualTo(PRESIDENT),
                    () -> assertThat(general.clubRole().clubRoleCategory()).isEqualTo(GENERAL)
            );
        }
    }

    @Test
    void 역할이_제거된_참여자들의_역할을_기본_역할로_변경한다() {
        // given
        final ClubRole role = club.createRole(president.member().id(), "새로", GENERAL);
        participants.register(member(11L), club, role);
        participants.register(member(12L), club, role);

        assertAll(
                () -> assertThat(participants.findByMemberId(11L).clubRole())
                        .isEqualTo(role),
                () -> assertThat(participants.findByMemberId(12L).clubRole())
                        .isEqualTo(role)
        );

        // when
        participants.replaceDeletedRoleIntoDefault(role);

        // then
        assertAll(
                () -> assertThat(participants.findByMemberId(11L).clubRole())
                        .isEqualTo(club.findDefaultRoleByCategory(GENERAL)),
                () -> assertThat(participants.findByMemberId(12L).clubRole())
                        .isEqualTo(club.findDefaultRoleByCategory(GENERAL))
        );
    }

    @Test
    void 참여자가_존재하는지_여부를_확인할_수_있다() {
        // given
        final Club other = ClubFixture.clubWithMember(member(100L));
        final Participant otherPresident = other.findPresident();

        // then
        assertAll(
                () -> assertThat(club.contains(president)).isTrue(),
                () -> assertThat(club.contains(officer)).isTrue(),
                () -> assertThat(club.contains(general)).isTrue(),
                () -> assertThat(club.contains(otherPresident)).isFalse()
        );
    }
}

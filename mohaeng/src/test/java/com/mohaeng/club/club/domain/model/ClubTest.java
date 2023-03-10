package com.mohaeng.club.club.domain.model;

import com.mohaeng.club.club.exception.ClubRoleException;
import com.mohaeng.club.club.exception.ParticipantException;
import com.mohaeng.common.exception.BaseExceptionType;
import com.mohaeng.member.domain.model.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mohaeng.club.club.domain.model.ClubRoleCategory.*;
import static com.mohaeng.club.club.exception.ClubRoleExceptionType.*;
import static com.mohaeng.club.club.exception.ParticipantExceptionType.*;
import static com.mohaeng.common.fixtures.ClubFixture.clubWithMember;
import static com.mohaeng.common.fixtures.MemberFixture.member;
import static com.mohaeng.common.fixtures.ParticipantFixture.participantWithId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("Club(모임) 은")
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

    @Nested
    class 모임_생성_테스트 {

        @Test
        void 생성_시_모임의_기본_역할과_회장을_같이_저장한다() {
            // when
            Club club = clubWithMember(presidentMember);

            // then
            assertThat(club.clubRoles().clubRoles())
                    .extracting(ClubRole::clubRoleCategory)
                    .containsExactlyInAnyOrder(GENERAL, OFFICER, PRESIDENT);
            assertThat(club.participants().participants())
                    .extracting(Participant::clubRole)
                    .extracting(ClubRole::clubRoleCategory)
                    .containsExactly(PRESIDENT);
        }
    }

    @Nested
    class MemberId_를_통해_참가자_찾기_테스트 {

        @Test
        void memberId_를_통해_참여자를_찾을_수_있다() {
            // when
            Participant participant1 = club.findParticipantByMemberId(officerMemberId);

            // then
            assertThat(participant1.member()).isEqualTo(officer.member());
        }

        @Test
        void memberId_를_가진_참여자가_없는_경우() {
            // then
            assertThat(club.existParticipantByMemberId(100L)).isFalse();
        }
    }

    @Test
    void findPresident_는_회장을_반환한다() {
        // when
        Participant president = club.findPresident();

        // then
        assertThat(president.isPresident()).isTrue();
    }

    @Nested
    class ParticipantId_를_통해_참자가_찾기_테스트 {

        @Test
        void ParticipantId_를_통해_참여자를_찾는다() {
            // given
            final Participant president = club.findPresident();
            ReflectionTestUtils.setField(president, "id", 100L);

            // when & then
            assertThat(club.findParticipantById(100L)).isEqualTo(president);
        }

        @Test
        void 참여자가_없는_경우_예외() {
            // given
            final Club club = clubWithMember(member(1L));

            // when & then
            assertThatThrownBy(() -> club.findParticipantById(9999L));
        }
    }

    @Nested
    class roleId로_역할_찾기 {

        @Test
        void roleID를_통해_해당_역할을_조회한다() {
            // given
            ClubRoles clubRoles = club.clubRoles();
            final ClubRole role = clubRoles.clubRoles().get(0);
            ReflectionTestUtils.setField(role, "id", 1L);

            // when & then
            assertThat(club.findRoleById(1L)).isEqualTo(role);
        }

        @Test
        void 해당_Id를_가진_역할이_없는_경우_예외() {
            // when
            final BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class,
                    () -> club.findRoleById(100L)
            ).exceptionType();

            // then
            assertThat(baseExceptionType).isEqualTo(NOT_FOUND_ROLE);
        }

    }

    @Nested
    class 모임_탈퇴_테스트 {

        @Test
        void 회장이_아닌_회원은_모임에서_탈퇴할_수_있다() {
            // given
            Member target = member(10L);
            club.registerParticipant(target);
            Participant participant = club.findParticipantByMemberId(target.id());
            int before = club.currentParticipantCount();

            // when
            club.deleteParticipant(participant);

            // then
            assertThat(club.existParticipantByMemberId(target.id())).isFalse();
            assertThat(club.currentParticipantCount()).isEqualTo(before - 1);
        }

        @Test
        void 회장은_모임을_탈퇴할_수_없다() {
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

    @Test
    void findAllParticipant_는_모든_참가자를_반환한다() {
        // when
        List<Participant> allParticipant = club.findAllParticipant();

        // then
        assertThat(allParticipant.size()).isEqualTo(club.participants().participants().size());
    }

    @Nested
    class 참여자_추방_테스트 {

        @Test
        void 대상_참여자를_모임에서_추방한다() {
            // when
            club.expel(president.member().id(), officer.id());

            // then
            assertThat(club.existParticipantByMemberId(officer.member().id())).isFalse();
        }

        @Test
        void 회장이_아닌_경우_추방할_수_없다() {
            // when
            BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class, () ->
                    club.expel(officer.member().id(), general.id())
            ).exceptionType();

            // then
            assertThat(baseExceptionType).isEqualTo(NO_AUTHORITY_EXPEL_PARTICIPANT);
        }

        @Test
        void 대상_참여자와_같은_모임이_아닌_경우_추방할_수_없다() {
            // given
            Club other = clubWithMember(member(100L));

            other.registerParticipant(member(101L));
            final Participant registered = other.findParticipantByMemberId(101L);
            ReflectionTestUtils.setField(registered, "id", 100L);

            // when
            BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class, () ->
                    club.expel(president.member().id(), registered.id())
            ).exceptionType();

            // then
            assertThat(baseExceptionType).isEqualTo(NOT_FOUND_PARTICIPANT);
        }
    }

    @Nested
    class 참가자의_역할_변경_테스트 {

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
        void 회장은_참여자의_역할을_변경할_수_있다() {
            // given
            assertThat(club.findParticipantById(officerId).isManager()).isTrue();
            assertThat(club.findParticipantById(generalId).isManager()).isFalse();

            // when
            club.changeParticipantRole(presidentMemberId, officerId, generalRoleId);
            club.changeParticipantRole(presidentMemberId, generalId, officerRoleId);

            // then
            assertThat(club.findParticipantById(officerId).isManager()).isFalse();
            assertThat(club.findParticipantById(generalId).isManager()).isTrue();
        }

        @Test
        void 회장이_아닌_경우_예외가_발생한다() {
            // when
            BaseExceptionType baseExceptionType1 = assertThrows(ParticipantException.class, () ->
                    club.changeParticipantRole(officerMemberId, generalId, officerRoleId)
            ).exceptionType();
            BaseExceptionType baseExceptionType2 = assertThrows(ParticipantException.class, () ->
                    club.changeParticipantRole(generalMemberId, officerId, generalRoleId)
            ).exceptionType();

            // then
            assertThat(club.findParticipantById(officerId).isManager()).isTrue();
            assertThat(club.findParticipantById(generalId).isManager()).isFalse();
            assertThat(baseExceptionType1).isEqualTo(NO_AUTHORITY_CHANGE_PARTICIPANT_ROLE);
            assertThat(baseExceptionType2).isEqualTo(NO_AUTHORITY_CHANGE_PARTICIPANT_ROLE);
        }

        @Test
        void 회장_역할로_변경하려는_경우_예외가_발생한다() {
            // when
            BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class, () ->
                    club.changeParticipantRole(presidentMemberId, generalId, presidentRoleId)
            ).exceptionType();

            // then
            assertThat(club.findParticipantById(officerId).isPresident()).isFalse();
            assertThat(baseExceptionType).isEqualTo(NOT_CHANGE_PRESIDENT_ROLE);
        }

        @Test
        void 대상이_존재하지_않는_경우_예외가_발생한다() {
            // when
            BaseExceptionType baseExceptionType1 = assertThrows(ParticipantException.class, () ->
                    club.changeParticipantRole(presidentMemberId + 100L, generalId, officerRoleId)
            ).exceptionType();
            BaseExceptionType baseExceptionType2 = assertThrows(ParticipantException.class, () ->
                    club.changeParticipantRole(presidentMemberId, generalId + 100L, officerRoleId)
            ).exceptionType();

            // then
            assertThat(club.findParticipantById(generalId).isManager()).isFalse();
            assertThat(baseExceptionType1).isEqualTo(NOT_FOUND_PARTICIPANT);
            assertThat(baseExceptionType2).isEqualTo(NOT_FOUND_PARTICIPANT);
        }

        @Test
        void 역할이_없는_경우_혹은_다른_모임의_역할인_경우_예외가_발생한다() {
            // when
            BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class, () ->
                    club.changeParticipantRole(presidentMemberId, generalId, officerRoleId + 100L)
            ).exceptionType();

            // then
            assertThat(baseExceptionType).isEqualTo(NOT_FOUND_ROLE);
        }
    }

    @Nested
    class 역할_생성_테스트 {

        @Test
        void 회장과_임원은_모임_역할을_생성할_수_있다() {
            // given
            String name1 = "새로 생성할 역할 이름1";
            String name2 = "새로 생성할 역할 이름2";

            // when
            ClubRole role1 = club.createRole(officerMemberId, name1, OFFICER);
            ClubRole role2 = club.createRole(presidentMemberId, name2, GENERAL);

            // then
            assertThat(club.clubRoles().clubRoles())
                    .contains(role1, role2);
        }

        @Test
        void 새로_생성된_역할은_기본_역할이_아니다() {
            // when
            ClubRole role = club.createRole(officerMemberId, "새로생성", OFFICER);

            // then
            assertThat(role.isDefault()).isFalse();
        }

        @Test
        void 일반_회원은_모임_역할을_생성할_수_없다() {
            // when
            BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class,
                    () -> club.createRole(generalMemberId, "새로 생성할 이름", GENERAL)
            ).exceptionType();

            // then
            assertThat(baseExceptionType).isEqualTo(NO_AUTHORITY_CREATE_ROLE);
        }

        @Test
        void 회장_역할을_새로_생성할_수_없다() {
            // when
            BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class,
                    () -> club.createRole(presidentMemberId, "새로 생성할 이름", PRESIDENT)
            ).exceptionType();

            // then
            assertThat(baseExceptionType).isEqualTo(CAN_NOT_CREATE_PRESIDENT_ROLE);
        }

        @Test
        void 역할의_이름은_모임_내에서_중복될_수_없다() {
            // given
            String duplicatedName = "중복";
            club.createRole(presidentMemberId, duplicatedName, OFFICER);

            // when
            BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class,
                    () -> club.createRole(presidentMemberId, duplicatedName, GENERAL)
            ).exceptionType();

            // then
            assertThat(baseExceptionType).isEqualTo(DUPLICATED_NAME);
        }
    }

    @Nested
    class 역할_이름_변경_테스트 {

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
        void 역할_이름을_변경한다() {
            // given
            String 변경이름 = "변경이름";

            // when
            club.changeRoleName(presidentMemberId, presidentRoleId, 변경이름);

            // then
            assertThat(club.clubRoles().findById(presidentRoleId).name())
                    .isEqualTo(변경이름);
        }

        @Test
        void 역할_이름_변경_시_변경될_이름이_중복되는_경우_예외가_발생한다() {
            // given
            String 중복_이름 = club.findDefaultRoleByCategory(OFFICER).name();

            // when
            BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class, () ->
                    club.changeRoleName(presidentMemberId, presidentRoleId, 중복_이름)
            ).exceptionType();

            // then
            assertThat(baseExceptionType).isEqualTo(DUPLICATED_NAME);
        }

        @Test
        void 일반_회원은_역할_이름을_변경할_수_없다() {
            // when
            BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class, () ->
                    club.changeRoleName(generalMemberId, generalRoleId, "변경이름")
            ).exceptionType();

            // then
            assertThat(baseExceptionType).isEqualTo(NO_AUTHORITY_CHANGE_ROLE_NAME);
        }

        @Test
        void 임원은_일반_역할_이름만을_변경할_수_있다() {
            // given
            String 변경이름 = "변경이름";

            // when
            club.changeRoleName(officerMemberId, generalRoleId, 변경이름);

            // then
            assertThat(club.clubRoles().findById(generalRoleId).name()).isEqualTo(변경이름);
        }

        @Test
        void 임원이_일반_역할이_아닌_역할의_이름을_변경하려는_경우_예외() {
            // given
            final List<Long> targetIds = List.of(presidentRoleId, officerRoleId);

            // when
            for (final Long targetId : targetIds) {
                BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class, () ->
                        club.changeRoleName(officerMemberId, targetId, "변경이름")
                ).exceptionType();
                // then
                assertThat(baseExceptionType).isEqualTo(NO_AUTHORITY_CHANGE_ROLE_NAME);
            }

        }

        @Test
        void 회장은_모든_역할의_이름을_다_변경할_수_있다() {
            // given
            final List<Long> targetIds = List.of(presidentRoleId, officerRoleId, generalRoleId);

            // when
            for (final Long targetId : targetIds) {
                club.changeRoleName(presidentMemberId, targetId, "이름" + targetId);
                // then
                assertThat(club.clubRoles().findById(targetId).name()).isEqualTo("이름" + targetId);
            }
        }

        @Test
        void 회원을_찾을_수_없는_경우_예외가_발생한다() {
            // when
            BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class, () ->
                    club.changeRoleName(100000L, presidentRoleId, "변경이름")
            ).exceptionType();

            // then
            assertThat(baseExceptionType).isEqualTo(NOT_FOUND_PARTICIPANT);
        }

        @Test
        void 바꿀_역할을_찾을_수_없는_경우_예외가_발생한다() {
            // when
            BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class, () ->
                    club.changeRoleName(presidentMemberId, 10000L, "변경이름")
            ).exceptionType();

            // then
            assertThat(baseExceptionType).isEqualTo(NOT_FOUND_ROLE);
        }
    }

    @Nested
    class 역할_제거_테스트 {

        private final ClubRole 새로생성된_임원_역할1 = club.createRole(presidentMemberId, "새로생성된임원역할1", OFFICER);
        private final ClubRole 새로생성된_임원_역할2 = club.createRole(presidentMemberId, "새로생성된임원역할2", OFFICER);
        private final ClubRole 새로생성된_일반_역할1 = club.createRole(presidentMemberId, "새로생성된일반역할1", GENERAL);
        private final ClubRole 새로생성된_일반_역할2 = club.createRole(presidentMemberId, "새로생성된일반역할2", GENERAL);
        private final List<ClubRole> 새로생성된_역할들 = List.of(
                새로생성된_임원_역할1,
                새로생성된_임원_역할2,
                새로생성된_일반_역할1,
                새로생성된_일반_역할2
        );

        @BeforeEach
        void init() {
            for (int i = 0; i < club.clubRoles().clubRoles().size(); i++) {
                ReflectionTestUtils.setField(club.clubRoles().clubRoles().get(i), "id", (long) i + 100L);
            }
        }

        @Test
        void 회장과_임원진은_기본_역할이_아닌_역할을_제거할_수_있다() {
            // when
            for (final ClubRole role : 새로생성된_역할들) {
                club.deleteRole(presidentMemberId, role.id());

                // then
                final BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class,
                        () -> club.clubRoles().findById(새로생성된_임원_역할1.id())
                ).exceptionType();
                assertThat(baseExceptionType).isEqualTo(NOT_FOUND_ROLE);
            }
        }

        @ParameterizedTest(name = "기본 역할은 제거할 수 없다")
        @EnumSource(mode = EXCLUDE)
        void 기본_역할을_제거하려는_경우_예외(final ClubRoleCategory category) {
            // given
            ClubRole defaultRoleByCategory = club.findDefaultRoleByCategory(category);

            // when
            BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class, () ->
                    club.deleteRole(presidentMemberId, defaultRoleByCategory.id())
            ).exceptionType();

            // then
            assertThat(baseExceptionType).isEqualTo(CAN_NOT_DELETE_DEFAULT_ROLE);
        }

        @Test
        void 일반_회원이_역할을_제거하려는_경우_예외() {
            for (final ClubRole role : 새로생성된_역할들) {
                // when
                BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class,
                        () -> club.deleteRole(generalMemberId, role.id())
                ).exceptionType();

                // then
                assertThat(baseExceptionType).isEqualTo(NO_AUTHORITY_DELETE_ROLE);
            }
        }

        @Test
        void 제거되는_역할을_가진_참여자들은_해당_분야의_기본_역할로_변경된다() {
            // given
            List<Participant> generalParticipants = new ArrayList<>();
            generalParticipants.addAll(saveParticipantsWithRole(새로생성된_일반_역할1));
            generalParticipants.addAll(saveParticipantsWithRole(새로생성된_일반_역할2));
            ClubRole generalDefaultRole = club.findDefaultRoleByCategory(GENERAL);

            // when
            club.deleteRole(presidentMemberId, 새로생성된_일반_역할1.id());
            club.deleteRole(presidentMemberId, 새로생성된_일반_역할2.id());

            // then
            generalParticipants.forEach(it -> assertThat(it.clubRole())
                    .isEqualTo(generalDefaultRole));
        }

        private static long uniqueId = 1000L;

        private static long uniqueId() {
            return uniqueId++;
        }

        private List<Participant> saveParticipantsWithRole(final ClubRole clubRole) {
            List<Long> ids = List.of(uniqueId(), uniqueId());
            return ids.stream()
                    .map(it -> {
                                club.registerParticipant(member(it));
                                Participant participant = club.findParticipantByMemberId(it);
                                ReflectionTestUtils.setField(participant, "id", it);
                                club.changeParticipantRole(presidentMemberId, it, clubRole.id());
                                return participant;
                            }
                    ).toList();
        }
    }

    @Nested
    class 모임의_기본_역할_변경_테스트 {

        private final ClubRole 임원역할 = club.clubRoles().add(club, "임원역할", OFFICER);
        private final ClubRole 일반역할 = club.clubRoles().add(club, "일반역할", GENERAL);

        @BeforeEach
        void init() {
            for (int i = 0; i < club.clubRoles().clubRoles().size(); i++) {
                ReflectionTestUtils.setField(club.clubRoles().clubRoles().get(i), "id", (long) i + 100L);
            }
        }

        @Test
        void 회장과_임원진은_기본_역할을_변경할_수_있다() {
            // when
            club.changeDefaultRole(officer.member().id(), 임원역할.id());
            club.changeDefaultRole(presidentMember.id(), 일반역할.id());

            // then
            assertAll(
                    () -> assertThat(club.findDefaultRoleByCategory(OFFICER)).isEqualTo(임원역할),
                    () -> assertThat(club.findDefaultRoleByCategory(GENERAL)).isEqualTo(일반역할)
            );
        }

        @Test
        void 기본_역할_변경_시_기존_기본_역할은_기본_역할이_아니게_된다() {
            // given
            ClubRole originalDefaultOfficer = club.findDefaultRoleByCategory(OFFICER);

            // when
            회장과_임원진은_기본_역할을_변경할_수_있다();

            // then
            assertThat(originalDefaultOfficer.isDefault()).isFalse();
        }

        @Test
        void 일반_회원은_기본_역할을_변경할_수_없다() {
            // given
            ClubRole originalDefaultGeneral = club.findDefaultRoleByCategory(GENERAL);

            // when
            BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class, () ->
                    club.changeDefaultRole(general.member().id(), 일반역할.id())
            ).exceptionType();

            // then
            assertAll(
                    () -> assertThat(baseExceptionType).isEqualTo(NO_AUTHORITY_CHANGE_DEFAULT_ROLE),
                    () -> assertThat(baseExceptionType).isEqualTo(NO_AUTHORITY_CHANGE_DEFAULT_ROLE),
                    () -> assertThat(originalDefaultGeneral.isDefault()).isTrue(),
                    () -> assertThat(club.findDefaultRoleByCategory(OFFICER)).isNotEqualTo(일반역할),
                    () -> assertThat(일반역할.isDefault()).isFalse()
            );
        }
    }

    @Nested
    class 회장_위임_테스트 {

        @Test
        void 회장은_임임의_회원을_차기_회장으로_위임할_수_있다() {
            // when
            club.delegatePresident(president.member().id(), general.id());

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
                club.delegatePresident(officer.member().id(), general.id());
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
                club.delegatePresident(10000L, general.id());
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
                club.delegatePresident(president.member().id(), 10000L);
            }).exceptionType();

            // then
            assertAll(
                    () -> assertThat(baseExceptionType).isEqualTo(NOT_FOUND_PARTICIPANT),
                    () -> assertThat(president.clubRole().clubRoleCategory()).isEqualTo(PRESIDENT),
                    () -> assertThat(general.clubRole().clubRoleCategory()).isEqualTo(GENERAL)
            );
        }
    }
}

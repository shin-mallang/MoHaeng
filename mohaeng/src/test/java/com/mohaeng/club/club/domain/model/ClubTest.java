package com.mohaeng.club.club.domain.model;

import com.mohaeng.club.club.exception.ClubRoleException;
import com.mohaeng.club.club.exception.ClubRoleExceptionType;
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

    @Test
    void memberId_를_통해_참여자를_찾을_수_있다() {
        // when
        Participant participant1 = club.findParticipantByMemberId(officerMemberId);
        Participant participant2 = club.findParticipantByMemberId(generalMemberId);

        // then
        assertThat(participant1.member()).isEqualTo(officer.member());
        assertThat(participant2.member()).isEqualTo(general.member());
    }

    @Test
    void memberId_를_가진_참여자가_없는_경우() {
        // then
        assertThat(club.existParticipantByMemberId(100L)).isFalse();
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
        assertThat(club.findParticipantById(1L)).isEqualTo(president);
        assertThatThrownBy(() -> club.findParticipantById(9999L));
    }

    @Test
    void findRoleById는_Role의_ID를_통해_해당_역할을_조회한다() {
        // given
        ClubRoles clubRoles = club.clubRoles();
        for (int i = 1; i <= clubRoles.clubRoles().size(); i++) {
            ReflectionTestUtils.setField(clubRoles.clubRoles().get(i - 1), "id", (long) i);
        }

        // when & then
        assertThat(club.findRoleById(1L).id()).isNotNull();
        assertThat(club.findRoleById(2L).id()).isNotNull();
        assertThat(club.findRoleById(3L).id()).isNotNull();
        assertThatThrownBy(() -> club.findRoleById(4L));
    }

    @Test
    void deleteParticipant_는_회원을_모임에서_제거한다() {
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
            assertThat(club.existParticipantByMemberId(officer.member().id())).isFalse();
            assertThat(club.existParticipantByMemberId(general.member().id())).isFalse();
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
            assertThat(club.existParticipantByMemberId(general.member().id())).isTrue();
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
            Participant otherGeneral = other.findParticipantByMemberId(generalId);

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
    @DisplayName("참가자의 역할 변경(changeParticipantRole)")
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
        void changeParticipantRole_시_회장이_아닌_경우_예외가_발생한다() {
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
        void changeParticipantRole_시_회장으로_변경하려는_경우_예외가_발생한다() {
            // when
            BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class, () ->
                    club.changeParticipantRole(presidentMemberId, generalId, presidentRoleId)
            ).exceptionType();

            // then
            assertThat(club.findParticipantById(officerId).isPresident()).isFalse();
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
            assertThat(club.findParticipantById(generalId).isManager()).isFalse();
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

    @Nested
    @DisplayName("역할 생성(createRole)")
    class CreateRole {

        @Test
        void 회장과_임원은_모임_역할을_생성할_수_있다() {
            // given
            String name1 = "새로 생성할 역할 이름1";
            String name2 = "새로 생성할 역할 이름2";

            // when
            ClubRole role1 = club.createRole(officerMemberId, name1, OFFICER);
            ClubRole role2 = club.createRole(presidentMemberId, name2, GENERAL);

            // then
            assertThat(club.clubRoles().clubRoles()).contains(role1, role2);
        }

        @Test
        void 새로_생성된_역할을_모두_기본_역할이_아니다() {
            // given
            String name1 = "새로 생성할 역할 이름1";
            String name2 = "새로 생성할 역할 이름2";

            // when
            ClubRole role1 = club.createRole(officerMemberId, name1, OFFICER);
            ClubRole role2 = club.createRole(presidentMemberId, name2, GENERAL);

            // then
            assertThat(role1.isDefault()).isFalse();
            assertThat(role2.isDefault()).isFalse();
        }

        @Test
        void 일반_회원은_모임_역할을_생성할_수_없다() {
            // given
            int before = club.clubRoles().clubRoles().size();

            // when
            BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class,
                    () -> club.createRole(generalMemberId, "새로 생성할 이름", GENERAL)
            ).exceptionType();

            // then
            assertThat(club.clubRoles().clubRoles().size()).isEqualTo(before);
            assertThat(baseExceptionType).isEqualTo(ClubRoleExceptionType.NO_AUTHORITY_CREATE_ROLE);
        }

        @Test
        void 회장_역할을_새로_생성할_수_없다() {
            // given
            int before = club.clubRoles().clubRoles().size();

            // when
            BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class,
                    () -> club.createRole(presidentMemberId, "새로 생성할 이름", PRESIDENT)
            ).exceptionType();

            // then
            assertThat(club.clubRoles().clubRoles().size()).isEqualTo(before);
            assertThat(baseExceptionType).isEqualTo(ClubRoleExceptionType.CAN_NOT_CREATE_PRESIDENT_ROLE);
        }

        @Test
        void 역할의_이름은_모임_내에서_중복될_수_없다() {
            // given
            String duplicatedName = "중복";
            club.createRole(presidentMemberId, duplicatedName, OFFICER);
            int before = club.clubRoles().clubRoles().size();

            // when
            BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class,
                    () -> club.createRole(presidentMemberId, duplicatedName, GENERAL)
            ).exceptionType();

            // then
            assertThat(club.clubRoles().clubRoles().size()).isEqualTo(before);
            assertThat(baseExceptionType).isEqualTo(ClubRoleExceptionType.DUPLICATED_NAME);
        }
    }

    @Nested
    @DisplayName("역할 이름 변경(changeRoleName) 테스트")
    class ChangeRoleNameTest {

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
            assertThat(club.clubRoles().findById(presidentRoleId).get().name())
                    .isEqualTo(변경이름);
        }

        @Test
        void 역할_이름_변경_시_변경될_이름이_중복되는_경우_예외가_발생한다() {
            // given
            String 변경이름 = club.findDefaultRoleByCategory(OFFICER).name();

            // when
            BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class, () ->
                    club.changeRoleName(presidentMemberId, presidentRoleId, 변경이름)
            ).exceptionType();

            // then
            assertThat(baseExceptionType).isEqualTo(DUPLICATED_NAME);
            assertThat(club.clubRoles().findById(presidentRoleId).get().name())
                    .isNotEqualTo(변경이름);
        }

        @Test
        void 일반_회원은_역할_이름을_변경할_수_없다() {
            // given
            String 변경이름 = "변경이름";

            // when
            BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class, () ->
                    club.changeRoleName(generalMemberId, generalRoleId, 변경이름)
            ).exceptionType();

            // then
            assertThat(baseExceptionType).isEqualTo(NO_AUTHORITY_CHANGE_ROLE_NAME);
            assertThat(club.clubRoles().findById(generalRoleId).get().name())
                    .isNotEqualTo(변경이름);
        }

        @Test
        void 임원은_일반_역할만을_변경할_수_있다() {
            // given
            String 변경이름1 = "변경이름1";
            String 변경이름2 = "변경이름2";
            String 변경이름3 = "변경이름3";

            // when
            BaseExceptionType baseExceptionType1 = assertThrows(ClubRoleException.class, () ->
                    club.changeRoleName(officerMemberId, presidentRoleId, 변경이름1)
            ).exceptionType();
            BaseExceptionType baseExceptionType2 = assertThrows(ClubRoleException.class, () ->
                    club.changeRoleName(officerMemberId, officerRoleId, 변경이름2)
            ).exceptionType();
            club.changeRoleName(officerMemberId, generalRoleId, 변경이름3);

            // then
            assertThat(baseExceptionType1).isEqualTo(NO_AUTHORITY_CHANGE_ROLE_NAME);
            assertThat(baseExceptionType2).isEqualTo(NO_AUTHORITY_CHANGE_ROLE_NAME);
            assertThat(club.clubRoles().findById(presidentRoleId).get().name()).isNotEqualTo(변경이름1);
            assertThat(club.clubRoles().findById(officerRoleId).get().name()).isNotEqualTo(변경이름2);
            assertThat(club.clubRoles().findById(generalRoleId).get().name()).isEqualTo(변경이름3);
        }

        @Test
        void 회장은_모든_역할의_이름을_다_변경할_수_있다() {
            // given
            String 변경이름1 = "변경이름1";
            String 변경이름2 = "변경이름2";
            String 변경이름3 = "변경이름3";

            // when
            club.changeRoleName(presidentMemberId, presidentRoleId, 변경이름1);
            club.changeRoleName(presidentMemberId, officerRoleId, 변경이름2);
            club.changeRoleName(presidentMemberId, generalRoleId, 변경이름3);

            // then
            assertThat(club.clubRoles().findById(presidentRoleId).get().name()).isEqualTo(변경이름1);
            assertThat(club.clubRoles().findById(officerRoleId).get().name()).isEqualTo(변경이름2);
            assertThat(club.clubRoles().findById(generalRoleId).get().name()).isEqualTo(변경이름3);
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
    @DisplayName("역할 제거(deleteRole) 테스트")
    class DeleteRoleTest {

        ClubRole 새로생성된_임원_역할1;
        ClubRole 새로생성된_임원_역할2;
        ClubRole 새로생성된_일반_역할1;
        ClubRole 새로생성된_일반_역할2;

        @BeforeEach
        void init() {
            새로생성된_임원_역할1 = club.createRole(presidentMemberId, "새로생성된임원역할1", OFFICER);
            새로생성된_임원_역할2 = club.createRole(presidentMemberId, "새로생성된임원역할2", OFFICER);
            새로생성된_일반_역할1 = club.createRole(presidentMemberId, "새로생성된일반역할1", GENERAL);
            새로생성된_일반_역할2 = club.createRole(presidentMemberId, "새로생성된일반역할2", GENERAL);
            for (int i = 0; i < club.clubRoles().clubRoles().size(); i++) {
                ReflectionTestUtils.setField(club.clubRoles().clubRoles().get(i), "id", (long) i + 100L);
            }
        }

        @Test
        void 회장과_임원진은_기본_역할이_아닌_역할을_제거할_수_있다() {
            // when
            club.deleteRole(presidentMemberId, 새로생성된_임원_역할1.id());
            club.deleteRole(officerMemberId, 새로생성된_임원_역할2.id());
            club.deleteRole(presidentMemberId, 새로생성된_일반_역할1.id());
            club.deleteRole(officerMemberId, 새로생성된_일반_역할2.id());

            // then
            assertAll(
                    () -> assertThat(club.clubRoles().findById(새로생성된_임원_역할1.id())).isEmpty(),
                    () -> assertThat(club.clubRoles().findById(새로생성된_임원_역할2.id())).isEmpty(),
                    () -> assertThat(club.clubRoles().findById(새로생성된_일반_역할1.id())).isEmpty(),
                    () -> assertThat(club.clubRoles().findById(새로생성된_일반_역할2.id())).isEmpty()
            );
        }

        @ParameterizedTest(name = "기본 역할은 제거할 수 없다")
        @EnumSource(mode = EnumSource.Mode.EXCLUDE)
        void 기본_역할은_제거할_수_없다(final ClubRoleCategory category) {
            // given
            ClubRole defaultRoleByCategory = club.findDefaultRoleByCategory(category);

            // when
            BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class, () ->
                    club.deleteRole(presidentMemberId, defaultRoleByCategory.id())
            ).exceptionType();

            // then
            assertAll(
                    () -> assertThat(baseExceptionType).isEqualTo(CAN_NOT_DELETE_DEFAULT_ROLE),
                    () -> assertThat(club.clubRoles().clubRoles()).contains(defaultRoleByCategory)
            );
        }

        @Test
        void 일반_회원을_역할을_제거할_수_없다() {
            // given
            List<ClubRole> clubRoles = List.of(새로생성된_일반_역할1, 새로생성된_일반_역할2, 새로생성된_임원_역할1, 새로생성된_임원_역할2);

            // when & then
            clubRoles.forEach(it -> {
                BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class, () ->
                        club.deleteRole(generalMemberId, it.id())
                ).exceptionType();
                assertAll(
                        () -> assertThat(baseExceptionType).isEqualTo(NO_AUTHORITY_DELETE_ROLE),
                        () -> assertThat(club.clubRoles().findById(it.id())).isPresent()
                );
            });
        }

        @Test
        void 제거되는_역할을_가진_참여자들은_해당_분야의_기본_역할로_변경된다() {
            // given
            List<Participant> participants1 = saveParticipantWithRole(새로생성된_일반_역할1);
            List<Participant> participants2 = saveParticipantWithRole(새로생성된_일반_역할2);
            List<Participant> participants3 = saveParticipantWithRole(새로생성된_임원_역할1);
            List<Participant> participants4 = saveParticipantWithRole(새로생성된_임원_역할2);

            List<Participant> generalParticipants = new ArrayList<>();
            generalParticipants.addAll(participants1);
            generalParticipants.addAll(participants2);
            List<Participant> officerParticipants = new ArrayList<>();
            officerParticipants.addAll(participants3);
            officerParticipants.addAll(participants4);
            ClubRole generalDefaultRole = club.findDefaultRoleByCategory(GENERAL);
            ClubRole officerDefaultRole = club.findDefaultRoleByCategory(OFFICER);

            // when
            club.deleteRole(presidentMemberId, 새로생성된_일반_역할1.id());
            club.deleteRole(presidentMemberId, 새로생성된_일반_역할2.id());
            club.deleteRole(presidentMemberId, 새로생성된_임원_역할1.id());
            club.deleteRole(presidentMemberId, 새로생성된_임원_역할2.id());

            // then
            assertAll(
                    () -> assertThat(club.clubRoles().findById(새로생성된_일반_역할1.id())).isEmpty(),
                    () -> assertThat(club.clubRoles().findById(새로생성된_일반_역할2.id())).isEmpty(),
                    () -> assertThat(club.clubRoles().findById(새로생성된_임원_역할1.id())).isEmpty(),
                    () -> assertThat(club.clubRoles().findById(새로생성된_임원_역할2.id())).isEmpty()
            );
            generalParticipants.forEach(it -> assertThat(it.clubRole()).isEqualTo(generalDefaultRole));
            officerParticipants.forEach(it -> assertThat(it.clubRole()).isEqualTo(officerDefaultRole));
        }

        private static long uniqueId = 1000L;

        private static long uniqueId() {
            return uniqueId++;
        }

        private List<Participant> saveParticipantWithRole(final ClubRole clubRole) {
            List<Long> ids = List.of(uniqueId(), uniqueId());
            return ids.stream().map(it -> {
                club.registerParticipant(member(it));
                Participant participant = club.findParticipantByMemberId(it);
                ReflectionTestUtils.setField(participant, "id", it);
                club.changeParticipantRole(presidentMemberId, it, clubRole.id());
                return participant;
            }).toList();
        }
    }

    @Nested
    @DisplayName("기본 역할 변경(changeDefaultRole) 테스트")
    class ChangeDefaultRoleTest {

        ClubRole 임원역할;
        ClubRole 일반역할;

        @BeforeEach
        void init() {
            임원역할 = club.clubRoles().add(club, "임원역할", OFFICER);
            일반역할 = club.clubRoles().add(club, "일반역할", GENERAL);
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
            ClubRole originalDefaultGeneral = club.findDefaultRoleByCategory(GENERAL);

            // when
            회장과_임원진은_기본_역할을_변경할_수_있다();

            // then
            assertAll(
                    () -> assertThat(originalDefaultOfficer.isDefault()).isFalse(),
                    () -> assertThat(originalDefaultGeneral.isDefault()).isFalse()
            );
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
    @DisplayName("회장 위임(delegatePresident) 테스트")
    class DelegatePresident {

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

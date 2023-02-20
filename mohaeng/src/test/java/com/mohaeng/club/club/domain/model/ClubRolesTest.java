package com.mohaeng.club.club.domain.model;

import com.mohaeng.club.club.exception.ClubRoleException;
import com.mohaeng.common.exception.BaseExceptionType;
import org.junit.jupiter.api.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mohaeng.club.club.domain.model.ClubRoleCategory.*;
import static com.mohaeng.club.club.exception.ClubRoleExceptionType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("ClubRoles 은")
class ClubRolesTest {

    private final Club club = mock(Club.class);
    private final ClubRoles clubRoles = ClubRoles.defaultRoles(club);

    @Test
    void defaultRoles_는_역할의_카테고리별_기본_역할_1개씩을_각각_생성하여_반환한다() {
        // then
        assertThat(clubRoles.clubRoles().stream().map(ClubRole::clubRoleCategory).toList())
                .containsExactlyInAnyOrderElementsOf(List.of(PRESIDENT, OFFICER, GENERAL));
        clubRoles.clubRoles().forEach(it -> assertThat(it.isDefault()).isTrue());
    }

    @Test
    void findDefaultRoleByCategory_는_주어진_카테고리에_해당하는_역할들_중_기본_역할을_반환한다() {
        // when & then
        assertThat(clubRoles.findDefaultRoleByCategory(PRESIDENT).clubRoleCategory()).isEqualTo(PRESIDENT);
        assertThat(clubRoles.findDefaultRoleByCategory(OFFICER).clubRoleCategory()).isEqualTo(OFFICER);
        assertThat(clubRoles.findDefaultRoleByCategory(GENERAL).clubRoleCategory()).isEqualTo(GENERAL);
    }

    @Test
    void findById_는_id를_통해_역할을_조회한다() {
        // given
        for (int i = 1; i <= clubRoles.clubRoles().size(); i++) {
            ReflectionTestUtils.setField(clubRoles.clubRoles().get(i - 1), "id", (long) i);
        }

        // when
        assertThat(clubRoles.findById(1L)).isPresent();
        assertThat(clubRoles.findById(2L)).isPresent();
        assertThat(clubRoles.findById(3L)).isPresent();
        assertThat(clubRoles.findById(4L)).isEmpty();
    }

    @Nested
    @DisplayName("역할 추가(add) 테스트")
    class AddRole {

        @Test
        void add_시_기본_역할이_아닌_역할을_생성하여_저장한다() {
            // when
            ClubRole 일반역할 = clubRoles.add(club, "새로생성할역할1", GENERAL);
            ClubRole 임원역할 = clubRoles.add(club, "새로생성할역할2", OFFICER);

            // then
            assertThat(clubRoles.clubRoles()).contains(일반역할, 임원역할);
            assertThat(일반역할.isDefault()).isFalse();
            assertThat(임원역할.isDefault()).isFalse();
        }

        @Test
        void add_시_역할_이름이_중복되면_오류가_발생한다() {
            // given
            String duplicatedName = "중복이름";
            clubRoles.add(club, duplicatedName, GENERAL);
            int size = clubRoles.clubRoles().size();

            // when
            BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class, () ->
                    clubRoles.add(club, duplicatedName, OFFICER)
            ).exceptionType();

            // then
            assertThat(baseExceptionType).isEqualTo(DUPLICATED_NAME);
            assertThat(clubRoles.clubRoles().size()).isEqualTo(size);
        }

        @Test
        void add_시_회장_역할을_추가하려는_경우_예외가_발생한다() {
            // given
            int size = clubRoles.clubRoles().size();

            // when
            BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class, () ->
                    clubRoles.add(club, "something", PRESIDENT)
            ).exceptionType();

            // then
            assertThat(baseExceptionType).isEqualTo(CAN_NOT_CREATE_PRESIDENT_ROLE);
            assertThat(clubRoles.clubRoles().size()).isEqualTo(size);
        }
    }

    @Nested
    @DisplayName("역할 이름 변경(changeRoleName) 테스트")
    class ChangeRoleNameTest {

        private Map<ClubRoleCategory, ClubRole> clubRoleMap;

        @BeforeEach
        void init() {
            for (int i = 1; i <= clubRoles.clubRoles().size(); i++) {
                ReflectionTestUtils.setField(clubRoles.clubRoles().get(i - 1), "id", (long) i);
            }
            clubRoleMap = clubRoles.clubRoles()
                    .stream()
                    .collect(Collectors.toMap(ClubRole::clubRoleCategory, it -> it));
        }

        @Test
        void 역할_이름을_변경한다() {
            // given
            ClubRole role = clubRoleMap.get(PRESIDENT);
            String changed = "바꿈";

            // when
            clubRoles.changeRoleName(PRESIDENT, role.id(), changed);

            // then
            assertThat(role.name()).isEqualTo(changed);
        }

        @Test
        void 역할_이름_변경_시_변경될_이름이_중복되는_경우_예외가_발생한다() {
            // given
            ClubRole role1 = clubRoleMap.get(PRESIDENT);
            ClubRole role2 = clubRoleMap.get(OFFICER);
            String duplicated = "중복";
            clubRoles.changeRoleName(PRESIDENT, role1.id(), duplicated);

            // when
            BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class, () ->
                    clubRoles.changeRoleName(PRESIDENT, role2.id(), duplicated)
            ).exceptionType();

            // then
            assertThat(role2.name()).isNotEqualTo(duplicated);
            assertThat(baseExceptionType).isEqualTo(DUPLICATED_NAME);
        }

        @Test
        void 일반_회원은_역할_이름을_변경할_수_없다() {
            // given
            ClubRole role = clubRoleMap.get(GENERAL);
            String name = "이름입니당";

            // when
            BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class, () ->
                    clubRoles.changeRoleName(GENERAL, role.id(), name)
            ).exceptionType();

            // then
            assertThat(role.name()).isNotEqualTo(name);
            assertThat(baseExceptionType).isEqualTo(NO_AUTHORITY_CHANGE_ROLE_NAME);
        }

        @Test
        void 임원은_일반_역할만을_변경할_수_있다() {
            // given
            ClubRole generalRole = clubRoleMap.get(GENERAL);
            ClubRole officerRole = clubRoleMap.get(OFFICER);
            ClubRole presidentRole = clubRoleMap.get(PRESIDENT);
            String name = "이름입니당";

            // when
            clubRoles.changeRoleName(OFFICER, generalRole.id(), name);
            BaseExceptionType baseExceptionType1 = assertThrows(ClubRoleException.class, () ->
                    clubRoles.changeRoleName(OFFICER, officerRole.id(), name)
            ).exceptionType();
            BaseExceptionType baseExceptionType2 = assertThrows(ClubRoleException.class, () ->
                    clubRoles.changeRoleName(OFFICER, presidentRole.id(), name)
            ).exceptionType();

            // then
            assertThat(generalRole.name()).isEqualTo(name);
            assertThat(baseExceptionType1).isEqualTo(NO_AUTHORITY_CHANGE_ROLE_NAME);
            assertThat(baseExceptionType2).isEqualTo(NO_AUTHORITY_CHANGE_ROLE_NAME);
        }

        @Test
        void 회장은_모든_역할의_이름을_다_변경할_수_있다() {
            // given
            ClubRole generalRole = clubRoleMap.get(GENERAL);
            ClubRole officerRole = clubRoleMap.get(OFFICER);
            ClubRole presidentRole = clubRoleMap.get(PRESIDENT);
            String name1 = "이름입니당1";
            String name2 = "이름입니당2";
            String name3 = "이름입니당3";

            // when
            clubRoles.changeRoleName(PRESIDENT, generalRole.id(), name1);
            clubRoles.changeRoleName(PRESIDENT, officerRole.id(), name2);
            clubRoles.changeRoleName(PRESIDENT, presidentRole.id(), name3);

            // then
            assertThat(generalRole.name()).isEqualTo(name1);
            assertThat(officerRole.name()).isEqualTo(name2);
            assertThat(presidentRole.name()).isEqualTo(name3);
        }

        @Test
        void 바꿀_역할을_찾을_수_없는_경우_예외가_발생한다() {
            // when
            BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class, () ->
                    clubRoles.changeRoleName(PRESIDENT, 1000000L, "임시이름")
            ).exceptionType();

            // then
            assertThat(baseExceptionType)
                    .isEqualTo(NOT_FOUND_ROLE);
        }
    }
}
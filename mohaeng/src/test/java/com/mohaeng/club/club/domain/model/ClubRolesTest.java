package com.mohaeng.club.club.domain.model;

import com.mohaeng.club.club.exception.ClubRoleException;
import com.mohaeng.common.exception.BaseExceptionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mohaeng.club.club.domain.model.ClubRoleCategory.*;
import static com.mohaeng.club.club.exception.ClubRoleExceptionType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;
import static org.mockito.Mockito.mock;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("ClubRoles(모임 역할들) 은")
class ClubRolesTest {

    private final Club club = mock(Club.class);
    private final ClubRoles clubRoles = ClubRoles.defaultRoles(club);

    @Test
    void 기본_역할들을_가지고_생성된다() {
        // when
        final ClubRoles clubRoles = ClubRoles.defaultRoles(club);

        // then
        assertThat(clubRoles.clubRoles())
                .extracting(ClubRole::clubRoleCategory)
                .containsExactlyInAnyOrder(ClubRoleCategory.values());
        assertThat(clubRoles.clubRoles())
                .extracting(ClubRole::isDefault)
                .containsOnly(true);
    }

    @ParameterizedTest(name = "{0} 카테고리에 속하는 기본 역할을 반환할 수 있다")
    @EnumSource(mode = EXCLUDE, value = ClubRoleCategory.class)
    void 카테고리에_속하는_기본_역할을_반환할_수_있다(final ClubRoleCategory category) {
        // given
        final ClubRoles clubRoles = ClubRoles.defaultRoles(club);

        // when
        final ClubRole defaultRoleByCategory = clubRoles.findDefaultRoleByCategory(category);

        // then
        assertThat(defaultRoleByCategory.clubRoleCategory()).isEqualTo(category);
        assertThat(defaultRoleByCategory.isDefault()).isTrue();
    }

    @Nested
    class ID_를_통한_조회_테스트 {

        private final ClubRoles clubRoles = ClubRoles.defaultRoles(mock(Club.class));
        private final Long existId = 1L;
        private final Long nonExistId = 100L;

        @BeforeEach
        void init() {
            ReflectionTestUtils.setField(clubRoles.clubRoles().get(0), "id", existId);
        }

        @Test
        void ID_가_일치하는_역할이_존재하면_반환한다() {
            // when
            final ClubRole clubRole = clubRoles.findById(existId);

            // that
            assertThat(clubRole.id()).isEqualTo(existId);
        }

        @Test
        void ID_가_일치하는_역할이_존재하지_않으면_예외() {
            // when
            final BaseExceptionType baseExceptionType = assertThrows(
                    ClubRoleException.class,
                    () -> clubRoles.findById(nonExistId)
            ).exceptionType();

            // then
            assertThat(baseExceptionType).isEqualTo(NOT_FOUND_ROLE);
        }
    }

    @Nested
    class 역할_추가_테스트 {

        @Test
        void 기본_역할이_아닌_역할을_추가한다() {
            // when
            ClubRole 일반역할 = clubRoles.add(club, "역할1", GENERAL);
            ClubRole 임원역할 = clubRoles.add(club, "역할2", OFFICER);

            // then
            assertAll(
                    () -> assertThat(clubRoles.clubRoles()).contains(일반역할, 임원역할),
                    () -> assertThat(일반역할.isDefault()).isFalse(),
                    () -> assertThat(임원역할.isDefault()).isFalse()
            );
        }

        @Test
        void 역할_이름이_중복되면_예외() {
            // given
            String duplicatedName = "중복이름";
            clubRoles.add(club, duplicatedName, GENERAL);

            // when
            BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class, () ->
                    clubRoles.add(club, duplicatedName, OFFICER)
            ).exceptionType();

            // then
            assertThat(baseExceptionType).isEqualTo(DUPLICATED_NAME);
        }

        @Test
        void 회장_역할을_추가하려는_경우_예외() {
            // when
            BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class, () ->
                    clubRoles.add(club, "something", PRESIDENT)
            ).exceptionType();

            // then
            assertThat(baseExceptionType).isEqualTo(CAN_NOT_CREATE_PRESIDENT_ROLE);
        }
    }

    @Nested
    class 역할_이름_변경_테스트 {

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

            // when
            clubRoles.changeRoleName(PRESIDENT, role.id(), "바꿈");

            // then
            assertThat(role.name()).isEqualTo("바꿈");
        }

        @Test
        void 역할_이름_변경_시_변경될_이름이_중복되는_경우_예외가_발생한다() {
            // given
            ClubRole role1 = clubRoleMap.get(PRESIDENT);
            clubRoles.changeRoleName(PRESIDENT, role1.id(), "중복");
            ClubRole role2 = clubRoleMap.get(OFFICER);

            // when
            BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class, () ->
                    clubRoles.changeRoleName(PRESIDENT, role2.id(), "중복")
            ).exceptionType();

            // then
            assertThat(baseExceptionType).isEqualTo(DUPLICATED_NAME);
        }

        @Test
        void 일반_회원은_역할_이름을_변경할_수_없다() {
            // given
            ClubRole role = clubRoleMap.get(GENERAL);

            // when
            BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class, () ->
                    clubRoles.changeRoleName(GENERAL, role.id(), "변경")
            ).exceptionType();

            // then
            assertThat(baseExceptionType).isEqualTo(NO_AUTHORITY_CHANGE_ROLE_NAME);
        }

        @Test
        void 임원은_일반_역할_이름만을_변경할_수_있다() {
            // given
            ClubRole generalRole = clubRoleMap.get(GENERAL);

            // when
            clubRoles.changeRoleName(OFFICER, generalRole.id(), "변경");

            // then
            assertThat(generalRole.name()).isEqualTo("변경");
        }

        @ParameterizedTest(name = "임원이 일반 역할이 아닌 다른 역할(ex: {0})의 이름을 변경하는 경우 예외")
        @EnumSource(mode = EXCLUDE, value = ClubRoleCategory.class, names = {"GENERAL"})
        void 임원이_일반_역할이_아닌_역할의_이름을_변경하는_경우_예외(final ClubRoleCategory category) {
            // when
            BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class, () ->
                    clubRoles.changeRoleName(OFFICER, clubRoleMap.get(category).id(), "변경")
            ).exceptionType();

            // then
            assertThat(baseExceptionType).isEqualTo(NO_AUTHORITY_CHANGE_ROLE_NAME);
        }

        @ParameterizedTest(name = "회장은_모은_역할의_이름을_다_변경할_수_있다 - ({0} 변경)")
        @EnumSource(mode = EXCLUDE, value = ClubRoleCategory.class)
        void 회장은_모든_역할의_이름을_다_변경할_수_있다(final ClubRoleCategory category) {
            // given
            final ClubRole role = clubRoleMap.get(category);

            // when
            clubRoles.changeRoleName(PRESIDENT, role.id(), "변경");

            // then
            assertThat(role.name()).isEqualTo("변경");
        }

        @Test
        void 바꿀_역할을_찾을_수_없는_경우_예외() {
            // when
            BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class, () ->
                    clubRoles.changeRoleName(PRESIDENT, 1000000L, "임시이름")
            ).exceptionType();

            // then
            assertThat(baseExceptionType).isEqualTo(NOT_FOUND_ROLE);
        }
    }

    @Nested
    @DisplayName("역할 제거(delete) 테스트")
    class 역할_제거_테스트 {

        private final ClubRole 일반역할 = clubRoles.add(club, "새로 생성 일반", GENERAL);
        private final ClubRole 임원역할 = clubRoles.add(club, "새로 생성 임원", OFFICER);
        private final List<ClubRole> 추가된_역할들 = List.of(일반역할, 임원역할);

        @BeforeEach
        void init() {
            for (int i = 0; i < clubRoles.clubRoles().size(); i++) {
                ReflectionTestUtils.setField(clubRoles.clubRoles().get(i), "id", (long) i + 100L);
            }
        }

        @Test
        void 역할을_제거한다() {
            // when
            for (final ClubRole role : 추가된_역할들) {
                clubRoles.delete(role);
            }

            // then
            assertThat(clubRoles.clubRoles())
                    .doesNotContain(임원역할, 일반역할);
        }

        @ParameterizedTest(name = "기본 역할은 제거할 수 없다")
        @EnumSource(mode = EXCLUDE)
        void 기본_역할은_제거할_수_없다(final ClubRoleCategory category) {
            // given
            ClubRole defaultRole = clubRoles.findDefaultRoleByCategory(category);

            // when
            BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class, () ->
                    clubRoles.delete(defaultRole)
            ).exceptionType();

            // then
            assertThat(baseExceptionType).isEqualTo(CAN_NOT_DELETE_DEFAULT_ROLE);
            assertThat(clubRoles.clubRoles()).contains(defaultRole);
        }
    }

    @Nested
    class 기본_역할_변경_테스트 {

        private final ClubRole 일반역할 = clubRoles.add(club, "일반역할", GENERAL);
        private final ClubRole 임원역할 = clubRoles.add(club, "임원역할", OFFICER);
        private final List<ClubRole> 추가된_역할들 = List.of(일반역할, 임원역할);

        @BeforeEach
        void init() {
            for (int i = 0; i < clubRoles.clubRoles().size(); i++) {
                ReflectionTestUtils.setField(clubRoles.clubRoles().get(i), "id", (long) i + 100L);
            }
        }

        @Test
        void 기본_역할을_변경한다() {
            // when
            for (final ClubRole role : 추가된_역할들) {
                clubRoles.changeDefaultRole(role.id());
            }

            // then
            assertAll(
                    () -> assertThat(clubRoles.findDefaultRoleByCategory(OFFICER)).isEqualTo(임원역할),
                    () -> assertThat(clubRoles.findDefaultRoleByCategory(GENERAL)).isEqualTo(일반역할)
            );
        }

        @Test
        void 기본_역할_변경_시_기존_기본_역할은_기본_역할이_아니게_된다() {
            // given
            ClubRole originalDefaultOfficer = clubRoles.findDefaultRoleByCategory(OFFICER);
            ClubRole originalDefaultGeneral = clubRoles.findDefaultRoleByCategory(GENERAL);

            // when
            기본_역할을_변경한다();

            // then
            assertAll(
                    () -> assertThat(originalDefaultOfficer.isDefault()).isFalse(),
                    () -> assertThat(originalDefaultGeneral.isDefault()).isFalse()
            );
        }
    }
}

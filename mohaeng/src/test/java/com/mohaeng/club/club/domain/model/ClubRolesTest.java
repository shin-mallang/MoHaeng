package com.mohaeng.club.club.domain.model;

import com.mohaeng.club.club.exception.ClubRoleException;
import com.mohaeng.common.exception.BaseExceptionType;
import org.junit.jupiter.api.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static com.mohaeng.club.club.domain.model.ClubRoleCategory.*;
import static com.mohaeng.club.club.exception.ClubRoleExceptionType.CAN_NOT_CREATE_PRESIDENT_ROLE;
import static com.mohaeng.club.club.exception.ClubRoleExceptionType.DUPLICATED_NAME;
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
}
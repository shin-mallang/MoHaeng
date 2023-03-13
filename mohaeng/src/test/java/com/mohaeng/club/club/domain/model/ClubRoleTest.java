package com.mohaeng.club.club.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.mohaeng.club.club.domain.model.ClubRoleCategory.PRESIDENT;
import static com.mohaeng.club.club.domain.model.ClubRoleCategory.values;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("ClubRole 은")
class ClubRoleTest {

    private ClubRole clubRoleWithDefault(final boolean isDefault) {
        return new ClubRole("name", PRESIDENT, mock(Club.class), isDefault);
    }

    @Nested
    class 기본_역할_생성_테스트 {

        @Test
        void 회장_임원_일반_역할을_하나씩_생성한다() {
            // when
            final List<ClubRole> clubRoles = ClubRole.defaultRoles(mock(Club.class));

            // then
            assertThat(clubRoles)
                    .extracting(ClubRole::clubRoleCategory)
                    .containsExactlyInAnyOrder(values());
            assertThat(clubRoles)
                    .extracting(ClubRole::isDefault)
                    .containsOnly(true);
        }
    }

    @Nested
    class 기본_역할_여부_확인_테스트 {

        @Test
        void 기본_역할인_경우_true를_반환한다() {
            // given
            final ClubRole role = clubRoleWithDefault(true);

            // when & then
            assertThat(role.isDefault()).isTrue();
        }

        @Test
        void 기본_역할이_아닌_경우_false_를_반환한다() {
            // given
            final ClubRole role = clubRoleWithDefault(false);

            // when & then
            assertThat(role.isDefault()).isFalse();
        }

    }

    @Test
    void 기본_역할이_아닌_역할을_기본_역할로_만들_수_있다() {
        // given
        final ClubRole role = clubRoleWithDefault(false);

        // when
        role.makeDefault();

        // then
        assertThat(role.isDefault()).isTrue();
    }

    @Test
    void 기본_역할을_기본_역할이_아니도록_만들_수_있다() {
        // given
        final ClubRole role = clubRoleWithDefault(true);

        // when
        role.makeNonDefault();

        // then
        assertThat(role.isDefault()).isFalse();
    }

    @Test
    void 이름을_변경할_수_있다() {
        // given
        final ClubRole role = new ClubRole("변경전", PRESIDENT, mock(Club.class), false);

        // when
        role.changeName("후");

        // then
        assertThat(role.name()).isEqualTo("후");
    }
}

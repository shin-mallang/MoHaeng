package com.mohaeng.clubrole.domain.model;

import com.mohaeng.club.domain.model.Club;
import com.mohaeng.common.fixtures.ClubRoleFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.mohaeng.common.fixtures.ClubFixture.club;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("ClubRole 은 ")
class ClubRoleTest {

    @Nested
    @DisplayName("성공 테스트")
    class SuccessTest {

        @Test
        @DisplayName("defaultRoles() 호출 시, 회장, 임원, 일반 분류에 속하는 기본 역할 하나씩을 생성한다.")
        void success_test_1() {
            // given
            Club club = club(1L);

            // when
            List<ClubRole> clubRoles = ClubRole.defaultRoles(club);

            // then
            int presidentRoleCount = 0;
            int officerRoleCount = 0;
            int generalRoleCount = 0;
            for (ClubRole clubRole : clubRoles) {
                switch (clubRole.clubRoleCategory()) {
                    case PRESIDENT -> presidentRoleCount++;
                    case OFFICER -> officerRoleCount++;
                    case GENERAL -> generalRoleCount++;
                }
            }
            int totalPresidentRoleCount = presidentRoleCount;
            int totalOfficerRoleCount = officerRoleCount;
            int totalGeneralRoleCount = generalRoleCount;
            assertAll(
                    () -> assertThat(clubRoles.size()).isEqualTo(3),
                    () -> assertThat(totalPresidentRoleCount).isEqualTo(1),
                    () -> assertThat(totalOfficerRoleCount).isEqualTo(1),
                    () -> assertThat(totalGeneralRoleCount).isEqualTo(1)
            );
        }

        @Test
        @DisplayName("isPresidentRole() 는 회장의 역할인 경우 true를, 이외의 경우 false를 반환한다.")
        void success_test_2() {
            // given
            Club club = club(null);
            ClubRole generalRole = ClubRoleFixture.generalRole("일반", club);
            ClubRole officerRole = ClubRoleFixture.officerRole("임원", club);
            ClubRole presidentRole = ClubRoleFixture.presidentRole("회장", club);

            // when & then
            assertAll(
                    () -> assertThat(presidentRole.isPresidentRole()).isTrue(),
                    () -> assertThat(generalRole.isPresidentRole()).isFalse(),
                    () -> assertThat(officerRole.isPresidentRole()).isFalse()
            );
        }

        @Test
        @DisplayName("isManagerRole() 는 회장과 임원의 역할인 경우 true를, 이외의 경우 false를 반환한다.")
        void success_test_3() {
            // given
            Club club = club(null);
            ClubRole generalRole = ClubRoleFixture.generalRole("일반", club);
            ClubRole officerRole = ClubRoleFixture.officerRole("임원", club);
            ClubRole presidentRole = ClubRoleFixture.presidentRole("회장", club);

            // when & then
            assertAll(
                    () -> assertThat(officerRole.isManagerRole()).isTrue(),
                    () -> assertThat(presidentRole.isManagerRole()).isTrue(),
                    () -> assertThat(generalRole.isManagerRole()).isFalse()
            );
        }

        @Test
        @DisplayName("isGeneralRole() 는 일반 회원의 역할인 경우 true를, 이외의 경우 false를 반환한다.")
        void success_test_4() {
            // given
            Club club = club(null);
            ClubRole generalRole = ClubRoleFixture.generalRole("일반", club);
            ClubRole officerRole = ClubRoleFixture.officerRole("임원", club);
            ClubRole presidentRole = ClubRoleFixture.presidentRole("회장", club);

            // when & then
            assertAll(
                    () -> assertThat(generalRole.isGeneralRole()).isTrue(),
                    () -> assertThat(officerRole.isGeneralRole()).isFalse(),
                    () -> assertThat(presidentRole.isGeneralRole()).isFalse()
            );
        }

        @Test
        @DisplayName("changeName()은 역할의 이름을 변경한다.")
        void success_test_5() {
            // given
            Club club = club(null);
            ClubRole generalRole = ClubRoleFixture.generalRole("일반", club);
            ClubRole officerRole = ClubRoleFixture.officerRole("임원", club);
            ClubRole presidentRole = ClubRoleFixture.presidentRole("회장", club);

            String changeName = "변경!";

            // when
            generalRole.changeName(changeName);
            officerRole.changeName(changeName);
            presidentRole.changeName(changeName);

            // then
            assertAll(
                    () -> assertThat(generalRole.name()).isEqualTo(changeName),
                    () -> assertThat(officerRole.name()).isEqualTo(changeName),
                    () -> assertThat(presidentRole.name()).isEqualTo(changeName)
            );
        }
    }
}
package com.mohaeng.clubrole.domain.model;

import com.mohaeng.club.domain.model.Club;
import com.mohaeng.clubrole.exception.ClubRoleException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.mohaeng.clubrole.domain.model.ClubRoleCategory.*;
import static com.mohaeng.clubrole.exception.ClubRoleExceptionType.ALREADY_DEFAULT_ROLE;
import static com.mohaeng.clubrole.exception.ClubRoleExceptionType.CAN_NOT_COMPARE_OTHER_CLUB_ROLE;
import static com.mohaeng.common.fixtures.ClubFixture.club;
import static com.mohaeng.common.fixtures.ClubRoleFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
            ClubRole generalRole = generalRole("일반", club);
            ClubRole officerRole = officerRole("임원", club);
            ClubRole presidentRole = presidentRole("회장", club);

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
            ClubRole generalRole = generalRole("일반", club);
            ClubRole officerRole = officerRole("임원", club);
            ClubRole presidentRole = presidentRole("회장", club);

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
            ClubRole generalRole = generalRole("일반", club);
            ClubRole officerRole = officerRole("임원", club);
            ClubRole presidentRole = presidentRole("회장", club);

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
            ClubRole generalRole = generalRole("일반", club);
            ClubRole officerRole = officerRole("임원", club);
            ClubRole presidentRole = presidentRole("회장", club);

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

        @Test
        @DisplayName("makeDefault() 는 기본 역할이 아닌 역할을 기본 역할로 변경한다.")
        void success_test_6() {
            // given
            Club club = club(null);
            ClubRole generalRole = generalRole("기본역할이 아닌 알번 역할", club);
            ClubRole officerRole = officerRole("기본역할이 아닌 임원 역할", club);
            ClubRole presidentRole = new ClubRole("기본역할이 아닌 회장 역할", PRESIDENT, club, false);
            ;

            // when
            generalRole.makeDefault();
            officerRole.makeDefault();
            presidentRole.makeDefault();

            // then
            assertAll(
                    () -> assertThat(generalRole.isDefault()).isTrue(),
                    () -> assertThat(officerRole.isDefault()).isTrue(),
                    () -> assertThat(presidentRole.isDefault()).isTrue()
            );
        }

        @Test
        @DisplayName("makeNotDefault() 는 기본 역할을 기본 역할이 아닌 역할로 변경한다.")
        void success_test_7() {
            // given
            Club club = club(null);
            List<ClubRole> defaultRoles = ClubRole.defaultRoles(club);

            // when
            defaultRoles.forEach(ClubRole::makeNotDefault);

            // then
            defaultRoles.forEach(
                    it -> assertThat(it.isDefault()).isFalse()
            );
        }

        @Test
        @DisplayName("isPowerfulThan() 은 내 역할이 대상 역할보다 센 경우 true를 반환한다.")
        void success_test_8() {
            // given
            Club club = club(1L);
            ClubRole presidentRole1 = new ClubRole("P1", PRESIDENT, club, false);
            ClubRole presidentRole2 = new ClubRole("P2", PRESIDENT, club, false);
            ClubRole officerRole1 = new ClubRole("O1", OFFICER, club, false);
            ClubRole officerRole2 = new ClubRole("O2", OFFICER, club, false);
            ClubRole generalRole1 = new ClubRole("G1", GENERAL, club, false);
            ClubRole generalRole2 = new ClubRole("G2", GENERAL, club, false);

            // then
            assertAll(
                    () -> assertThat(presidentRole1.isPowerfulThan(presidentRole1)).isFalse(),
                    () -> assertThat(presidentRole1.isPowerfulThan(presidentRole2)).isFalse(),
                    () -> assertThat(presidentRole1.isPowerfulThan(officerRole1)).isTrue(),
                    () -> assertThat(presidentRole1.isPowerfulThan(officerRole2)).isTrue(),
                    () -> assertThat(presidentRole1.isPowerfulThan(generalRole1)).isTrue(),
                    () -> assertThat(presidentRole1.isPowerfulThan(generalRole2)).isTrue(),
                    () -> assertThat(presidentRole2.isPowerfulThan(presidentRole1)).isFalse(),
                    () -> assertThat(presidentRole2.isPowerfulThan(presidentRole2)).isFalse(),
                    () -> assertThat(presidentRole2.isPowerfulThan(officerRole1)).isTrue(),
                    () -> assertThat(presidentRole2.isPowerfulThan(officerRole2)).isTrue(),
                    () -> assertThat(presidentRole2.isPowerfulThan(generalRole1)).isTrue(),
                    () -> assertThat(presidentRole2.isPowerfulThan(generalRole2)).isTrue(),

                    () -> assertThat(officerRole1.isPowerfulThan(presidentRole1)).isFalse(),
                    () -> assertThat(officerRole1.isPowerfulThan(presidentRole2)).isFalse(),
                    () -> assertThat(officerRole1.isPowerfulThan(officerRole1)).isFalse(),
                    () -> assertThat(officerRole1.isPowerfulThan(officerRole2)).isFalse(),
                    () -> assertThat(officerRole1.isPowerfulThan(generalRole1)).isTrue(),
                    () -> assertThat(officerRole1.isPowerfulThan(generalRole2)).isTrue(),
                    () -> assertThat(officerRole2.isPowerfulThan(presidentRole1)).isFalse(),
                    () -> assertThat(officerRole2.isPowerfulThan(presidentRole2)).isFalse(),
                    () -> assertThat(officerRole2.isPowerfulThan(officerRole1)).isFalse(),
                    () -> assertThat(officerRole2.isPowerfulThan(officerRole2)).isFalse(),
                    () -> assertThat(officerRole2.isPowerfulThan(generalRole1)).isTrue(),
                    () -> assertThat(officerRole2.isPowerfulThan(generalRole2)).isTrue(),

                    () -> assertThat(generalRole1.isPowerfulThan(presidentRole1)).isFalse(),
                    () -> assertThat(generalRole1.isPowerfulThan(presidentRole2)).isFalse(),
                    () -> assertThat(generalRole1.isPowerfulThan(officerRole1)).isFalse(),
                    () -> assertThat(generalRole1.isPowerfulThan(officerRole2)).isFalse(),
                    () -> assertThat(generalRole1.isPowerfulThan(generalRole1)).isFalse(),
                    () -> assertThat(generalRole1.isPowerfulThan(generalRole2)).isFalse(),
                    () -> assertThat(generalRole2.isPowerfulThan(presidentRole1)).isFalse(),
                    () -> assertThat(generalRole2.isPowerfulThan(presidentRole2)).isFalse(),
                    () -> assertThat(generalRole2.isPowerfulThan(officerRole1)).isFalse(),
                    () -> assertThat(generalRole2.isPowerfulThan(officerRole2)).isFalse(),
                    () -> assertThat(generalRole2.isPowerfulThan(generalRole1)).isFalse(),
                    () -> assertThat(generalRole2.isPowerfulThan(generalRole2)).isFalse()
            );
        }

        @Test
        @DisplayName("isSamePowerThan()은 내 역할과 대상 역할의 파워가 동일한 경우 ture를 반환한다.")
        void success_test_9() {
            // given
            Club club = club(1L);
            ClubRole presidentRole1 = new ClubRole("P1", PRESIDENT, club, false);
            ClubRole presidentRole2 = new ClubRole("P2", PRESIDENT, club, false);
            ClubRole officerRole1 = new ClubRole("O1", OFFICER, club, false);
            ClubRole officerRole2 = new ClubRole("O2", OFFICER, club, false);
            ClubRole generalRole1 = new ClubRole("G1", GENERAL, club, false);
            ClubRole generalRole2 = new ClubRole("G2", GENERAL, club, false);

            // then
            assertAll(
                    () -> assertThat(presidentRole1.isSamePowerThan(presidentRole1)).isTrue(),
                    () -> assertThat(presidentRole1.isSamePowerThan(presidentRole2)).isTrue(),
                    () -> assertThat(presidentRole1.isSamePowerThan(officerRole1)).isFalse(),
                    () -> assertThat(presidentRole1.isSamePowerThan(officerRole2)).isFalse(),
                    () -> assertThat(presidentRole1.isSamePowerThan(generalRole1)).isFalse(),
                    () -> assertThat(presidentRole1.isSamePowerThan(generalRole2)).isFalse(),
                    () -> assertThat(presidentRole2.isSamePowerThan(presidentRole1)).isTrue(),
                    () -> assertThat(presidentRole2.isSamePowerThan(presidentRole2)).isTrue(),
                    () -> assertThat(presidentRole2.isSamePowerThan(officerRole1)).isFalse(),
                    () -> assertThat(presidentRole2.isSamePowerThan(officerRole2)).isFalse(),
                    () -> assertThat(presidentRole2.isSamePowerThan(generalRole1)).isFalse(),
                    () -> assertThat(presidentRole2.isSamePowerThan(generalRole2)).isFalse(),

                    () -> assertThat(officerRole1.isSamePowerThan(presidentRole1)).isFalse(),
                    () -> assertThat(officerRole1.isSamePowerThan(presidentRole2)).isFalse(),
                    () -> assertThat(officerRole1.isSamePowerThan(officerRole1)).isTrue(),
                    () -> assertThat(officerRole1.isSamePowerThan(officerRole2)).isTrue(),
                    () -> assertThat(officerRole1.isSamePowerThan(generalRole1)).isFalse(),
                    () -> assertThat(officerRole1.isSamePowerThan(generalRole2)).isFalse(),
                    () -> assertThat(officerRole2.isSamePowerThan(presidentRole1)).isFalse(),
                    () -> assertThat(officerRole2.isSamePowerThan(presidentRole2)).isFalse(),
                    () -> assertThat(officerRole2.isSamePowerThan(officerRole1)).isTrue(),
                    () -> assertThat(officerRole2.isSamePowerThan(officerRole2)).isTrue(),
                    () -> assertThat(officerRole2.isSamePowerThan(generalRole1)).isFalse(),
                    () -> assertThat(officerRole2.isSamePowerThan(generalRole2)).isFalse(),

                    () -> assertThat(generalRole1.isSamePowerThan(presidentRole1)).isFalse(),
                    () -> assertThat(generalRole1.isSamePowerThan(presidentRole2)).isFalse(),
                    () -> assertThat(generalRole1.isSamePowerThan(officerRole1)).isFalse(),
                    () -> assertThat(generalRole1.isSamePowerThan(officerRole2)).isFalse(),
                    () -> assertThat(generalRole1.isSamePowerThan(generalRole1)).isTrue(),
                    () -> assertThat(generalRole1.isSamePowerThan(generalRole2)).isTrue(),
                    () -> assertThat(generalRole2.isSamePowerThan(presidentRole1)).isFalse(),
                    () -> assertThat(generalRole2.isSamePowerThan(presidentRole2)).isFalse(),
                    () -> assertThat(generalRole2.isSamePowerThan(officerRole1)).isFalse(),
                    () -> assertThat(generalRole2.isSamePowerThan(officerRole2)).isFalse(),
                    () -> assertThat(generalRole2.isSamePowerThan(generalRole1)).isTrue(),
                    () -> assertThat(generalRole2.isSamePowerThan(generalRole2)).isTrue()
            );
        }
    }

    @Nested
    @DisplayName("실패 테스트")
    class FailTest {

        @Test
        @DisplayName("makeDefault() 는 이미 기본 역할에 대해 적용되는 경우, 예외가 발생한다.")
        void fail_test_1() {
            // given
            Club club = club(null);
            List<ClubRole> defaultRoles = ClubRole.defaultRoles(club);

            // when
            defaultRoles.stream()
                    .map(it ->
                            assertThrows(ClubRoleException.class, it::makeDefault)
                                    .exceptionType())
                    .forEach(it -> {
                        assertThat(it).isEqualTo(ALREADY_DEFAULT_ROLE);
                    });
        }

        @Test
        @DisplayName("isPowerfulThan() 은 두 역할이 속한 모임이 다른 경우 예외를 발생시킨다.")
        void fail_test_2() {
            // given
            Club club1 = club(1L);
            Club club2 = club(2L);
            ClubRole presidentRole1 = new ClubRole("P1", PRESIDENT, club1, false);
            ClubRole presidentRole2 = new ClubRole("P2", PRESIDENT, club2, false);
            ClubRole officerRole1 = new ClubRole("O1", OFFICER, club1, false);
            ClubRole officerRole2 = new ClubRole("O2", OFFICER, club2, false);
            ClubRole generalRole1 = new ClubRole("G1", GENERAL, club1, false);
            ClubRole generalRole2 = new ClubRole("G2", GENERAL, club2, false);

            List<ClubRole> roles1 = List.of(presidentRole1, officerRole1, generalRole1);
            List<ClubRole> roles2 = List.of(presidentRole2, officerRole2, generalRole2);

            // when
            roles1.stream()
                    .flatMap(it -> roles2.stream().map(other ->
                            assertThrows(ClubRoleException.class, () ->

                                    it.isPowerfulThan(other)

                            ).exceptionType())
                    ).forEach(exType -> {
                        assertThat(exType).isEqualTo(CAN_NOT_COMPARE_OTHER_CLUB_ROLE);
                    });
        }

        @Test
        @DisplayName("isSamePowerThan() 은 두 역할이 속한 모임이 다른 경우 예외를 발생시킨다.")
        void fail_test_3() {
            // given
            Club club1 = club(1L);
            Club club2 = club(2L);
            ClubRole presidentRole1 = new ClubRole("P1", PRESIDENT, club1, false);
            ClubRole presidentRole2 = new ClubRole("P2", PRESIDENT, club2, false);
            ClubRole officerRole1 = new ClubRole("O1", OFFICER, club1, false);
            ClubRole officerRole2 = new ClubRole("O2", OFFICER, club2, false);
            ClubRole generalRole1 = new ClubRole("G1", GENERAL, club1, false);
            ClubRole generalRole2 = new ClubRole("G2", GENERAL, club2, false);

            List<ClubRole> roles1 = List.of(presidentRole1, officerRole1, generalRole1);
            List<ClubRole> roles2 = List.of(presidentRole2, officerRole2, generalRole2);

            // when
            roles1.stream()
                    .flatMap(it -> roles2.stream().map(other ->
                            assertThrows(ClubRoleException.class, () ->

                                    it.isSamePowerThan(other)

                            ).exceptionType())
                    ).forEach(exType -> {
                        assertThat(exType).isEqualTo(CAN_NOT_COMPARE_OTHER_CLUB_ROLE);
                    });
        }
    }
}
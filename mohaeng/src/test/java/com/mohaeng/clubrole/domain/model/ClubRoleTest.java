package com.mohaeng.clubrole.domain.model;

import com.mohaeng.club.domain.model.Club;
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
        @DisplayName("회장, 임원, 일반 분류에 속하는 기본 역할 하나씩을 생성한다.")
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
    }
}
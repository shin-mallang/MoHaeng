package com.mohaeng.domain.club.model.role;

import com.mohaeng.domain.club.model.club.Club;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("ClubRole 은 ")
class ClubRoleTest {

    @Test
    @DisplayName("회장, 임원, 일반 분류에 속하는 기본 역할 하나씩을 생성한다.")
    void defaultRoles() {
        // given
        Club club = new Club("name", "des", 10);

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
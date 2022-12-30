package com.mohaeng.domain.club.domain;

import com.mohaeng.domain.club.domain.enums.ClubRoleCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("ClubRole 은 ")
class ClubRoleTest {

    @Test
    @DisplayName("`기본` 역할의 기본값은 `회장`, `임원`, `일반`이다.")
    void test() {
        ClubRole defaultOfficerRole = ClubRole.defaultOfficerRole();
        ClubRole defaultPresidentRole = ClubRole.defaultPresidentRole();
        ClubRole defaultGeneralRole = ClubRole.defaultGeneralRole();

        assertAll(
                () -> assertThat(defaultPresidentRole.name()).isEqualTo("회장"),
                () -> assertThat(defaultPresidentRole.roleCategory()).isEqualTo(ClubRoleCategory.PRESIDENT),
                () -> assertThat(defaultPresidentRole.isBasicRile()).isTrue(),
                () -> assertThat(defaultOfficerRole.name()).isEqualTo("임원"),
                () -> assertThat(defaultOfficerRole.roleCategory()).isEqualTo(ClubRoleCategory.OFFICER),
                () -> assertThat(defaultOfficerRole.isBasicRile()).isTrue(),
                () -> assertThat(defaultGeneralRole.name()).isEqualTo("일반"),
                () -> assertThat(defaultGeneralRole.roleCategory()).isEqualTo(ClubRoleCategory.GENERAL),
                () -> assertThat(defaultGeneralRole.isBasicRile()).isTrue()
        );
    }
}
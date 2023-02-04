package com.mohaeng.clubrole.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.mohaeng.clubrole.domain.model.ClubRoleCategory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("ClubRoleCategory 는 ")
class ClubRoleCategoryTest {

    @Nested
    @DisplayName("성공 테스트")
    class SuccessTest {

        @Test
        @DisplayName("isPowerfulThan()은 내 파워가 더 센 경우 true를 반환한다.")
        void success_test_1() {
            // then
            assertAll(
                    () -> assertThat(PRESIDENT.isPowerfulThan(PRESIDENT)).isFalse(),
                    () -> assertThat(PRESIDENT.isPowerfulThan(OFFICER)).isTrue(),
                    () -> assertThat(PRESIDENT.isPowerfulThan(GENERAL)).isTrue(),

                    () -> assertThat(OFFICER.isPowerfulThan(PRESIDENT)).isFalse(),
                    () -> assertThat(OFFICER.isPowerfulThan(OFFICER)).isFalse(),
                    () -> assertThat(OFFICER.isPowerfulThan(GENERAL)).isTrue(),

                    () -> assertThat(GENERAL.isPowerfulThan(PRESIDENT)).isFalse(),
                    () -> assertThat(GENERAL.isPowerfulThan(OFFICER)).isFalse(),
                    () -> assertThat(GENERAL.isPowerfulThan(GENERAL)).isFalse()
            );
        }

        @Test
        @DisplayName("isSamePowerThan()은 파워가 동일한 경우 true를 반환한다.")
        void success_test_2() {
            // then
            assertAll(
                    () -> assertThat(PRESIDENT.isSamePowerThan(PRESIDENT)).isTrue(),
                    () -> assertThat(PRESIDENT.isSamePowerThan(OFFICER)).isFalse(),
                    () -> assertThat(PRESIDENT.isSamePowerThan(GENERAL)).isFalse(),

                    () -> assertThat(OFFICER.isSamePowerThan(PRESIDENT)).isFalse(),
                    () -> assertThat(OFFICER.isSamePowerThan(OFFICER)).isTrue(),
                    () -> assertThat(OFFICER.isSamePowerThan(GENERAL)).isFalse(),

                    () -> assertThat(GENERAL.isSamePowerThan(PRESIDENT)).isFalse(),
                    () -> assertThat(GENERAL.isSamePowerThan(OFFICER)).isFalse(),
                    () -> assertThat(GENERAL.isSamePowerThan(GENERAL)).isTrue()
            );
        }
    }
}
package com.mohaeng.member.domain.model;

import com.mohaeng.authentication.exception.AuthenticationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.mohaeng.authentication.exception.AuthenticationExceptionType.INCORRECT_AUTHENTICATION;
import static com.mohaeng.common.fixtures.MemberFixture.member;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Member 는 ")
class MemberTest {

    @Nested
    @DisplayName("성공 테스트")
    class SuccessTest {

        @Test
        @DisplayName("아이디 비밀번호가 일치하다면 로그인에 성공한다.")
        void success_test_1() {
            // given
            Member mallang = member(null);

            // when & then
            assertThatCode(() -> mallang.login(mallang.username(), mallang.password()))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("실패 테스트")
    class FailTest {

        @Test
        @DisplayName("아이디 혹은 비밀번호가 일치하지 않는다면 로그인에 실패하고 예외를 발생시킨다.")
        void fail_test_1() {
            // given
            Member mallang = member(null);

            // when & then
            assertAll(
                    () -> assertThat(assertThrows(AuthenticationException.class,

                            () -> mallang.login(mallang.username(), "wrong"))

                            .exceptionType())
                            .isEqualTo(INCORRECT_AUTHENTICATION),

                    () -> assertThat(assertThrows(AuthenticationException.class,

                            () -> mallang.login("wrong", mallang.password()))

                            .exceptionType())
                            .isEqualTo(INCORRECT_AUTHENTICATION),

                    () -> assertThat(assertThrows(AuthenticationException.class,

                            () -> mallang.login("wrong", "wrong"))

                            .exceptionType())
                            .isEqualTo(INCORRECT_AUTHENTICATION)
            );
        }
    }
}
package com.mohaeng.authentication.application.service;

import com.mohaeng.authentication.application.usecase.LogInUseCase;
import com.mohaeng.authentication.domain.model.AccessToken;
import com.mohaeng.authentication.exception.AuthenticationException;
import com.mohaeng.common.annotation.ApplicationTest;
import com.mohaeng.common.exception.BaseExceptionType;
import com.mohaeng.common.fixtures.MemberFixture;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.member.domain.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.mohaeng.authentication.exception.AuthenticationExceptionType.INCORRECT_AUTHENTICATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ApplicationTest
@DisplayName("LogIn은 ")
class LogInTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private LogInUseCase logInUseCase;

    private final Member member = MemberFixture.member(null);

    @Nested
    @DisplayName("성공 테스트")
    class SuccessTest {

        @Test
        @DisplayName("아이디와 비밀번호를 통해 로그인을 진행하고 AccessToken을 반환한다.")
        void success_test_1() {
            // given
            memberRepository.save(member);

            // when
            AccessToken token = logInUseCase.command(
                    new LogInUseCase.Command(member.username(), member.password())
            );

            // then
            assertAll(
                    () -> assertThat(token.token()).isNotNull()
            );
        }
    }

    @Nested
    @DisplayName("실패 테스트")
    class FailTest {

        @Test
        @DisplayName("아이디가 없는 아이디라면 예외를 발생한다.")
        void fail_test_1() {
            // when, then
            BaseExceptionType baseExceptionType = assertThrows(AuthenticationException.class,
                    () -> logInUseCase.command(
                            new LogInUseCase.Command(member.username(), member.password())
                    )).exceptionType();
            assertThat(baseExceptionType).isEqualTo(INCORRECT_AUTHENTICATION);
        }

        @Test
        @DisplayName("비밀번호가 일치하지 않는다면 예외를 발생한다.")
        void fail_test_2() {
            // given
            memberRepository.save(member);

            // when, then
            BaseExceptionType baseExceptionType = assertThrows(AuthenticationException.class,
                    () -> logInUseCase.command(
                            new LogInUseCase.Command(member.username(), "incorrectPassword")
                    )).exceptionType();
            assertThat(baseExceptionType).isEqualTo(INCORRECT_AUTHENTICATION);
        }
    }
}
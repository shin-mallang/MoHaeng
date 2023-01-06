package com.mohaeng.authentication.application.service;

import com.mohaeng.authentication.application.exception.IncorrectAuthenticationException;
import com.mohaeng.authentication.application.usecase.CreateTokenUseCase;
import com.mohaeng.authentication.application.usecase.LogInUseCase;
import com.mohaeng.authentication.application.service.LogIn;
import com.mohaeng.authentication.domain.model.AccessToken;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.member.domain.model.enums.Gender;
import com.mohaeng.member.domain.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@DisplayName("LogIn은 ")
class LogInTest {

    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final CreateTokenUseCase createTokenUseCase = mock(CreateTokenUseCase.class);
    private final LogInUseCase logInUseCase = new LogIn(memberRepository, createTokenUseCase);
    private final Member member =
            new Member(1L, LocalDateTime.now(), LocalDateTime.now(), "username", "password", "name", 10, Gender.MAN);

    @Test
    @DisplayName("아이디와 비밀번호를 통해 로그인을 진행하고 AccessToken을 반환한다.")
    void loginWithUsernameAndPassword() {
        // given
        when(memberRepository.findByUsername("username"))
                .thenReturn(Optional.of(member));
        when(createTokenUseCase.command(any()))
                .thenReturn("token");
        // when
        AccessToken token = logInUseCase.command(
                new LogInUseCase.Command("username", "password")
        );

        // then
        assertAll(
                () -> assertThat(token.token()).isEqualTo("token"),
                () -> verify(memberRepository, times(1)).findByUsername("username")
        );
    }

    @Test
    @DisplayName("아이디가 없는 아이디라면 예외를 발생한다.")
    void loginFailCauseByIncorrectUsernameWillReturnException() {
        // given
        when(memberRepository.findByUsername("username"))
                .thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> logInUseCase.command(
                new LogInUseCase.Command("username", "password")
        )).isInstanceOf(IncorrectAuthenticationException.class);
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않는다면 예외를 발생한다.")
    void loginFailCauseByIncorrectPasswordWillReturnException() {
        // given
        when(memberRepository.findByUsername("username"))
                .thenReturn(Optional.of(member));

        // when, then
        assertThatThrownBy(() -> logInUseCase.command(
                new LogInUseCase.Command("username", "incorrectPassword")
        )).isInstanceOf(IncorrectAuthenticationException.class);
    }
}
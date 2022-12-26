package com.mohaeng.domain.authentication.service;

import com.mohaeng.common.member.Gender;
import com.mohaeng.domain.authentication.domain.AccessToken;
import com.mohaeng.domain.authentication.exception.IncorrectAuthenticationException;
import com.mohaeng.domain.authentication.usecase.LogInUseCase;
import com.mohaeng.infrastructure.authentication.jwt.usecase.CreateTokenUseCase;
import com.mohaeng.infrastructure.persistence.database.entity.member.MemberJpaEntity;
import com.mohaeng.infrastructure.persistence.database.service.member.MemberQuery;
import com.mohaeng.infrastructure.persistence.database.service.member.exception.NotFoundMemberException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@DisplayName("LogIn은 ")
class LogInTest {

    private final MemberQuery memberQuery = mock(MemberQuery.class);
    private final CreateTokenUseCase createTokenUseCase = mock(CreateTokenUseCase.class);
    private final LogInUseCase logInUseCase = new LogIn(memberQuery, createTokenUseCase);
    private final MemberJpaEntity memberJpaEntity =
            new MemberJpaEntity("username", "password", "name", 10, Gender.MAN);

    @Test
    @DisplayName("아이디와 비밀번호를 통해 로그인을 진행하고 AccessToken을 반환한다.")
    void loginWithUsernameAndPassword() {
        // given
        ReflectionTestUtils.setField(memberJpaEntity, "id", 1L);
        when(memberQuery.findByUsername("username"))
                .thenReturn(memberJpaEntity);
        when(createTokenUseCase.command(any()))
                .thenReturn("token");
        // when
        AccessToken token = logInUseCase.command(
                new LogInUseCase.Command("username", "password")
        );

        // then
        assertAll(
                () -> assertThat(token.memberId()).isEqualTo(1L),
                () -> assertThat(token.token()).isEqualTo("token"),
                () -> verify(memberQuery, times(1)).findByUsername("username")
        );
    }

    @Test
    @DisplayName("아이디가 없는 아이디라면 예외를 발생한다.")
    void loginFailCauseByIncorrectUsernameWillReturnException() {
        // given
        when(memberQuery.findByUsername("username"))
                .thenThrow(NotFoundMemberException.class);

        // when, then
        assertThatThrownBy(() -> logInUseCase.command(
                new LogInUseCase.Command("username", "password")
        )).isInstanceOf(IncorrectAuthenticationException.class);
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않는다면 예외를 발생한다.")
    void loginFailCauseByIncorrectPasswordWillReturnException() {
        // given
        when(memberQuery.findByUsername("username"))
                .thenReturn(memberJpaEntity);

        // when, then
        assertThatThrownBy(() -> logInUseCase.command(
                new LogInUseCase.Command("username", "incorrectPassword")
        )).isInstanceOf(IncorrectAuthenticationException.class);
    }
}
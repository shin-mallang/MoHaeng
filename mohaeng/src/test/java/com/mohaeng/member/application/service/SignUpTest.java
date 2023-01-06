package com.mohaeng.member.application.service;

import com.mohaeng.member.application.exception.DuplicateUsernameException;
import com.mohaeng.member.application.usecase.SignUpUseCase;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.member.domain.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.mohaeng.common.fixtures.MemberFixture.signUpUseCaseCommand;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@DisplayName("SignUp은 ")
class SignUpTest {

    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final SignUpUseCase signUp = new SignUp(memberRepository);

    @Test
    @DisplayName("중복되는 아이디가 있다면 오류를 반환한다.")
    void throwExceptionWhenUsernameDuplicated() {
        // given
        when(memberRepository.existsByUsername(any())).thenReturn(true);

        // when, then
        assertAll(
                () -> assertThatThrownBy(() -> signUp.command(signUpUseCaseCommand()))
                        .isInstanceOf(DuplicateUsernameException.class),
                () -> verify(memberRepository, times(0))
                        .save(any(Member.class))
        );
    }

    @Test
    @DisplayName("문제가 없는 경우 회원 가입을 진행한다.")
    void success() {
        // when, then
        assertAll(
                () -> assertDoesNotThrow(() -> signUp.command(signUpUseCaseCommand())),
                () -> verify(memberRepository, times(1))
                        .save(any(Member.class))
        );
    }
    // TODO 비밀번호 암호화 되는 테스트
}
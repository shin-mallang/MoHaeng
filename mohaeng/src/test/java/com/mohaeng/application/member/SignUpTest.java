package com.mohaeng.application.member;

import com.mohaeng.application.member.exception.DuplicateUsernameException;
import com.mohaeng.application.member.service.SignUp;
import com.mohaeng.application.member.usecase.SignUpUseCase;
import com.mohaeng.domain.member.model.Member;
import com.mohaeng.domain.member.model.enums.Gender;
import com.mohaeng.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
        SignUpUseCase.Command command = SignUpUseCaseCommandFixture.command();

        // when, then
        assertAll(
                () -> assertThatThrownBy(() -> signUp.command(command))
                        .isInstanceOf(DuplicateUsernameException.class),
                () -> verify(memberRepository, times(0))
                        .save(any(Member.class))
        );
    }

    @Test
    @DisplayName("문제가 없는 경우 회원 가입을 진행한다.")
    void success() {
        // given
        SignUpUseCase.Command command = SignUpUseCaseCommandFixture.command();

        // when, then
        assertAll(
                () -> assertDoesNotThrow(() -> signUp.command(command)),
                () -> verify(memberRepository, times(1))
                        .save(any(Member.class))
        );
    }
    // TODO 비밀번호 암호화 되는 테스트


    private static class SignUpUseCaseCommandFixture {
        private static String username = "username";
        private static String password = "password";
        private static String name = "name";
        private static int age = 10;
        private static Gender gender = Gender.MAN;

        public static SignUpUseCase.Command command() {
            return new SignUpUseCase.Command(
                    username, password, name, age, gender
            );
        }
    }
}
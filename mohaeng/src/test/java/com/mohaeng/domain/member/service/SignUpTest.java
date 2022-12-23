package com.mohaeng.domain.member.service;

import com.mohaeng.common.member.Gender;
import com.mohaeng.common.member.dto.CreateMemberDto;
import com.mohaeng.domain.member.exception.DuplicateUsernameException;
import com.mohaeng.domain.member.usecase.SignUpUseCase;
import com.mohaeng.infrastructure.persistence.database.entity.member.MemberJpaEntity;
import com.mohaeng.infrastructure.persistence.database.service.member.MemberCommand;
import com.mohaeng.infrastructure.persistence.database.service.member.MemberQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@DisplayName("SignUp은 ")
class SignUpTest {

    private final MemberCommand mockMemberCommand = mock(MemberCommand.class);
    private final MemberQuery mockMemberQuery = mock(MemberQuery.class);
    private final SignUpUseCase signUp = new SignUp(mockMemberCommand, mockMemberQuery);

    @Test
    @DisplayName("중복되는 아이디가 있다면 오류를 반환한다.")
    void throwExceptionWhenUsernameDuplicated() {
        // given
        when(mockMemberQuery.existsByUsername(any())).thenReturn(true);
        SignUpUseCase.Command command = SignUpUseCaseCommandFixture.command();

        // when, then
        assertThatThrownBy(() -> signUp.command(command))
                .isInstanceOf(DuplicateUsernameException.class);
        verify(mockMemberCommand, times(0))
                .save(any(CreateMemberDto.class));
    }

    @Test
    @DisplayName("문제가 없는 경우 회원 가입을 진행한다.")
    void success() {
        // given
        SignUpUseCase.Command command = SignUpUseCaseCommandFixture.command();

        // when, then
        assertDoesNotThrow(() -> signUp.command(command));
        verify(mockMemberCommand, times(1))
                .save(any(CreateMemberDto.class));
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
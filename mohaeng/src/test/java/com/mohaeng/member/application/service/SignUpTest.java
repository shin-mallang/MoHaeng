package com.mohaeng.member.application.service;

import com.mohaeng.common.annotation.ApplicationTest;
import com.mohaeng.member.application.usecase.SignUpUseCase;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.member.domain.repository.MemberRepository;
import com.mohaeng.member.exception.MemberException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.mohaeng.common.fixtures.MemberFixture.*;
import static com.mohaeng.member.exception.MemberExceptionType.DUPLICATE_USERNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ApplicationTest
@DisplayName("SignUp 은 ")
class SignUpTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private SignUpUseCase signUp;

    @Nested
    @DisplayName("성공 테스트")
    class SuccessTest {

        @Test
        @DisplayName("문제가 없는 경우 회원 가입을 진행한다.")
        void success_test_1() {
            // when, then
            assertAll(
                    () -> assertDoesNotThrow(() -> signUp.command(signUpUseCaseCommand())),
                    () -> assertThat(memberRepository.findByUsername(MALLANG_USERNAME)).isNotNull()
            );
        }
        // TODO 비밀번호 암호화 되는 테스트
    }

    @Nested
    @DisplayName("실패 테스트")
    class FailTest {

        @Test
        @DisplayName("중복되는 아이디가 있다면 오류를 반환한다.")
        void fail_test_1() {
            // given
            Member mallang = MALLANG;
            memberRepository.save(mallang);

            // when, then
            assertThat(assertThrows(MemberException.class,
                    () -> signUp.command(new SignUpUseCase.Command(mallang.username(), mallang.password(), mallang.name(), mallang.age(), mallang.gender()))
            ).exceptionType()).isEqualTo(DUPLICATE_USERNAME);
        }
    }
}
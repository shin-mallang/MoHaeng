package com.mohaeng.domain.member.domain;

import com.mohaeng.application.authentication.exception.IncorrectAuthenticationException;
import com.mohaeng.domain.member.domain.enums.Gender;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Member는 ")
class MemberTest {

    @Test
    @DisplayName("아이디 비밀번호가 일치하다면 로그인에 성공한다.")
    void loginSuccess() {
        // given
        Member member = new Member("username", "password", "name", 12, Gender.MAN);

        // when & then
        assertThatCode(() -> member.login("username", "password"))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("아이디 혹은 비밀번호가 일치하지 않는다면 로그인에 실패하고 예외를 발생시킨다.")
    void failSuccess() {
        // given
        Member member = new Member("username", "password", "name", 12, Gender.MAN);

        // when & then
        assertAll(
                () -> assertThatThrownBy(() -> member.login("username", "wrong")).isInstanceOf(IncorrectAuthenticationException.class),
                () -> assertThatThrownBy(() -> member.login("wrong", "password")).isInstanceOf(IncorrectAuthenticationException.class),
                () -> assertThatThrownBy(() -> member.login("wrong", "wrong")).isInstanceOf(IncorrectAuthenticationException.class)
        );
    }
}
package com.mohaeng.common.fixtures;

import com.mohaeng.member.application.usecase.SignUpUseCase;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.member.domain.model.enums.Gender;
import com.mohaeng.member.presentation.SignUpController;
import org.springframework.test.util.ReflectionTestUtils;

import static java.lang.String.format;

public class MemberFixture {

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String NAME = "name";
    public static final int AGE = 10;
    public static final Gender GENDER = Gender.MAN;

    private static final String USERNAME_FORMAT = "username%d";
    private static Long sequence = 0L;

    public static Member member(final Long id) {
        Member member = new Member(format(USERNAME_FORMAT, ++sequence), PASSWORD, NAME, AGE, Gender.MAN);
        ReflectionTestUtils.setField(member, "id", id);
        return member;
    }

    public static SignUpUseCase.Command signUpUseCaseCommand() {
        return new SignUpUseCase.Command(format(USERNAME_FORMAT, ++sequence), PASSWORD, NAME, AGE, Gender.MAN);
    }

    public static SignUpController.SignUpRequest signUpRequest(final String username,
                                                               final String password,
                                                               final String name,
                                                               final int age,
                                                               final Gender gender) {
        return new SignUpController.SignUpRequest(username, password, name, age, gender);
    }
}

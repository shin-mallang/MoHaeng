package com.mohaeng.member.presentation.mapper;

import com.mohaeng.member.application.usecase.SignUpUseCase;
import com.mohaeng.member.domain.model.enums.Gender;
import com.mohaeng.member.presentation.SignUpController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.mohaeng.common.fixtures.MemberFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("MemberControllerMapper 는 ")
class MemberControllerMapperTest {

    @Test
    @DisplayName("SignUpRequest 를 SignUpUseCase.Command 로 변환한다.")
    void test() {
        // given
        SignUpController.SignUpRequest signUpRequest = signUpRequest(USERNAME, PASSWORD, NAME, AGE, Gender.MAN);

        // when
        SignUpUseCase.Command command = MemberControllerMapper.toApplicationDto(signUpRequest);

        // then
        assertAll(
                () -> assertThat(signUpRequest.username()).isEqualTo(command.username()),
                () -> assertThat(signUpRequest.password()).isEqualTo(command.password()),
                () -> assertThat(signUpRequest.name()).isEqualTo(command.name()),
                () -> assertThat(signUpRequest.age()).isEqualTo(command.age()),
                () -> assertThat(signUpRequest.gender()).isEqualTo(command.gender())
        );
    }
}
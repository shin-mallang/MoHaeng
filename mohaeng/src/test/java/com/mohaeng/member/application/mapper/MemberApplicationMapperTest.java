package com.mohaeng.member.application.mapper;

import com.mohaeng.member.application.usecase.SignUpUseCase;
import com.mohaeng.member.domain.model.Member;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.mohaeng.common.fixtures.MemberFixture.signUpUseCaseCommand;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("MemberApplicationMapper 는 ")
class MemberApplicationMapperTest {

    @Test
    @DisplayName("SignUpUseCase.Command 를 Member로 변환한다.")
    void test() {
        // given
        SignUpUseCase.Command command = signUpUseCaseCommand();

        // when
        Member member = MemberApplicationMapper.toDomainEntity(command);

        // then
        assertAll(
                () -> assertThat(command.username()).isEqualTo(member.username()),
                () -> assertThat(command.password()).isEqualTo(member.password()),
                () -> assertThat(command.name()).isEqualTo(member.name()),
                () -> assertThat(command.age()).isEqualTo(member.age()),
                () -> assertThat(command.gender()).isEqualTo(member.gender())
        );
    }
}
package com.mohaeng.member.presentation.mapper;

import com.mohaeng.member.application.usecase.SignUpUseCase;
import com.mohaeng.member.presentation.SignUpController;

public class MemberControllerMapper {

    public static SignUpUseCase.Command toApplicationDto(final SignUpController.SignUpRequest signUpRequest) {
        return new SignUpUseCase.Command(
                signUpRequest.username(),
                signUpRequest.password(),
                signUpRequest.name(),
                signUpRequest.age(),
                signUpRequest.gender());
    }
}

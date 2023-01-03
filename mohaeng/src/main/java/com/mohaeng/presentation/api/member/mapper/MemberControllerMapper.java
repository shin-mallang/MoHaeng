package com.mohaeng.presentation.api.member.mapper;

import com.mohaeng.application.member.usecase.SignUpUseCase;
import com.mohaeng.presentation.api.member.SignUpController;

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

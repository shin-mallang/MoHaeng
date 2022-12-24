package com.mohaeng.presentation.api.member.mapper;

import com.mohaeng.domain.member.usecase.SignUpUseCase;
import com.mohaeng.presentation.api.member.request.SignUpRequest;

public class MemberControllerMapper {

    public static SignUpUseCase.Command toDomainLayerDto(final SignUpRequest signUpRequest) {
        return new SignUpUseCase.Command(
                signUpRequest.username(),
                signUpRequest.password(),
                signUpRequest.name(),
                signUpRequest.age(),
                signUpRequest.gender());
    }
}

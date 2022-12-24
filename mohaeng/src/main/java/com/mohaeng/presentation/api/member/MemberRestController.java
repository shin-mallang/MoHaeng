package com.mohaeng.presentation.api.member;

import com.mohaeng.domain.member.usecase.SignUpUseCase;
import com.mohaeng.presentation.api.member.mapper.MemberControllerMapper;
import com.mohaeng.presentation.api.member.request.SignUpRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api")
public class MemberRestController {

    private final SignUpUseCase signUpUseCase;

    public MemberRestController(final SignUpUseCase signUpUseCase) {
        this.signUpUseCase = signUpUseCase;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "/v1/sign-up")
    public void signUp(
            @Valid @RequestBody final SignUpRequest signUpRequest
    ) {
        signUpUseCase.command(
                MemberControllerMapper.toDomainLayerDto(signUpRequest)
        );
    }
}

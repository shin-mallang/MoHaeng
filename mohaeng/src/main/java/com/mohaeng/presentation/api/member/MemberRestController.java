package com.mohaeng.presentation.api.member;

import com.mohaeng.application.member.usecase.SignUpUseCase;
import com.mohaeng.presentation.api.member.mapper.MemberControllerMapper;
import com.mohaeng.presentation.api.member.request.SignUpRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api")
public class MemberRestController {

    public static final String SIGN_UP_URL = "/v1/sign-up";
    private final SignUpUseCase signUpUseCase;

    public MemberRestController(final SignUpUseCase signUpUseCase) {
        this.signUpUseCase = signUpUseCase;
    }

    @PostMapping(path = SIGN_UP_URL)
    public ResponseEntity<Void> signUp(
            @Valid @RequestBody final SignUpRequest signUpRequest
    ) {
        signUpUseCase.command(
                MemberControllerMapper.toApplicationLayerDto(signUpRequest)
        );

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}

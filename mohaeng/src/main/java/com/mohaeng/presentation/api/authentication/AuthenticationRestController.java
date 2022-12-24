package com.mohaeng.presentation.api.authentication;

import com.mohaeng.domain.authentication.domain.AccessToken;
import com.mohaeng.domain.authentication.usecase.LogInUseCase;
import com.mohaeng.presentation.api.authentication.mapper.AuthenticationMapper;
import com.mohaeng.presentation.api.authentication.request.LoginRequest;
import com.mohaeng.presentation.api.authentication.response.TokenResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api")
public class AuthenticationRestController {

    private final LogInUseCase logInUseCase;

    public AuthenticationRestController(final LogInUseCase logInUseCase) {
        this.logInUseCase = logInUseCase;
    }

    /**
     * 로그인
     */
    @PostMapping(path = "/login")
    public ResponseEntity<TokenResponse> login(
            @Valid @RequestBody final LoginRequest loginRequest
    ) {
        AccessToken token = logInUseCase.command(
            AuthenticationMapper.toLoginDto(loginRequest)
        );
        return ResponseEntity.ok(AuthenticationMapper.toResponseDto(token));
    }
}

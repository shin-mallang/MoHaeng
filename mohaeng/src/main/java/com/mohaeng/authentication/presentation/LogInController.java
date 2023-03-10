package com.mohaeng.authentication.presentation;

import com.mohaeng.authentication.application.usecase.LogInUseCase;
import com.mohaeng.authentication.domain.model.AccessToken;
import com.mohaeng.authentication.presentation.mapper.AuthenticationControllerMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LogInController {

    public static final String LOGIN_URL = "/api/login";

    private final LogInUseCase logInUseCase;

    public LogInController(final LogInUseCase logInUseCase) {
        this.logInUseCase = logInUseCase;
    }

    /**
     * 로그인
     */
    @PostMapping(path = LOGIN_URL)
    public ResponseEntity<TokenResponse> login(
            @Valid @RequestBody final LoginRequest loginRequest
    ) {
        AccessToken token = logInUseCase.command(
                AuthenticationControllerMapper.toApplicationLayerDto(loginRequest)
        );
        return ResponseEntity.ok(AuthenticationControllerMapper.toResponseDto(token));
    }

    public record LoginRequest(
            @NotBlank(message = "아이디가 입력되지 않았습니다.")
            String username,

            @NotBlank(message = "비밀번호가 입력되지 않았습니다.")
            String password
    ) {
    }

    public record TokenResponse(
            String token
    ) {
    }
}

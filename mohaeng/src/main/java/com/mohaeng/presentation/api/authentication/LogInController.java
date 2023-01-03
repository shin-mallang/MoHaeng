package com.mohaeng.presentation.api.authentication;

import com.mohaeng.application.authentication.usecase.LogInUseCase;
import com.mohaeng.domain.authentication.domain.AccessToken;
import com.mohaeng.presentation.api.authentication.mapper.AuthenticationControllerMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api")
public class LogInController {

    public static final String LOGIN_URL = "/login";

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

    /**
     * 로그인 Request
     */
    public record LoginRequest(
            @NotBlank(message = "아이디가 입력되지 않았습니다.")
            String username,

            @NotBlank(message = "비밀번호가 입력되지 않았습니다.")
            String password
    ) {
    }

    /**
     * 로그인 Response
     */
    public record TokenResponse(
            String token
    ) {
    }
}

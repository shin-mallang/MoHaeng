package com.mohaeng.presentation.api.authentication.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "아이디가 입력되지 않았습니다.")
        String username,

        @NotBlank(message = "비밀번호가 입력되지 않았습니다.")
        String password
) {
}

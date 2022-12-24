package com.mohaeng.presentation.api.member.request;

import com.mohaeng.common.member.Gender;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SignUpRequest(
        @NotBlank(message = "아이디가 입력되지 않았습니다.")
        String username,

        @NotBlank(message = "비밀번호가 입력되지 않았습니다.")
        String password,

        @NotBlank(message = "이름이 입력되지 않았습니다.")
        String name,

        @Min(value = 1, message = "나이는 1살 이상이어야 합니다.")
        int age,

        @NotNull(message = "성별이 입력되지 않았습니다.")
        Gender gender
) {
}

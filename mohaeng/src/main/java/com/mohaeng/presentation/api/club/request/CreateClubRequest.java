package com.mohaeng.presentation.api.club.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateClubRequest(
        @NotBlank(message = "모임 이름이 입력되지 않았습니다.")
        String name,

        String description,

        @Min(value = 1, message = "인원은 최소 1명 이상이어야 합니다.")
        int maxPeopleCount
) {
}

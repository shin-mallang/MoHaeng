package com.mohaeng.application.member.dto;

import com.mohaeng.domain.member.domain.enums.Gender;

public record CreateMemberDto(
        String username,
        String password,
        String name,
        int age,
        Gender gender
) {
}

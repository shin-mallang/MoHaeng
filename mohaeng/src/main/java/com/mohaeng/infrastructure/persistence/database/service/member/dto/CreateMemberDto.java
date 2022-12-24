package com.mohaeng.infrastructure.persistence.database.service.member.dto;

import com.mohaeng.common.member.Gender;

public record CreateMemberDto(
        String username,
        String password,
        String name,
        int age,
        Gender gender
) {
}

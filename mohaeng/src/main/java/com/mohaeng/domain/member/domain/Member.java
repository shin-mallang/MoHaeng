package com.mohaeng.domain.member.domain;

import com.mohaeng.common.member.Gender;
import com.mohaeng.domain.member.domain.enums.PasswordMatchResult;

import java.time.LocalDateTime;

public record Member(
        Long id,
        LocalDateTime createdAt,
        LocalDateTime lastModifiedAt,
        String username,
        String password,
        String name,
        int age,
        Gender gender
) {

    public PasswordMatchResult matchPassword(final String password) {
        // TODO 비밀번호 암호화
        if (this.password.equals(password)) {
            return PasswordMatchResult.MATCH;
        }
        return PasswordMatchResult.MISS_MATCH;
    }
}

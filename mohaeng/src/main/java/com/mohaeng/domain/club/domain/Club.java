package com.mohaeng.domain.club.domain;

import java.time.LocalDateTime;

public record Club(
        Long id,
        LocalDateTime createdAt,
        LocalDateTime lastModifiedAt,
        String name,  // 이름
        String description,  // 설명
        int maxPeopleCount  // 최대 인원수
) {

    public Club(final String name, final String description, final int maxPeopleCount) {
        this(null, null, null,
                name, description, maxPeopleCount);
    }
}

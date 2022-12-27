package com.mohaeng.domain.club.domain;

import java.time.LocalDateTime;

public record Club(
        Long id,
        LocalDateTime createdAt,
        LocalDateTime lastModifiedAt,

        Long presidentId,  // 회장 Id
        String name,  // 이름
        String description,  // 설명
        int maxPeopleCount  // 최대 인원수
) {

    public Club(final Long presidentId, final String name, final String description, final int maxPeopleCount) {
        this(null, null, null,
                presidentId, name, description, maxPeopleCount);
    }
}

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

}

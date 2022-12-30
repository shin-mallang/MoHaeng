package com.mohaeng.domain.club.domain;

import java.time.LocalDateTime;

public record ClubMember(
        Long id,
        LocalDateTime createdAt,
        LocalDateTime lastModifiedAt,

        Long memberId,
        ClubRole clubRole,
        Club club
) {

    public ClubMember(final Long memberId, final ClubRole clubRole, final Club club) {
        this(null, null, null, memberId, clubRole, club);
    }
}

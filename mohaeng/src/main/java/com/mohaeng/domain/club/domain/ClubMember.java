package com.mohaeng.domain.club.domain;

import com.mohaeng.domain.member.domain.Member;

import java.time.LocalDateTime;

public record ClubMember(
        Long id,
        LocalDateTime createdAt,
        LocalDateTime lastModifiedAt,

        ClubRole clubRole,
        // TODO memberId로 묶을지, Member로 묶을지 고민, 어떤 상황이 발생할지 몰라서 일단 Member로 해둠
        Member member,
        Club club
) {
}

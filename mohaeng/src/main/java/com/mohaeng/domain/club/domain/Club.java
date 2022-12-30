package com.mohaeng.domain.club.domain;

import com.mohaeng.domain.club.domain.enums.ClubRoleCategory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record Club(
        Long id,
        LocalDateTime createdAt,
        LocalDateTime lastModifiedAt,

        String name,  // 이름
        String description,  // 설명
        int maxPeopleCount,  // 최대 인원수

        List<ClubRole> clubRoles,
        List<ClubMember> clubMembers
) {

    public static Club newClub(final String name, final String description, final int maxPeopleCount, final Long presidentId) {
        Club club = new Club(null,
                null,
                null,
                name,
                description,
                maxPeopleCount,
                ClubRole.defaultClubRoles(),
                new ArrayList<>());
        club.addMember(new ClubMember(presidentId, club.basicRoleOfCategory(ClubRoleCategory.PRESIDENT), club));
        return club;
    }

    /**
     * 주어진 카테고리에 속하는 기본 역할을 반환한다.
     */
    private ClubRole basicRoleOfCategory(final ClubRoleCategory clubRoleCategory) {
        return this.clubRoles.stream()
                .filter(role -> role.isBasicRile() && role.roleCategory() == clubRoleCategory)
                .findAny()
                .orElseThrow(() -> new IllegalStateException("발생하면 안되는 오류"));
    }

    /**
     * 모임에 회원을 추가한다.
     */
    private void addMember(final ClubMember clubMember) {
        this.clubMembers.add(clubMember);
    }
}

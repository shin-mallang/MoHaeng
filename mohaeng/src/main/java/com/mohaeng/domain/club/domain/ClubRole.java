package com.mohaeng.domain.club.domain;

import com.mohaeng.domain.club.domain.enums.ClubRoleCategory;

import java.time.LocalDateTime;
import java.util.List;

public record ClubRole(
        Long id,
        LocalDateTime createdAt,
        LocalDateTime lastModifiedAt,

        ClubRoleCategory roleCategory,  // 역할 분류
        String name,  // 역할 이름
        boolean isBasicRile  // 기본 역할인지 여부
) {

    public static ClubRole defaultPresidentRole() {
        return new ClubRole(ClubRoleCategory.PRESIDENT, "회장", true);
    }

    public static ClubRole defaultOfficerRole() {
        return new ClubRole(ClubRoleCategory.OFFICER, "임원", true);
    }

    public static ClubRole defaultGeneralRole() {
        return new ClubRole(ClubRoleCategory.GENERAL, "일반", true);
    }

    public ClubRole(final ClubRoleCategory clubRoleCategory, final String name, final boolean isBasicRile) {
        this(null, null, null, clubRoleCategory, name, isBasicRile);
    }

    public static List<ClubRole> defaultClubRoles() {
        return List.of(defaultPresidentRole(), defaultOfficerRole(), defaultGeneralRole());
    }

    public ClubRole changeName(final String name) {
        return new ClubRole(id(), createdAt(), lastModifiedAt(), roleCategory(), name, isBasicRile());
    }
}

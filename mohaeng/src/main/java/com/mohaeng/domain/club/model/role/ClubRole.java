package com.mohaeng.domain.club.model.role;

import com.mohaeng.domain.club.model.club.Club;
import com.mohaeng.domain.config.BaseEntity;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "club_role")
public class ClubRole extends BaseEntity {

    private static final String DEFAULT_PRESIDENT_ROLE_NAME = "회장";
    private static final String DEFAULT_OFFICER_ROLE_NAME = "임원";
    private static final String DEFAULT_GENERAL_ROLE_NAME = "일반 회원";

    private String name;  // 역할의 이름

    @Enumerated(EnumType.STRING)
    private ClubRoleCategory clubRoleCategory;  // 역할 분류

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id")
    private Club club;  // 해당 역할을 가진 Club

    //== 생성자 ==//
    protected ClubRole() {
    }

    public ClubRole(final String name,
                    final ClubRoleCategory clubRoleCategory,
                    final Club club) {
        this.name = name;
        this.clubRoleCategory = clubRoleCategory;
        this.club = club;
    }

    //== 정적 메서드 ==//
    private static ClubRole defaultPresidentRole(final Club club) {
        return new ClubRole(DEFAULT_PRESIDENT_ROLE_NAME, ClubRoleCategory.PRESIDENT, club);
    }

    private static ClubRole defaultOfficerRole(final Club club) {
        return new ClubRole(DEFAULT_OFFICER_ROLE_NAME, ClubRoleCategory.OFFICER, club);
    }

    private static ClubRole defaultGeneralRole(final Club club) {
        return new ClubRole(DEFAULT_GENERAL_ROLE_NAME, ClubRoleCategory.GENERAL, club);
    }

    public static List<ClubRole> defaultRoles(final Club club) {
        return List.of(
                defaultPresidentRole(club),
                defaultOfficerRole(club),
                defaultGeneralRole(club)
        );
    }

    //== getter ==//
    public String name() {
        return name;
    }

    public ClubRoleCategory clubRoleCategory() {
        return clubRoleCategory;
    }

    public Club club() {
        return club;
    }
}

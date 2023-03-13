package com.mohaeng.club.club.domain.model;

import com.mohaeng.common.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.List;

import static com.mohaeng.club.club.domain.model.ClubRoleCategory.*;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "club_role")
public class ClubRole extends BaseEntity {

    private static final String DEFAULT_PRESIDENT_ROLE_NAME = "회장";
    private static final String DEFAULT_OFFICER_ROLE_NAME = "임원";
    private static final String DEFAULT_GENERAL_ROLE_NAME = "일반 회원";

    private String name;  // 역할의 이름
    private boolean isDefault;  // 기본 역할인지 여부

    @Enumerated(STRING)
    private ClubRoleCategory clubRoleCategory;  // 역할 분류

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "club_id")
    private Club club;  // 해당 역할을 가진 Club

    //== 생성자 ==//
    protected ClubRole() {
    }

    public ClubRole(final String name,
                    final ClubRoleCategory clubRoleCategory,
                    final Club club,
                    final boolean isDefault) {
        this.name = name;
        this.clubRoleCategory = clubRoleCategory;
        this.club = club;
        this.isDefault = isDefault;
    }

    public static List<ClubRole> defaultRoles(final Club club) {
        return List.of(
                new ClubRole(DEFAULT_PRESIDENT_ROLE_NAME, PRESIDENT, club, true),
                new ClubRole(DEFAULT_OFFICER_ROLE_NAME, OFFICER, club, true),
                new ClubRole(DEFAULT_GENERAL_ROLE_NAME, GENERAL, club, true)
        );
    }

    public void changeName(final String name) {
        this.name = name;
    }

    public void makeDefault() {
        isDefault = true;
    }

    public void makeNonDefault() {
        isDefault = false;
    }

    public String name() {
        return name;
    }

    public ClubRoleCategory clubRoleCategory() {
        return clubRoleCategory;
    }

    public Club club() {
        return club;
    }

    public boolean isDefault() {
        return isDefault;
    }
}

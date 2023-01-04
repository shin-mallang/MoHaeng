package com.mohaeng.domain.club.model.role;

import com.mohaeng.domain.club.model.club.Club;
import com.mohaeng.domain.config.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "club_role")
public class ClubRole extends BaseEntity {

    private String name;  // 역할의 이름

    @Enumerated(EnumType.STRING)
    private ClubRoleCategory clubRoleCategory;  // 역할 분류

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id")
    private Club club;  // 해당 역할을 가진 Club

    protected ClubRole() {}

    public ClubRole(final String name,
                    final ClubRoleCategory clubRoleCategory,
                    final Club club) {
        this.name = name;
        this.clubRoleCategory = clubRoleCategory;
        this.club = club;
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
}

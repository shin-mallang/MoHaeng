package com.mohaeng.infrastructure.persistence.database.entity.club;

import com.mohaeng.domain.club.domain.enums.ClubRoleCategory;
import com.mohaeng.infrastructure.persistence.database.config.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "club_role")
public class ClubRoleJpaEntity extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private ClubRoleCategory clubRoleCategory;
    private String name;
    private boolean isBasicRole;

    @ManyToOne
    @JoinColumn(name = "club_id")
    private ClubJpaEntity clubJpaEntity;

    protected ClubRoleJpaEntity() {
    }

    public ClubRoleJpaEntity(final ClubRoleCategory clubRoleCategory, final String name, final boolean isBasicRole) {
        this.clubRoleCategory = clubRoleCategory;
        this.name = name;
        this.isBasicRole = isBasicRole;
    }

    public ClubRoleCategory clubRoleCategory() {
        return clubRoleCategory;
    }

    public String name() {
        return name;
    }

    public boolean isBasicRole() {
        return isBasicRole;
    }

    public void changeName(final String name) {
        this.name = name;
    }

    /**
     * 기본 역할에서 제거한다.
     */
    public void makeGeneralRole() {
        this.isBasicRole = false;
    }

    /**
     * 기본 역할로 만든다.
     */
    public void makeBasicRole() {
        this.isBasicRole = true;
    }

    /**
     * Club을 세팅한다.
     */
    public void confirmClub(final ClubJpaEntity clubJpaEntity) {
        this.clubJpaEntity = clubJpaEntity;
    }
}

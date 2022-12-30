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

    public ClubRoleJpaEntity(final ClubRoleCategory clubRoleCategory,
                             final String name,
                             final boolean isBasicRole,
                             final ClubJpaEntity clubJpaEntity) {
        this.clubRoleCategory = clubRoleCategory;
        this.name = name;
        this.isBasicRole = isBasicRole;
        this.clubJpaEntity = clubJpaEntity;
    }

    //== Getter ==//
    public ClubRoleCategory clubRoleCategory() {
        return clubRoleCategory;
    }

    public String name() {
        return name;
    }

    public boolean isBasicRole() {
        return isBasicRole;
    }

    public ClubJpaEntity clubJpaEntity() {
        return clubJpaEntity;
    }

    public void confirmClub(final ClubJpaEntity clubJpaEntity) {
        this.clubJpaEntity = clubJpaEntity;
    }
}

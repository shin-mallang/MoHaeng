package com.mohaeng.infrastructure.persistence.database.entity.club;

import com.mohaeng.infrastructure.persistence.database.config.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "club_member")
public class ClubMemberJpaEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_role_id")
    private ClubRoleJpaEntity clubRoleJpaEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id")
    private ClubJpaEntity clubJpaEntity;  // clubRole에 있는 club으로 해결할 수도 있지만, 일단 함

    protected ClubMemberJpaEntity() {
    }

    public ClubMemberJpaEntity(final ClubRoleJpaEntity clubRoleJpaEntity) {
        this.clubRoleJpaEntity = clubRoleJpaEntity;
    }

    //== Getter ==//
    public ClubRoleJpaEntity clubRoleJpaEntity() {
        return clubRoleJpaEntity;
    }

    public ClubJpaEntity clubJpaEntity() {
        return clubJpaEntity;
    }

    public void confirmClub(final ClubJpaEntity clubJpaEntity) {
        this.clubJpaEntity = clubJpaEntity;
    }
}

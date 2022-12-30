package com.mohaeng.infrastructure.persistence.database.entity.club;

import com.mohaeng.infrastructure.persistence.database.config.BaseEntity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "club")
public class ClubJpaEntity extends BaseEntity {

    private String name;  // 이름
    private String description;  // 설명
    private int maxPeopleCount;  // 최대 인원수

    @OneToMany(mappedBy = "clubJpaEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClubRoleJpaEntity> clubRoleJpaEntities = new ArrayList<>();  // 모임 역할

    @OneToMany(mappedBy = "clubJpaEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClubMemberJpaEntity> clubMemberJpaEntities = new ArrayList<>();  // 모임에 참여한 회원

    protected ClubJpaEntity() {
    }

    public ClubJpaEntity(final String name, final String description, final int maxPeopleCount) {
        this.name = name;
        this.description = description;
        this.maxPeopleCount = maxPeopleCount;
    }

    //== Getter ==//
    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public int maxPeopleCount() {
        return maxPeopleCount;
    }

    public List<ClubRoleJpaEntity> clubRoleJpaEntities() {
        return clubRoleJpaEntities;
    }

    public List<ClubMemberJpaEntity> clubMemberJpaEntities() {
        return clubMemberJpaEntities;
    }

    //== 연관관계 편의 메서드 ==//
    public void addClubRole(final ClubRoleJpaEntity clubRoleJpaEntity) {
        clubRoleJpaEntity.confirmClub(this);
        this.clubRoleJpaEntities.add(clubRoleJpaEntity);
    }

    public void addClubMember(final ClubMemberJpaEntity clubMemberJpaEntity) {
        if (clubMemberJpaEntity.clubRoleJpaEntity() == null
                || !clubMemberJpaEntity.clubRoleJpaEntity().clubJpaEntity().equals(this)) {
            throw new IllegalStateException("Club Member의 Club Role이 잘못되었습니다.");
        }
        clubMemberJpaEntity.confirmClub(this);
        this.clubMemberJpaEntities.add(clubMemberJpaEntity);
    }
}

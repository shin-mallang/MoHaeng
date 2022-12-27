package com.mohaeng.infrastructure.persistence.database.entity.club;

import com.mohaeng.infrastructure.persistence.database.config.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "club")
public class ClubJpaEntity extends BaseEntity {

    private String name;  // 이름
    private String description;  // 설명
    private int maxPeopleCount;  // 최대 인원수

    protected ClubJpaEntity() {
    }

    public ClubJpaEntity(final String name, final String description, final int maxPeopleCount) {
        this.name = name;
        this.description = description;
        this.maxPeopleCount = maxPeopleCount;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public int maxPeopleCount() {
        return maxPeopleCount;
    }
}

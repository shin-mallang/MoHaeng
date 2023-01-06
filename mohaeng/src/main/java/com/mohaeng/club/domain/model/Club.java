package com.mohaeng.club.domain.model;


import com.mohaeng.common.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "club")
public class Club extends BaseEntity {

    private String name;

    private String description;

    // TODO maxParticipantCount
    private int maxPeopleCount;

    protected Club() {
    }

    public Club(final String name, final String description, final int maxPeopleCount) {
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

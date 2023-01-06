package com.mohaeng.club.domain.model;


import com.mohaeng.common.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "club")
public class Club extends BaseEntity {

    private String name;

    private String description;

    private int maxParticipantCount;

    protected Club() {
    }

    public Club(final String name, final String description, final int maxParticipantCount) {
        this.name = name;
        this.description = description;
        this.maxParticipantCount = maxParticipantCount;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public int maxParticipantCount() {
        return maxParticipantCount;
    }
}

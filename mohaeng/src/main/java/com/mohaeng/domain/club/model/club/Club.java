package com.mohaeng.domain.club.model.club;


import com.mohaeng.domain.config.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "club")
public class Club extends BaseEntity {

    private String name;

    private String description;

    private int maxPeopleCount;

    protected Club() {
    }

    public Club(final Long id,
                final LocalDateTime createdAt,
                final LocalDateTime lastModifiedAt,
                final String name,
                final String description,
                final int maxPeopleCount) {
        super(id, createdAt, lastModifiedAt);
        this.name = name;
        this.description = description;
        this.maxPeopleCount = maxPeopleCount;
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
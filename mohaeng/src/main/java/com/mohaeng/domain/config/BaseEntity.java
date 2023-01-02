package com.mohaeng.domain.config;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@MappedSuperclass
public class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id = null;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime lastModifiedAt;

    protected BaseEntity() {

    }

    public BaseEntity(final Long id, final LocalDateTime createdAt, final LocalDateTime lastModifiedAt) {
        this.id = id;
        this.createdAt = createdAt;
        this.lastModifiedAt = lastModifiedAt;
    }

    @PrePersist
    private void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        lastModifiedAt = now;
    }

    @PreUpdate
    private void preUpdate() {
        lastModifiedAt = LocalDateTime.now();
    }

    public Long id() {
        return id;
    }

    public LocalDateTime createdAt() {
        return createdAt;
    }

    public LocalDateTime lastModifiedAt() {
        return lastModifiedAt;
    }
}

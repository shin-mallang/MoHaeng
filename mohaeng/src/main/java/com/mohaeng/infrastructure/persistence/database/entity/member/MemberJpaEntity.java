package com.mohaeng.infrastructure.persistence.database.entity.member;

import com.mohaeng.domain.member.domain.enums.Gender;
import com.mohaeng.infrastructure.persistence.database.config.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "member")
public class MemberJpaEntity extends BaseEntity {

    private String username;
    private String password;
    private String name;
    private int age;
    @Enumerated(EnumType.STRING)
    private Gender gender;

    protected MemberJpaEntity() {
    }

    public MemberJpaEntity(final String username,
                           final String password,
                           final String name,
                           final int age,
                           final Gender gender) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.age = age;
        this.gender = gender;
    }

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }

    public String name() {
        return name;
    }

    public int age() {
        return age;
    }

    public Gender gender() {
        return gender;
    }
}

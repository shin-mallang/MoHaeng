package com.mohaeng.domain.member.domain;

import com.mohaeng.domain.config.BaseEntity;
import com.mohaeng.domain.member.domain.enums.Gender;
import com.mohaeng.domain.member.domain.enums.PasswordMatchResult;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "member")
public class Member extends BaseEntity {

    protected Member() {
    }


    public Member(final Long id,
                  final LocalDateTime createdAt,
                  final LocalDateTime lastModifiedAt,
                  final String username,
                  final String password,
                  final String name,
                  final int age,
                  final Gender gender) {
        super(id, createdAt, lastModifiedAt);
        this.username = username;
        this.password = password;
        this.name = name;
        this.age = age;
        this.gender = gender;
    }

    public Member(final String username,
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

    private String username;

    private String password;

    private String name;

    private int age;

    @Enumerated(EnumType.STRING)
    private Gender gender;

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

    public PasswordMatchResult matchPassword(final String password) {
        // TODO 비밀번호 암호화
        if (this.password.equals(password)) {
            return PasswordMatchResult.MATCH;
        }
        return PasswordMatchResult.MISS_MATCH;
    }
}

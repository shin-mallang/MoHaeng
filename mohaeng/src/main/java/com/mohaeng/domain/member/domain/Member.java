package com.mohaeng.domain.member.domain;

import com.mohaeng.application.authentication.exception.IncorrectAuthenticationException;
import com.mohaeng.domain.config.BaseEntity;
import com.mohaeng.domain.member.domain.enums.Gender;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "member")
public class Member extends BaseEntity {

    private String username;

    private String password;

    private String name;

    private int age;

    @Enumerated(EnumType.STRING)
    private Gender gender;

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

    /**
     * 로그인을 수행한다.
     *
     * @param username 입력받은 아이디
     * @param password 입력받은 비밀번호
     * @throws IncorrectAuthenticationException 아이디 혹은 비밀번호가 일치하지 않은 경우 발생
     */
    public void login(final String username, final String password) throws IncorrectAuthenticationException {
        if (!matchUsername(username) || !matchPassword(password)) {
            throw new IncorrectAuthenticationException();
        }
    }

    private boolean matchPassword(final String password) {
        // TODO 비밀번호 암호화
        return this.password.equals(password);
    }

    private boolean matchUsername(final String username) {
        return this.username.equals(username);
    }
}

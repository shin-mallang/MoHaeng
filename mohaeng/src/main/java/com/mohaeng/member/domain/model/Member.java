package com.mohaeng.member.domain.model;

import com.mohaeng.authentication.exception.AuthenticationException;
import com.mohaeng.common.domain.BaseEntity;
import com.mohaeng.member.domain.model.enums.Gender;
import jakarta.persistence.*;

import static com.mohaeng.authentication.exception.AuthenticationExceptionType.INCORRECT_AUTHENTICATION;

@Entity
@Table(name = "member")
public class Member extends BaseEntity {

    @Column(unique = true)
    private String username;

    private String password;

    private String name;

    private int age;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    protected Member() {
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
     * @throws AuthenticationException (TYPE : INCORRECT_AUTHENTICATION) 아이디 혹은 비밀번호가 일치하지 않은 경우 발생
     */
    public void login(final String username, final String password) throws AuthenticationException {
        if (!matchUsername(username) || !matchPassword(password)) {
            throw new AuthenticationException(INCORRECT_AUTHENTICATION);
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

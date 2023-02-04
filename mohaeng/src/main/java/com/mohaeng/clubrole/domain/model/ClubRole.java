package com.mohaeng.clubrole.domain.model;

import com.mohaeng.club.domain.model.Club;
import com.mohaeng.clubrole.exception.ClubRoleException;
import com.mohaeng.common.domain.BaseEntity;
import jakarta.persistence.*;

import java.util.List;

import static com.mohaeng.clubrole.exception.ClubRoleExceptionType.ALREADY_DEFAULT_ROLE;
import static com.mohaeng.clubrole.exception.ClubRoleExceptionType.CAN_NOT_COMPARE_OTHER_CLUB_ROLE;

@Entity
@Table(name = "club_role")
public class ClubRole extends BaseEntity {

    private static final String DEFAULT_PRESIDENT_ROLE_NAME = "회장";
    private static final String DEFAULT_OFFICER_ROLE_NAME = "임원";
    private static final String DEFAULT_GENERAL_ROLE_NAME = "일반 회원";

    private String name;  // 역할의 이름

    private boolean isDefault;  // 기본 역할인지 여부

    @Enumerated(EnumType.STRING)
    private ClubRoleCategory clubRoleCategory;  // 역할 분류

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id")
    private Club club;  // 해당 역할을 가진 Club

    //== 생성자 ==//
    protected ClubRole() {
    }

    public ClubRole(final String name,
                    final ClubRoleCategory clubRoleCategory,
                    final Club club,
                    final boolean isDefault) {
        this.name = name;
        this.clubRoleCategory = clubRoleCategory;
        this.club = club;
        this.isDefault = isDefault;
    }

    //== 정적 메서드 ==//
    private static ClubRole defaultPresidentRole(final Club club) {
        return new ClubRole(DEFAULT_PRESIDENT_ROLE_NAME, ClubRoleCategory.PRESIDENT, club, true);
    }

    private static ClubRole defaultOfficerRole(final Club club) {
        return new ClubRole(DEFAULT_OFFICER_ROLE_NAME, ClubRoleCategory.OFFICER, club, true);
    }

    private static ClubRole defaultGeneralRole(final Club club) {
        return new ClubRole(DEFAULT_GENERAL_ROLE_NAME, ClubRoleCategory.GENERAL, club, true);
    }

    public static List<ClubRole> defaultRoles(final Club club) {
        return List.of(
                defaultPresidentRole(club),
                defaultOfficerRole(club),
                defaultGeneralRole(club)
        );
    }

    //== getter ==//
    public String name() {
        return name;
    }

    public ClubRoleCategory clubRoleCategory() {
        return clubRoleCategory;
    }

    public Club club() {
        return club;
    }

    public boolean isDefault() {
        return isDefault;
    }

    /**
     * 관리자(회장, 임원)역할인지 확인
     */
    public boolean isManagerRole() {
        // 일반 역할만 아니면 관리자다
        return this.clubRoleCategory != ClubRoleCategory.GENERAL;
    }

    /**
     * 회장인지 확인
     */
    public boolean isPresidentRole() {
        return this.clubRoleCategory == ClubRoleCategory.PRESIDENT;
    }

    /**
     * 일반 회원인지 확인
     */
    public boolean isGeneralRole() {
        return this.clubRoleCategory == ClubRoleCategory.GENERAL;
    }

    /**
     * 역할의 이름 변경
     */
    public void changeName(final String roleName) {
        this.name = roleName;
    }

    /**
     * 기본 역할로 변경한다.
     */
    public void makeDefault() {
        // 이미 기본 역할일 경우 예외
        if (isDefault) {
            throw new ClubRoleException(ALREADY_DEFAULT_ROLE);
        }
        this.isDefault = true;
    }

    public void makeNotDefault() {
        this.isDefault = false;
    }

    /**
     * 다른 역할보다 파워가 더 센지 확인한다.
     *
     * @param clubRole 비교대상
     * @return 파워가 더 센 경우 true
     */
    public boolean isPowerfulThan(final ClubRole clubRole) {
        // 같은 모임인지 확인한다.
        checkSameClub(clubRole);
        return this.clubRoleCategory().isPowerfulThan(clubRole.clubRoleCategory());
    }

    /**
     * 다른 역할과 내 역할의 파워가 동일한지 확인한다.
     *
     * @param clubRole 비교대상
     * @return 파워가 동일한 경우 true
     */
    public boolean isSamePowerThan(final ClubRole clubRole) {
        // 같은 모임인지 확인한다.
        checkSameClub(clubRole);
        return this.clubRoleCategory().isSamePowerThan(clubRole.clubRoleCategory());
    }

    /**
     * 같은 모임의 역할이 아닌 경우 예외를 발생한다.
     */
    private void checkSameClub(final ClubRole clubRole) {
        System.out.println("!@@!@!");
        System.out.println(this.club().id());
        System.out.println(clubRole.club().id());
        if (!this.club().id().equals(clubRole.club().id())) {
            throw new ClubRoleException(CAN_NOT_COMPARE_OTHER_CLUB_ROLE);
        }
    }
}

package com.mohaeng.clubrole.domain.model;

public enum ClubRoleCategory {
    PRESIDENT(100),
    OFFICER(10),
    GENERAL(1),
    ;

    private int power;  // 파워, 숫자가 클수록 파워가 세다.

    ClubRoleCategory(final int power) {
        this.power = power;
    }

    /**
     * 내 역할과 다른 역할의 파워를 비교한다.
     *
     * @param category 비교대상
     * @return 내 파워가 더 큰 경우에만 true
     */
    public boolean isPowerfulThan(final ClubRoleCategory category) {
        return (this.power - category.power) > 0;
    }

    /**
     * 내 역할과 다른 역할의 파워를 비교한다.
     *
     * @param category 비교대상
     * @return 내 파워와 대상의 파워가 같은 경우 true
     */
    public boolean isSamePowerThan(final ClubRoleCategory category) {
        return (this.power - category.power) == 0;
    }
}

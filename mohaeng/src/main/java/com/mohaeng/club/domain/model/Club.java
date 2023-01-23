package com.mohaeng.club.domain.model;

import com.mohaeng.club.exception.ClubException;
import com.mohaeng.common.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import static com.mohaeng.club.exception.ClubExceptionType.CLUB_IS_EMPTY;
import static com.mohaeng.club.exception.ClubExceptionType.CLUB_IS_FULL;

@Entity
@Table(name = "club")
public class Club extends BaseEntity {

    private String name;

    private String description;

    private int maxParticipantCount;

    private int currentParticipantCount;  // 현재 가입한 인원 수

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

    public int currentParticipantCount() {
        return currentParticipantCount;
    }

    /**
     * 모임 참여 인원 증가
     */
    public void participantCountUp() {
        if (maxParticipantCount < currentParticipantCount + 1) {
            throw new ClubException(CLUB_IS_FULL);
        }
        currentParticipantCount++;
    }

    /**
     * 모임 참여 인원 감소
     */
    public void participantCountDown() {
        // 만약 currentParticipantCount--;를 먼저 수행한다면, 테스트가 롤백되지 않아 확인할 수 없음
        if (currentParticipantCount - 1 == 0) {
            throw new ClubException(CLUB_IS_EMPTY);
        }
        currentParticipantCount--;
    }
}

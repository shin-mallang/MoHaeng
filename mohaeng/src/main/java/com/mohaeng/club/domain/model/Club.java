package com.mohaeng.club.domain.model;

import com.mohaeng.club.exception.ClubException;
import com.mohaeng.common.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

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
}

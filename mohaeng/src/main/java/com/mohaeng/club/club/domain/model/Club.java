package com.mohaeng.club.club.domain.model;

import com.mohaeng.club.club.exception.ClubException;
import com.mohaeng.club.participant.domain.model.Participant;
import com.mohaeng.club.participant.domain.model.Participants;
import com.mohaeng.club.participant.exception.ParticipantException;
import com.mohaeng.common.domain.BaseEntity;
import com.mohaeng.member.domain.model.Member;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.List;
import java.util.Optional;

import static com.mohaeng.club.club.domain.model.ClubRoleCategory.GENERAL;
import static com.mohaeng.club.club.domain.model.ClubRoleCategory.PRESIDENT;
import static com.mohaeng.club.club.exception.ClubExceptionType.CLUB_IS_FULL;
import static com.mohaeng.club.participant.exception.ParticipantExceptionType.ALREADY_EXIST_PARTICIPANT;

@Entity
@Table(name = "club")
public class Club extends BaseEntity {

    private String name;
    private String description;
    private int maxParticipantCount;
    private int currentParticipantCount;  // 현재 가입한 인원 수

    @Embedded
    private ClubRoles clubRoles;

    @Embedded
    private Participants participants;

    protected Club(final String name, final String description, final int maxParticipantCount) {
    }

    public Club(final String name,
                final String description,
                final int maxParticipantCount,
                final Member member) {
        this.name = name;
        this.description = description;
        this.maxParticipantCount = maxParticipantCount;
        this.clubRoles = ClubRoles.defaultRoles(this);
        this.participants = Participants.initWithPresident(new Participant(member, this, findDefaultRoleByCategory(PRESIDENT)));
        participantCountUp();
    }

    private void participantCountUp() {
        if (currentParticipantCount >= maxParticipantCount) {
            throw new ClubException(CLUB_IS_FULL);
        }
        currentParticipantCount++;
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

    public ClubRoles clubRoles() {
        return clubRoles;
    }

    public Participants participants() {
        return participants;
    }

    public ClubRole findDefaultRoleByCategory(final ClubRoleCategory category) {
        return clubRoles().findDefaultRoleByCategory(category);
    }

    /**
     * 회원을 모임에 등록한다.
     */
    public void registerParticipant(final Member member) {
        // 이미 가입되어있는지 확인
        validateAlreadyRegistered(member);

        participantCountUp();

        participants().register(new Participant(member, this, findDefaultRoleByCategory(GENERAL)));
    }

    private void validateAlreadyRegistered(final Member member) {
        if (participants().findByMemberId(member.id()).isPresent()) {
            throw new ParticipantException(ALREADY_EXIST_PARTICIPANT);
        }
    }

    public Optional<Participant> findParticipantByMemberId(final Long memberId) {
        return participants().findByMemberId(memberId);
    }

    public List<Participant> findAllManager() {
        return participants().findAllManager();
    }
}
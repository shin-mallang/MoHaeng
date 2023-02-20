package com.mohaeng.club.club.domain.model;

import com.mohaeng.club.club.domain.event.ExpelParticipantEvent;
import com.mohaeng.club.club.domain.event.ParticipantClubRoleChangedEvent;
import com.mohaeng.club.club.exception.ClubException;
import com.mohaeng.club.club.exception.ClubRoleException;
import com.mohaeng.club.club.exception.ParticipantException;
import com.mohaeng.common.domain.BaseEntity;
import com.mohaeng.common.event.Events;
import com.mohaeng.member.domain.model.Member;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.List;
import java.util.Optional;

import static com.mohaeng.club.club.domain.model.ClubRoleCategory.GENERAL;
import static com.mohaeng.club.club.domain.model.ClubRoleCategory.PRESIDENT;
import static com.mohaeng.club.club.exception.ClubExceptionType.CLUB_IS_FULL;
import static com.mohaeng.club.club.exception.ClubRoleExceptionType.NOT_FOUND_ROLE;
import static com.mohaeng.club.club.exception.ParticipantExceptionType.*;

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

    protected Club() {
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

    private void participantCountUp() {
        if (currentParticipantCount >= maxParticipantCount) {
            throw new ClubException(CLUB_IS_FULL);
        }
        currentParticipantCount++;
    }

    /**
     * 회원을 모임에서 제거한다.
     * 단, 회장은 모임에서 제거될 수 없다.
     */
    public void deleteParticipant(final Participant participant) {
        participants().delete(participant);

        participantCountDown();
    }

    private void participantCountDown() {
        currentParticipantCount--;
    }

    public List<Participant> findAllParticipant() {
        return participants().participants();
    }

    /**
     * 회원 추방 기능
     */
    public void expel(final Long requesterMemberId, final Long targetParticipantId) {
        Participant requester = findParticipantByMemberId(requesterMemberId).orElseThrow(() -> new ParticipantException(NOT_FOUND_PARTICIPANT));
        Participant target = findParticipantById(targetParticipantId).orElseThrow(() -> new ParticipantException(NOT_FOUND_PARTICIPANT));
        validateExpelAuthority(requester);
        deleteParticipant(target);
        Events.raise(new ExpelParticipantEvent(this, target.member().id(), id()));
    }

    private void validateExpelAuthority(final Participant requester) {
        if (!requester.isPresident()) {
            throw new ParticipantException(NO_AUTHORITY_EXPEL_PARTICIPANT);
        }
    }

    /**
     * 대상 참여자 역할 변경
     */
    public void changeParticipantRole(final Long requesterMemberId,
                                      final Long targetParticipantId,
                                      final Long clubRoleId) {
        ClubRole clubRole = findRoleById(clubRoleId).orElseThrow(() -> new ClubRoleException(NOT_FOUND_ROLE));
        validateChangedRoleIsPresident(clubRole);
        Participant requester = findParticipantByMemberId(requesterMemberId).orElseThrow(() -> new ParticipantException(NOT_FOUND_PARTICIPANT));
        validateChangeRoleAuthority(requester);
        Participant target = findParticipantById(targetParticipantId).orElseThrow(() -> new ParticipantException(NOT_FOUND_PARTICIPANT));
        target.changeRole(clubRole);

        Events.raise(new ParticipantClubRoleChangedEvent(this,
                target.member().id(),
                id(), clubRole.id(), clubRole.name(),
                clubRole.clubRoleCategory()));
    }

    private void validateChangedRoleIsPresident(final ClubRole clubRole) {
        if (clubRole.clubRoleCategory() == PRESIDENT) {
            throw new ParticipantException(NOT_CHANGE_PRESIDENT_ROLE);
        }
    }

    private void validateChangeRoleAuthority(final Participant requester) {
        if (!requester.isPresident()) {
            throw new ParticipantException(NO_AUTHORITY_CHANGE_PARTICIPANT_ROLE);
        }
    }

    public ClubRole findDefaultRoleByCategory(final ClubRoleCategory category) {
        return clubRoles().findDefaultRoleByCategory(category);
    }

    public Optional<Participant> findParticipantByMemberId(final Long memberId) {
        return participants().findByMemberId(memberId);
    }

    /**
     * ParticipantId로 해당하는 참여자 찾기
     */
    public Optional<Participant> findParticipantById(final Long id) {
        return participants().findById(id);
    }

    public List<Participant> findAllManager() {
        return participants().findAllManager();
    }

    public Participant findPresident() {
        return participants().findPresident();
    }

    public Optional<ClubRole> findRoleById(final Long clubRoleId) {
        return clubRoles.findById(clubRoleId);
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
}
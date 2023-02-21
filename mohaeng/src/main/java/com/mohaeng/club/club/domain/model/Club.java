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

import static com.mohaeng.club.club.domain.model.ClubRoleCategory.GENERAL;
import static com.mohaeng.club.club.domain.model.ClubRoleCategory.PRESIDENT;
import static com.mohaeng.club.club.exception.ClubExceptionType.CLUB_IS_FULL;
import static com.mohaeng.club.club.exception.ClubRoleExceptionType.*;
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
        participants().register(member, this, findDefaultRoleByCategory(GENERAL));
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

    /**
     * 회원 추방 기능
     */
    public void expel(final Long requesterMemberId, final Long targetParticipantId) {
        Participant requester = findParticipantByMemberId(requesterMemberId);
        Participant target = findParticipantById(targetParticipantId);
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
     * 회장만이 역할을 변경할 수 있으며,
     * 회장으로는 변경할 수 없다.
     */
    public void changeParticipantRole(final Long requesterMemberId,
                                      final Long targetParticipantId,
                                      final Long clubRoleId) {
        ClubRole clubRole = findRoleById(clubRoleId);
        validateChangedRoleIsPresident(clubRole);
        Participant requester = findParticipantByMemberId(requesterMemberId);
        validateChangeRoleAuthority(requester);
        Participant target = findParticipantById(targetParticipantId);
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

    /**
     * 모임 역할 생성
     */
    public ClubRole createRole(final Long memberId, final String name, final ClubRoleCategory category) {
        Participant participant = findParticipantByMemberId(memberId);
        validateAuthorityCreateRole(participant);
        return clubRoles.add(this, name, category);
    }

    private void validateAuthorityCreateRole(final Participant participant) {
        if (!participant.isManager()) {
            throw new ClubRoleException(NO_AUTHORITY_CREATE_ROLE);
        }
    }

    /**
     * 역할 이름 변경
     */
    public void changeRoleName(final Long memberId, final Long roleId, final String name) {
        Participant participant = findParticipantByMemberId(memberId);
        clubRoles().changeRoleName(participant.clubRole().clubRoleCategory(), roleId, name);
    }

    /**
     * 역할을 제거한다.
     * 기본 역할이 아닌 역할만 제거 가능하며,
     * 제거된 역할을 가진 참여자들은 해당 범주의 기본 역할로 변경된다
     */
    public void deleteRole(final Long memberId, final Long targetRoleId) {
        Participant participant = findParticipantByMemberId(memberId);
        validateAuthorityDeleteRole(participant);
        ClubRole targetRole = findRoleById(targetRoleId);

        clubRoles().delete(targetRole);
        // TODO 순서???
        List<Participant> changeRoleTargets = findAllParticipantByClubRole(targetRole);
        ClubRole changedRole = findDefaultRoleByCategory(targetRole.clubRoleCategory());
        changeRoleTargets.forEach(it -> it.changeRole(changedRole));
    }

    /* 회장 혹은 임원만이 역할 제거가 가능하다 */
    private void validateAuthorityDeleteRole(final Participant participant) {
        if (!participant.isManager()) {
            throw new ClubRoleException(NO_AUTHORITY_DELETE_ROLE);
        }
    }

    public ClubRole findDefaultRoleByCategory(final ClubRoleCategory category) {
        return clubRoles().findDefaultRoleByCategory(category);
    }

    public ClubRole findRoleById(final Long clubRoleId) {
        return clubRoles.findById(clubRoleId).orElseThrow(() -> new ClubRoleException(NOT_FOUND_ROLE));
    }

    public Participant findParticipantByMemberId(final Long memberId) {
        return participants().findByMemberId(memberId)
                .orElseThrow(() -> new ParticipantException(NOT_FOUND_PARTICIPANT));
    }

    public boolean existParticipantByMemberId(final Long memberId) {
        return participants().findByMemberId(memberId).isPresent();
    }

    /**
     * ParticipantId로 해당하는 참여자 찾기
     */
    public Participant findParticipantById(final Long id) {
        return participants().findById(id)
                .orElseThrow(() -> new ParticipantException(NOT_FOUND_PARTICIPANT));
    }

    public List<Participant> findAllParticipant() {
        return participants().participants();
    }

    public List<Participant> findAllManager() {
        return participants().findAllManager();
    }

    public Participant findPresident() {
        return participants().findPresident();
    }

    private List<Participant> findAllParticipantByClubRole(final ClubRole targetRole) {
        return participants().findAllParticipantByClubRole(targetRole);
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
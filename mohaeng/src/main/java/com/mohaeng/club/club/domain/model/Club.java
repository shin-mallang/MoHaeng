package com.mohaeng.club.club.domain.model;

import com.mohaeng.club.club.exception.ClubRoleException;
import com.mohaeng.club.club.exception.ParticipantException;
import com.mohaeng.common.domain.BaseEntity;
import com.mohaeng.member.domain.model.Member;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.List;

import static com.mohaeng.club.club.domain.model.ClubRoleCategory.GENERAL;
import static com.mohaeng.club.club.domain.model.ClubRoleCategory.PRESIDENT;
import static com.mohaeng.club.club.exception.ClubRoleExceptionType.*;
import static com.mohaeng.club.club.exception.ParticipantExceptionType.NOT_CHANGE_PRESIDENT_ROLE;

@Entity
@Table(name = "club")
public class Club extends BaseEntity {

    private String name;
    private String description;

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
        this.clubRoles = ClubRoles.defaultRoles(this);
        this.participants = Participants.initWithPresident(maxParticipantCount, new Participant(member, this, findDefaultRoleByCategory(PRESIDENT)));
    }

    /**
     * 회원을 모임에 등록한다.
     */
    public void registerParticipant(final Member member) {
        participants.register(member, this, findDefaultRoleByCategory(GENERAL));
    }

    /**
     * 회원을 모임에서 제거한다.
     * 단, 회장은 모임에서 제거될 수 없다.
     */
    public void deleteParticipant(final Participant participant) {
        participants.delete(participant);
    }

    /**
     * 회원 추방 기능
     */
    public void expel(final Long requesterMemberId, final Long targetParticipantId) {
        participants.expel(requesterMemberId, targetParticipantId);
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
        participants.changeParticipantRole(requesterMemberId, targetParticipantId, clubRole);
    }

    private void validateChangedRoleIsPresident(final ClubRole clubRole) {
        if (clubRole.clubRoleCategory() == PRESIDENT) {
            throw new ParticipantException(NOT_CHANGE_PRESIDENT_ROLE);
        }
    }

    /**
     * 모임 역할 생성
     */
    public ClubRole createRole(final Long memberId, final String name, final ClubRoleCategory category) {
        Participant participant = findParticipantByMemberId(memberId);
        validateAuthorityCreateRole(participant);
        return clubRoles.create(this, name, category);
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
        clubRoles.changeRoleName(participant.clubRole().clubRoleCategory(), roleId, name);
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

        clubRoles.delete(targetRole);
        // 쿼리는 나중에 나가므로 삭제 먼저 해도 상관이 없다
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

    /**
     * 기본 역할을 변경한다.
     * 회장과 임원만이 가능하다
     */
    public void changeDefaultRole(final Long memberId, final Long roleId) {
        Participant participant = findParticipantByMemberId(memberId);
        validateAuthorityChangeDefaultRole(participant);
        clubRoles.changeDefaultRole(roleId);
    }

    private void validateAuthorityChangeDefaultRole(final Participant participant) {
        if (!participant.isManager()) {
            throw new ClubRoleException(NO_AUTHORITY_CHANGE_DEFAULT_ROLE);
        }
    }

    /**
     * 회장 역할을 위임한다
     */
    public void delegatePresident(final Long presidentMemberId, final Long candidateParticipantId) {
        participants.delegatePresident(presidentMemberId, candidateParticipantId, findDefaultRoleByCategory(GENERAL));
    }

    public ClubRole findDefaultRoleByCategory(final ClubRoleCategory category) {
        return clubRoles.findDefaultRoleByCategory(category);
    }

    public ClubRole findRoleById(final Long clubRoleId) {
        return clubRoles.findById(clubRoleId);
    }

    public Participant findParticipantByMemberId(final Long memberId) {
        return participants.findByMemberId(memberId);
    }

    public boolean existParticipantByMemberId(final Long memberId) {
        return participants.existByMemberId(memberId);
    }

    public Participant findParticipantById(final Long id) {
        return participants.findById(id);
    }

    public List<Participant> findAllParticipant() {
        return participants.participants();
    }

    public List<Participant> findAllManager() {
        return participants.findAllManager();
    }

    public Participant findPresident() {
        return participants.findPresident();
    }

    private List<Participant> findAllParticipantByClubRole(final ClubRole targetRole) {
        return participants.findAllParticipantByClubRole(targetRole);
    }

    // == Getter ==//
    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public int maxParticipantCount() {
        return participants.maxParticipantCount();
    }

    public int currentParticipantCount() {
        return participants.size();
    }

    public ClubRoles clubRoles() {
        return clubRoles;
    }

    public Participants participants() {
        return participants;
    }
}

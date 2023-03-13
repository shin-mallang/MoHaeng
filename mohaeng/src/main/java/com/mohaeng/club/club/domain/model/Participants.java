package com.mohaeng.club.club.domain.model;

import com.mohaeng.club.club.domain.event.ExpelParticipantEvent;
import com.mohaeng.club.club.domain.event.ParticipantClubRoleChangedEvent;
import com.mohaeng.club.club.exception.ClubException;
import com.mohaeng.club.club.exception.ParticipantException;
import com.mohaeng.common.event.Events;
import com.mohaeng.member.domain.model.Member;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

import static com.mohaeng.club.club.exception.ClubExceptionType.CLUB_IS_FULL;
import static com.mohaeng.club.club.exception.ParticipantExceptionType.*;
import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;

@Embeddable
public class Participants {

    private int maxParticipantCount;

    @OneToMany(mappedBy = "club", fetch = LAZY, cascade = ALL, orphanRemoval = true)
    private List<Participant> participants = new ArrayList<>();

    protected Participants() {
    }

    private Participants(final int maxParticipantCount, final Participant president) {
        this.maxParticipantCount = maxParticipantCount;
        participants.add(president);
    }

    public static Participants initWithPresident(final int maxParticipantCount, final Participant president) {
        validatePresident(president);
        return new Participants(maxParticipantCount, president);
    }

    private static void validatePresident(final Participant president) {
        if (!president.isPresident()) {
            throw new ParticipantException(NOT_PRESIDENT);
        }
    }

    /**
     * 참여자 등록
     */
    public Participant register(final Member member, final Club club, final ClubRole clubRole) {
        validateRegisterParticipantSize();
        validateAlreadyRegistered(member);

        Participant participant = new Participant(member, club, clubRole);
        participants().add(participant);
        return participant;
    }

    private void validateRegisterParticipantSize() {
        if (participants.size() >= maxParticipantCount) {
            throw new ClubException(CLUB_IS_FULL);
        }
    }

    private void validateAlreadyRegistered(final Member member) {
        if (existByMemberId(member.id())) {
            throw new ParticipantException(ALREADY_EXIST_PARTICIPANT);
        }
    }

    /**
     * 참여자 제거
     */
    void delete(final Participant participant) {
        validateDeleteParticipant(participant);
        participants().remove(participant);
    }

    private void validateDeleteParticipant(final Participant participant) {
        if (participant.isPresident()) {
            throw new ParticipantException(PRESIDENT_CAN_NOT_LEAVE_CLUB);
        }
    }

    /**
     * 참여자 추방
     */
    void expel(final Long requesterMemberId, final Long targetParticipantId) {
        Participant requester = findByMemberId(requesterMemberId);
        Participant target = findById(targetParticipantId);
        validateExpelAuthority(requester);
        participants().remove(target);
        Events.raise(new ExpelParticipantEvent(this, target.member().id(), target.club().id()));
    }

    private void validateExpelAuthority(final Participant requester) {
        if (!requester.isPresident()) {
            throw new ParticipantException(NO_AUTHORITY_EXPEL_PARTICIPANT);
        }
    }

    /**
     * 참여자의 역할 변경
     */
    void changeParticipantRole(final Long requesterMemberId, final Long targetParticipantId, final ClubRole clubRole) {
        Participant requester = findByMemberId(requesterMemberId);
        validateChangeRoleAuthority(requester);
        Participant target = findById(targetParticipantId);
        target.changeRole(clubRole);
        Events.raise(new ParticipantClubRoleChangedEvent(this,
                target.member().id(),
                target.club().id(),
                clubRole.id(),
                clubRole.name(),
                clubRole.clubRoleCategory()));
    }

    private void validateChangeRoleAuthority(final Participant requester) {
        if (!requester.isPresident()) {
            throw new ParticipantException(NO_AUTHORITY_CHANGE_PARTICIPANT_ROLE);
        }
    }

    /**
     * 회장 역할 위임 기능
     */
    void delegatePresident(final Long presidentMemberId, final Long candidateParticipantId, final ClubRole generalRole) {
        final Participant originalPresident = findByMemberId(presidentMemberId);
        final ClubRole presidentRole = originalPresident.clubRole();
        validateDeletePresident(originalPresident);

        final Participant candidate = findById(candidateParticipantId);
        candidate.changeRole(presidentRole);
        originalPresident.changeRole(generalRole);
    }

    /**
     * 주어진 역할을 가진 참여자들의 역할을 같은 범주의 기본 역할로 변경한다
     */
    public void replaceDeletedRoleIntoDefault(final ClubRole deletedRole) {
        final List<Participant> changeRoleTargets = findAllParticipantByClubRole(deletedRole);
        final ClubRole changedRole = deletedRole.club().findDefaultRoleByCategory(deletedRole.clubRoleCategory());
        changeRoleTargets.forEach(it -> it.changeRole(changedRole));
    }

    private void validateDeletePresident(final Participant president) {
        if (!president.isPresident()) {
            throw new ParticipantException(NO_AUTHORITY_DELEGATE_PRESIDENT);
        }
    }

    boolean existByMemberId(final Long id) {
        return participants().stream()
                .anyMatch(it -> id.equals(it.member().id()));
    }

    Participant findByMemberId(final Long id) {
        return participants().stream()
                .filter(it -> id.equals(it.member().id()))
                .findAny()
                .orElseThrow(() -> new ParticipantException(NOT_FOUND_PARTICIPANT));
    }

    Participant findById(final Long id) {
        return participants().stream()
                .filter(it -> id.equals(it.id()))
                .findAny()
                .orElseThrow(() -> new ParticipantException(NOT_FOUND_PARTICIPANT));
    }

    Participant findPresident() {
        return participants().stream()
                .filter(it -> it.clubRole().clubRoleCategory() == ClubRoleCategory.PRESIDENT)
                .findAny()
                .orElseThrow(() -> new ParticipantException(NOT_FOUND_PRESIDENT));
    }

    List<Participant> findAllParticipantByClubRole(final ClubRole targetRole) {
        return participants().stream()
                .filter(it -> it.clubRole().equals(targetRole))
                .toList();
    }
    // == Getter == //

    public List<Participant> participants() {
        return participants;
    }

    List<Participant> findAllManager() {
        return participants().stream()
                .filter(Participant::isManager)
                .toList();
    }

    int maxParticipantCount() {
        return maxParticipantCount;
    }

    int size() {
        return participants.size();
    }

    boolean contains(final Participant participant) {
        return participants.contains(participant);
    }
}

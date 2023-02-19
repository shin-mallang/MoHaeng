package com.mohaeng.club.club.domain.model;

import com.mohaeng.club.club.exception.ParticipantException;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mohaeng.club.club.exception.ParticipantExceptionType.NOT_FOUND_PRESIDENT;
import static com.mohaeng.club.club.exception.ParticipantExceptionType.NOT_PRESIDENT;
import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;

@Embeddable
public class Participants {

    @OneToMany(mappedBy = "club", fetch = LAZY, cascade = ALL, orphanRemoval = true)
    private List<Participant> participants = new ArrayList<>();

    protected Participants() {
    }

    public Participants(final Participant president) {
        this.participants = new ArrayList<>();
        this.participants.add(president);
    }

    public static Participants initWithPresident(final Participant president) {
        validatePresident(president);
        return new Participants(president);
    }

    private static void validatePresident(final Participant president) {
        if (!president.isPresident()) {
            throw new ParticipantException(NOT_PRESIDENT);
        }
    }

    public List<Participant> participants() {
        return participants;
    }

    public void register(final Participant participant) {
        this.participants().add(participant);
    }

    public Optional<Participant> findByMemberId(final Long id) {
        return participants().stream()
                .filter(it -> it.member().id().equals(id))
                .findAny();
    }

    public List<Participant> findAllManager() {
        return participants().stream()
                .filter(Participant::isManager)
                .toList();
    }

    public Participant findPresident() {
        return participants().stream()
                .filter(it -> it.clubRole().clubRoleCategory() == ClubRoleCategory.PRESIDENT)
                .findAny()
                .orElseThrow(() -> new ParticipantException(NOT_FOUND_PRESIDENT));
    }
}

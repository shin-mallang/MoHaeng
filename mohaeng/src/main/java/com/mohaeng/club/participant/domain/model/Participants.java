package com.mohaeng.club.participant.domain.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.FetchType.LAZY;

@Embeddable
public class Participants {

    @OneToMany(mappedBy = "club", fetch = LAZY, cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    private List<Participant> participants = new ArrayList<>();

    protected Participants() {
    }

    public Participants(final Participant president) {
        this.participants = new ArrayList<>();
        this.participants.add(president);
    }

    public static Participants initWithPresident(final Participant president) {
        return new Participants(president);
    }

    public List<Participant> participants() {
        return participants;
    }

    public void register(final Participant participant) {
        this.participants().add(participant);
    }

    public Optional<Participant> findByMemberId(final Long id) {
        return participants.stream()
                .filter(it -> it.member().id().equals(id))
                .findAny();
    }
}

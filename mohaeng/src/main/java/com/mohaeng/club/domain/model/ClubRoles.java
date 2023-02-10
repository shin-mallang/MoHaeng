package com.mohaeng.club.domain.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;

@Embeddable
public class ClubRoles {

    @OneToMany(mappedBy = "club", fetch = LAZY, cascade = ALL, orphanRemoval = true)
    private List<ClubRole> clubRoles = new ArrayList<>();

    protected ClubRoles() {
    }

    public ClubRoles(final List<ClubRole> defaultRoles) {
        this.clubRoles = defaultRoles;
    }

    public static ClubRoles defaultRoles(final Club club) {
        return new ClubRoles(ClubRole.defaultRoles(club));
    }
}

package com.mohaeng.club.club.domain.model;

import com.mohaeng.club.club.exception.ClubRoleException;
import com.mohaeng.club.club.exception.ClubRoleExceptionType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mohaeng.club.club.exception.ClubRoleExceptionType.CAN_NOT_CREATE_PRESIDENT_ROLE;
import static com.mohaeng.club.club.exception.ClubRoleExceptionType.DUPLICATED_NAME;
import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;

@Embeddable
public class ClubRoles {

    @OneToMany(mappedBy = "club", fetch = LAZY, cascade = ALL, orphanRemoval = true)
    private List<ClubRole> clubRoles = new ArrayList<>();

    protected ClubRoles() {
    }

    public ClubRoles(final List<ClubRole> defaultRoles) {
        this.clubRoles = new ArrayList<>(defaultRoles);
    }

    public static ClubRoles defaultRoles(final Club club) {
        return new ClubRoles(ClubRole.defaultRoles(club));
    }

    public ClubRole findDefaultRoleByCategory(final ClubRoleCategory category) {
        return clubRoles().stream().filter(ClubRole::isDefault)
                .filter(it -> it.clubRoleCategory() == category)
                .findAny()
                .orElseThrow(() -> new ClubRoleException(ClubRoleExceptionType.NOT_FOUND_DEFAULT_ROLE));
    }

    public Optional<ClubRole> findById(final Long id) {
        return clubRoles().stream()
                .filter(it -> id.equals(it.id()))
                .findAny();
    }

    public List<ClubRole> clubRoles() {
        return clubRoles;
    }

    public ClubRole add(final Club club, final String name, final ClubRoleCategory category) {
        validatePresidentRole(category);
        validateDuplicatedName(name);
        ClubRole clubRole = new ClubRole(name, category, club, false);
        this.clubRoles().add(clubRole);
        return clubRole;
    }

    private void validatePresidentRole(final ClubRoleCategory category) {
        if (category == ClubRoleCategory.PRESIDENT) {
            throw new ClubRoleException(CAN_NOT_CREATE_PRESIDENT_ROLE);
        }
    }

    private void validateDuplicatedName(final String name) {
        this.clubRoles().stream()
                .filter(it -> name.equals(it.name()))
                .findAny()
                .ifPresent((it) -> {
                    throw new ClubRoleException(DUPLICATED_NAME);
                });
    }
}

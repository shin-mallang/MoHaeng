package com.mohaeng.club.club.domain.model;

import com.mohaeng.club.club.exception.ClubRoleException;
import com.mohaeng.club.club.exception.ClubRoleExceptionType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mohaeng.club.club.exception.ClubRoleExceptionType.*;
import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;

@Embeddable
public class ClubRoles {

    @OneToMany(mappedBy = "club", fetch = LAZY, cascade = ALL, orphanRemoval = true)
    private List<ClubRole> clubRoles = new ArrayList<>();

    protected ClubRoles() {
    }

    private ClubRoles(final List<ClubRole> defaultRoles) {
        this.clubRoles = new ArrayList<>(defaultRoles);
    }

    public static ClubRoles defaultRoles(final Club club) {
        return new ClubRoles(ClubRole.defaultRoles(club));
    }

    /* Club에서 호출하여 사용하는 용도 */
    ClubRole add(final Club club, final String name, final ClubRoleCategory category) {
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

    /* Club에서 호출하여 사용하는 용도 */
    void changeRoleName(final ClubRoleCategory requesterRoleCategory, final Long roleId, final String name) {
        ClubRole role = findById(roleId).orElseThrow(() -> new ClubRoleException(NOT_FOUND_ROLE));
        // 회장 -> 모두 가능, 임원 -> 일반 역할만 변경 가능
        validateChangeRoleNameAuthority(requesterRoleCategory, role);
        validateDuplicatedName(name);
        role.changeName(name);
    }

    private void validateChangeRoleNameAuthority(final ClubRoleCategory requesterRoleCategory, final ClubRole role) {
        if (requesterRoleCategory == ClubRoleCategory.GENERAL) {
            throw new ClubRoleException(NO_AUTHORITY_CHANGE_ROLE_NAME);
        }
        if (requesterRoleCategory == ClubRoleCategory.PRESIDENT) {
            return;
        }
        // when OFFICER
        if (role.clubRoleCategory() != ClubRoleCategory.GENERAL) {
            throw new ClubRoleException(NO_AUTHORITY_CHANGE_ROLE_NAME);
        }
    }

    /* Club에서 호출하여 사용하는 용도 */
    void delete(final ClubRole targetRole) {
        validateDeletedTargetIsDefaultRole(targetRole);
        this.clubRoles().remove(targetRole);
    }

    /* 기본 역할은 제거할 수 없다 */
    private void validateDeletedTargetIsDefaultRole(final ClubRole targetRole) {
        if (targetRole.isDefault()) {
            throw new ClubRoleException(CAN_NOT_DELETE_DEFAULT_ROLE);
        }
    }

    /* 해당 역할을 기본 역할로 변경한다.
       Club에서 호출하여 사용하는 용도 */
    void changeDefaultRole(final Long id) {
        ClubRole defaultRoleCandidate = findById(id).orElseThrow(() -> new ClubRoleException(NOT_FOUND_ROLE));
        ClubRole originalDefaultRole = findDefaultRoleByCategory(defaultRoleCandidate.clubRoleCategory());
        defaultRoleCandidate.makeDefault();
        originalDefaultRole.makeNonDefault();
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
}

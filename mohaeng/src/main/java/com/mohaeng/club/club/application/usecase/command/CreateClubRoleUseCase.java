package com.mohaeng.club.club.application.usecase.command;

import com.mohaeng.club.club.domain.model.ClubRoleCategory;

public interface CreateClubRoleUseCase {

    void command(final Command command);

    record Command(
            Long memberId,
            Long clubId,
            String name,
            ClubRoleCategory clubRoleCategory
    ) {
    }
}

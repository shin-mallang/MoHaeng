package com.mohaeng.club.club.application.service.command;

import com.mohaeng.club.club.application.service.ClubCommandTest;
import com.mohaeng.club.club.application.usecase.command.CreateClubRoleUseCase;
import com.mohaeng.club.club.domain.model.ClubRole;
import com.mohaeng.club.club.domain.model.ClubRoleCategory;
import com.mohaeng.club.club.exception.ClubException;
import com.mohaeng.club.club.exception.ClubRoleException;
import com.mohaeng.club.club.exception.ParticipantException;
import com.mohaeng.common.annotation.ApplicationTest;
import com.mohaeng.common.exception.BaseExceptionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.stream.Collectors;

import static com.mohaeng.club.club.domain.model.ClubRoleCategory.GENERAL;
import static com.mohaeng.club.club.domain.model.ClubRoleCategory.OFFICER;
import static com.mohaeng.club.club.exception.ClubExceptionType.NOT_FOUND_CLUB;
import static com.mohaeng.club.club.exception.ClubRoleExceptionType.*;
import static com.mohaeng.club.club.exception.ParticipantExceptionType.NOT_FOUND_PARTICIPANT;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("CreateClubRole(모임 역할 생성) 은")
@ApplicationTest
class CreateClubRoleTest extends ClubCommandTest {

    @Autowired
    private CreateClubRoleUseCase createClubRoleUseCase;

    @Test
    void 회장_혹은_임원은_새로운_역할을_생성할_수_있으며_새로_생성된_역할은_기본_역할이_아니다() {
        // given
        final String generalRoleName = "새로생성한 일반회원역할";
        final String officerRoleName = "새로생성한 임원역할";

        // when
        createClubRoleUseCase.command(
                new CreateClubRoleUseCase.Command(
                        presidentMember.id(), club.id(),
                        generalRoleName, GENERAL
                ));
        createClubRoleUseCase.command(
                new CreateClubRoleUseCase.Command(
                        officer.member().id(), club.id(),
                        officerRoleName, OFFICER
                ));
        flushAndClear();

        // then
        Map<ClubRoleCategory, ClubRole> created = club.clubRoles().clubRoles()
                .stream()
                .filter(it -> !it.isDefault())
                .collect(Collectors.toMap(ClubRole::clubRoleCategory, it -> it));
        assertAll(
                () -> assertThat(created.get(GENERAL).name()).isEqualTo(generalRoleName),
                () -> assertThat(created.get(OFFICER).name()).isEqualTo(officerRoleName)
        );
    }

    @Test
    void 일반_회원이_역할을_생성하려는_경우_예외가_발생한다() {
        // given
        final String generalRoleName = "새로생성한 일반회원역할";
        int before = club.clubRoles().clubRoles().size();

        // when
        BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class, () ->
                createClubRoleUseCase.command(
                        new CreateClubRoleUseCase.Command(
                                general.member().id(), club.id(),
                                generalRoleName, GENERAL
                        ))
        ).exceptionType();

        // then
        assertThat(baseExceptionType).isEqualTo(NO_AUTHORITY_CREATE_ROLE);
        assertThat(club.clubRoles().clubRoles().size()).isEqualTo(before);
    }

    @Test
    void 회원_역할은_새로_생성할_수_없다() {
        // given
        final String roleName = "새로생성한 회장역할";
        int before = club.clubRoles().clubRoles().size();

        // when
        BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class, () ->
                createClubRoleUseCase.command(
                        new CreateClubRoleUseCase.Command(
                                presidentMember.id(),
                                club.id(),
                                roleName,
                                ClubRoleCategory.PRESIDENT
                        )
                )).exceptionType();

        // then
        assertThat(baseExceptionType).isEqualTo(CAN_NOT_CREATE_PRESIDENT_ROLE);
        assertThat(club.clubRoles().clubRoles().size()).isEqualTo(before);
    }

    @Test
    void 모임_내에서_역할_이름이_중복되는_경우_예외가_발생한다() {
        // given
        String duplicated = club.clubRoles().findDefaultRoleByCategory(GENERAL).name();
        int before = club.clubRoles().clubRoles().size();

        // when
        BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class, () ->
                createClubRoleUseCase.command(
                        new CreateClubRoleUseCase.Command(
                                presidentMember.id(),
                                club.id(),
                                duplicated,
                                OFFICER
                        )
                )).exceptionType();

        // then
        assertThat(baseExceptionType).isEqualTo(DUPLICATED_NAME);
        assertThat(club.clubRoles().clubRoles().size()).isEqualTo(before);
    }

    @Test
    void 모임이_없는_경우_예외가_발생한다() {
        // given
        final String generalRoleName = "새로생성한 일반회원역할";

        // when
        BaseExceptionType baseExceptionType = assertThrows(ClubException.class, () ->
                createClubRoleUseCase.command(
                        new CreateClubRoleUseCase.Command(
                                presidentMember.id(), club.id() + 123L,
                                generalRoleName, GENERAL
                        ))
        ).exceptionType();

        // then
        org.assertj.core.api.Assertions.assertThat(baseExceptionType).isEqualTo(NOT_FOUND_CLUB);
    }

    @Test
    void 요청자가_해당_모임에_없는경우_예외가_발생한다() {
        // given
        final String generalRoleName = "새로생성한 일반회원역할";

        // when
        BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class, () ->
                createClubRoleUseCase.command(
                        new CreateClubRoleUseCase.Command(
                                presidentMember.id() + 123L, club.id(),
                                generalRoleName, GENERAL
                        ))
        ).exceptionType();

        // then
        org.assertj.core.api.Assertions.assertThat(baseExceptionType).isEqualTo(NOT_FOUND_PARTICIPANT);
    }
}
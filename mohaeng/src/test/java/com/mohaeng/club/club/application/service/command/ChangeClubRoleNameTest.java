package com.mohaeng.club.club.application.service.command;

import com.mohaeng.club.club.application.service.ClubCommandTest;
import com.mohaeng.club.club.application.usecase.command.ChangeClubRoleNameUseCase;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;

import static com.mohaeng.club.club.domain.model.ClubRoleCategory.GENERAL;
import static com.mohaeng.club.club.domain.model.ClubRoleCategory.PRESIDENT;
import static com.mohaeng.club.club.exception.ClubExceptionType.NOT_FOUND_CLUB;
import static com.mohaeng.club.club.exception.ClubRoleExceptionType.NOT_FOUND_ROLE;
import static com.mohaeng.club.club.exception.ClubRoleExceptionType.NO_AUTHORITY_CHANGE_ROLE_NAME;
import static com.mohaeng.club.club.exception.ParticipantExceptionType.NOT_FOUND_PARTICIPANT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("ChangeClubRoleName(모임 역할 이름 변경) 은")
@ApplicationTest
class ChangeClubRoleNameTest extends ClubCommandTest {

    @Autowired
    private ChangeClubRoleNameUseCase changeClubRoleNameUseCase;

    @ParameterizedTest(name = "회장은 모든 역할의 이름을 변경할 수 있다")
    @EnumSource(mode = EXCLUDE)
    void 회장은_모든_역할의_이름을_변경할_수_있다(final ClubRoleCategory category) {
        // given
        String name = "변경할이름";

        // when
        changeClubRoleNameUseCase.command(
                new ChangeClubRoleNameUseCase.Command(
                        presidentMember.id(),
                        club.id(),
                        club.findDefaultRoleByCategory(category).id(),
                        name
                ));
        flushAndClear();

        // then
        assertThat(club.findRoleById(club.findDefaultRoleByCategory(category).id()).name())
                .isEqualTo(name);
    }

    @Test
    void 임원은_일반_역할의_이름만_변경할_수_있다() {
        // given
        String name = "변경할이름";

        // when
        changeClubRoleNameUseCase.command(
                new ChangeClubRoleNameUseCase.Command(
                        officer.member().id(),
                        club.id(),
                        club.findDefaultRoleByCategory(GENERAL).id(),
                        name
                ));
        flushAndClear();

        // then
        assertThat(club.findRoleById(club.findDefaultRoleByCategory(GENERAL).id()).name())
                .isEqualTo(name);
    }

    @ParameterizedTest(name = "임원이 일반 역할이 아닌 다른 역할의 이름을 변경할 경우 예외가 발생한다")
    @EnumSource(mode = EXCLUDE, value = ClubRoleCategory.class, names = {"GENERAL"})
    void 임원이_일반_역할이_아닌_다른_역할의_이름을_변경할_경우_예외가_발생한다(final ClubRoleCategory category) {
        // given
        String name = "변경할이름";

        // when
        BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class, () ->
                changeClubRoleNameUseCase.command(
                        new ChangeClubRoleNameUseCase.Command(
                                officer.member().id(),
                                club.id(),
                                club.findDefaultRoleByCategory(category).id(),
                                name
                        ))
        ).exceptionType();

        // then
        assertThat(club.findRoleById(club.findDefaultRoleByCategory(category).id()).name())
                .isNotEqualTo(name);
        assertThat(baseExceptionType).isEqualTo(NO_AUTHORITY_CHANGE_ROLE_NAME);
    }

    @ParameterizedTest(name = "일반 회원은 역할의 이름을 변경할 수 없다")
    @EnumSource(mode = EXCLUDE, value = ClubRoleCategory.class)
    void 일반_회원은_역할의_이름을_변경할_수_없다(final ClubRoleCategory category) {
        // given
        String name = "변경할이름";

        // when
        BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class, () ->
                changeClubRoleNameUseCase.command(
                        new ChangeClubRoleNameUseCase.Command(
                                general.member().id(),
                                club.id(),
                                club.findDefaultRoleByCategory(category).id(),
                                name
                        ))
        ).exceptionType();

        // then
        assertThat(club.findRoleById(club.findDefaultRoleByCategory(category).id()).name())
                .isNotEqualTo(name);
        assertThat(baseExceptionType).isEqualTo(NO_AUTHORITY_CHANGE_ROLE_NAME);
    }

    @Test
    void 회원을_찾을_수_없는_경우_예외가_발생한다() {
        // given
        String name = "변경할이름";

        // when
        BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class, () ->
                changeClubRoleNameUseCase.command(
                        new ChangeClubRoleNameUseCase.Command(
                                10000L,
                                club.id(),
                                club.findDefaultRoleByCategory(PRESIDENT).id(),
                                name
                        ))
        ).exceptionType();

        // then
        assertThat(club.findRoleById(club.findDefaultRoleByCategory(PRESIDENT).id()).name())
                .isNotEqualTo(name);
        assertThat(baseExceptionType).isEqualTo(NOT_FOUND_PARTICIPANT);
    }

    @Test
    void 바꿀_역할을_찾을_수_없는_경우_예외가_발생한다() {
        // given
        String name = "변경할이름";

        // when
        BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class, () ->
                changeClubRoleNameUseCase.command(
                        new ChangeClubRoleNameUseCase.Command(
                                general.member().id(),
                                club.id(),
                                10000L,
                                name
                        ))
        ).exceptionType();

        // then
        assertThat(baseExceptionType).isEqualTo(NOT_FOUND_ROLE);
    }

    @Test
    void 모임이_없는_경우_예외가_발생한다() {
        // given
        String name = "변경할이름";

        // when
        BaseExceptionType baseExceptionType = assertThrows(ClubException.class, () ->
                changeClubRoleNameUseCase.command(
                        new ChangeClubRoleNameUseCase.Command(
                                general.member().id(),
                                club.id() + 1233L,
                                club.findDefaultRoleByCategory(PRESIDENT).id(),
                                name
                        ))
        ).exceptionType();

        // then
        assertThat(baseExceptionType).isEqualTo(NOT_FOUND_CLUB);
    }
}
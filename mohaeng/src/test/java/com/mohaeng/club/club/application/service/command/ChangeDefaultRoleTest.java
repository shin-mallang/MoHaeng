package com.mohaeng.club.club.application.service.command;

import com.mohaeng.club.club.application.service.ClubCommandTest;
import com.mohaeng.club.club.application.usecase.command.ChangeDefaultRoleUseCase;
import com.mohaeng.club.club.domain.model.ClubRole;
import com.mohaeng.club.club.domain.model.ClubRoleCategory;
import com.mohaeng.club.club.exception.ClubException;
import com.mohaeng.club.club.exception.ClubRoleException;
import com.mohaeng.club.club.exception.ParticipantException;
import com.mohaeng.common.annotation.ApplicationTest;
import com.mohaeng.common.exception.BaseExceptionType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mohaeng.club.club.domain.model.ClubRoleCategory.GENERAL;
import static com.mohaeng.club.club.domain.model.ClubRoleCategory.OFFICER;
import static com.mohaeng.club.club.exception.ClubExceptionType.NOT_FOUND_CLUB;
import static com.mohaeng.club.club.exception.ClubRoleExceptionType.NOT_FOUND_ROLE;
import static com.mohaeng.club.club.exception.ClubRoleExceptionType.NO_AUTHORITY_CHANGE_DEFAULT_ROLE;
import static com.mohaeng.club.club.exception.ParticipantExceptionType.NOT_FOUND_PARTICIPANT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("ChangeDefaultRole(기본 역할 변경) 은")
@ApplicationTest
class ChangeDefaultRoleTest extends ClubCommandTest {

    @Autowired
    private ChangeDefaultRoleUseCase changeDefaultRoleUseCase;

    private Map<ClubRoleCategory, ClubRole> originalClubRoleMap;

    private ClubRole 새로생성한_일반역할;
    private ClubRole 새로생성한_임원역할;
    private List<ClubRole> 새로생성한_역할들;

    @Override
    @BeforeEach
    protected void 모임을_저장하고_한명의_임원진과_한명의_일반_참여자를_저장한다() {
        super.모임을_저장하고_한명의_임원진과_한명의_일반_참여자를_저장한다();
        originalClubRoleMap = club.clubRoles().clubRoles()
                .stream()
                .collect(Collectors.toMap(ClubRole::clubRoleCategory, it -> it));

        새로생성한_일반역할 = club.createRole(presidentMember.id(), "새로생성한 일반역할", GENERAL);
        새로생성한_임원역할 = club.createRole(presidentMember.id(), "새로생성한 임원역할", OFFICER);
        새로생성한_역할들 = List.of(새로생성한_일반역할, 새로생성한_임원역할);
        flushAndClear();
    }

    @Test
    void 회장은_기본_역할을_변경할_수_있다() {
        // when
        새로생성한_역할들.forEach(it -> {
            changeDefaultRoleUseCase.command(
                    new ChangeDefaultRoleUseCase.Command(presidentMember.id(), club.id(), it.id())
            );
        });

        // then
        새로생성한_역할들.forEach(it -> {
                    assertAll(
                            () -> assertThat(club.findDefaultRoleByCategory(it.clubRoleCategory()).id()).isEqualTo(it.id()),
                            () -> assertThat(club.findRoleById(originalClubRoleMap.get(it.clubRoleCategory()).id()).isDefault()).isFalse()
                    );
                }
        );
    }

    @Test
    void 임원진은_기본_역할을_변경할_수_있다() {
        // when
        새로생성한_역할들.forEach(it -> {
            changeDefaultRoleUseCase.command(
                    new ChangeDefaultRoleUseCase.Command(officer.member().id(), club.id(), it.id())
            );
        });
        flushAndClear();

        // then
        새로생성한_역할들.forEach(it -> {
                    assertAll(
                            () -> assertThat(club.findDefaultRoleByCategory(it.clubRoleCategory()).id()).isEqualTo(it.id()),
                            () -> assertThat(club.findRoleById(originalClubRoleMap.get(it.clubRoleCategory()).id()).isDefault()).isFalse()
                    );
                }
        );
    }

    @Test
    void 기본_역할_변경_시_기존_기본_역할은_기본_역할이_아니게_된다() {
        // when
        회장은_기본_역할을_변경할_수_있다();
        flushAndClear();

        // then
        새로생성한_역할들.forEach(it -> {
                    assertAll(
                            () -> assertThat(club.findRoleById(originalClubRoleMap.get(it.clubRoleCategory()).id()).isDefault()).isFalse()
                    );
                }
        );
    }

    @Test
    void 일반_회원은_기본_역할을_변경할_수_없다() {
        // when
        새로생성한_역할들.forEach(it -> {
            BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class, () ->
                    changeDefaultRoleUseCase.command(
                            new ChangeDefaultRoleUseCase.Command(general.member().id(), club.id(), it.id())
                    )
            ).exceptionType();
            assertThat(baseExceptionType).isEqualTo(NO_AUTHORITY_CHANGE_DEFAULT_ROLE);
        });
        flushAndClear();

        // then
        새로생성한_역할들.forEach(it -> {
            assertAll(
                    () -> assertThat(club.findDefaultRoleByCategory(it.clubRoleCategory())).isNotEqualTo(it),
                    () -> assertThat(it.isDefault()).isFalse()
            );
        });
    }

    @Test
    void 모임이_없는_경우_예외가_발생한다() {
        // when
        BaseExceptionType baseExceptionType = assertThrows(ClubException.class, () ->
                changeDefaultRoleUseCase.command(
                        new ChangeDefaultRoleUseCase.Command(presidentMember.id(), club.id() + 1999L, 새로생성한_임원역할.id())
                )
        ).exceptionType();

        // then
        assertThat(baseExceptionType).isEqualTo(NOT_FOUND_CLUB);
    }

    @Test
    void 참가자가_해당_모임에_없는경우_예외가_발생한다() {
        // when
        BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class, () ->
                changeDefaultRoleUseCase.command(
                        new ChangeDefaultRoleUseCase.Command(presidentMember.id() + 1990L, club.id(), 새로생성한_임원역할.id())
                )
        ).exceptionType();

        // then
        assertThat(baseExceptionType).isEqualTo(NOT_FOUND_PARTICIPANT);
    }

    @Test
    void 역할이_없는_경우_예외가_발생한다() {
        // when
        BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class, () ->
                changeDefaultRoleUseCase.command(
                        new ChangeDefaultRoleUseCase.Command(presidentMember.id(), club.id(), 새로생성한_임원역할.id() + 1999L)
                )
        ).exceptionType();

        // then
        assertThat(baseExceptionType).isEqualTo(NOT_FOUND_ROLE);
    }
}
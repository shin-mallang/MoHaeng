package com.mohaeng.club.club.application.service.command;

import com.mohaeng.club.club.application.service.ClubCommandTest;
import com.mohaeng.club.club.application.usecase.command.DeleteClubRoleUseCase;
import com.mohaeng.club.club.domain.model.ClubRole;
import com.mohaeng.club.club.domain.model.ClubRoleCategory;
import com.mohaeng.club.club.domain.model.Participant;
import com.mohaeng.club.club.exception.ClubException;
import com.mohaeng.club.club.exception.ClubRoleException;
import com.mohaeng.club.club.exception.ParticipantException;
import com.mohaeng.common.annotation.ApplicationTest;
import com.mohaeng.common.exception.BaseExceptionType;
import com.mohaeng.member.domain.model.Member;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.mohaeng.club.club.domain.model.ClubRoleCategory.GENERAL;
import static com.mohaeng.club.club.exception.ClubExceptionType.NOT_FOUND_CLUB;
import static com.mohaeng.club.club.exception.ClubRoleExceptionType.*;
import static com.mohaeng.club.club.exception.ParticipantExceptionType.NOT_FOUND_PARTICIPANT;
import static com.mohaeng.common.fixtures.MemberFixture.member;
import static com.mohaeng.common.util.RepositoryUtil.saveMember;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("DeleteClubRole(모임 역할 제거 기능)은")
@ApplicationTest
class DeleteClubRoleTest extends ClubCommandTest {

    @Autowired
    private DeleteClubRoleUseCase deleteClubRoleUseCase;

    private ClubRole 생성된_일반_역할_1;
    private ClubRole 생성된_임원_역할_1;
    private List<ClubRole> clubRoles;

    @BeforeEach
    protected void init() {
        생성된_일반_역할_1 = club.createRole(presidentMember.id(), "생성일반1", GENERAL);
        생성된_임원_역할_1 = club.createRole(presidentMember.id(), "생성임원1", ClubRoleCategory.OFFICER);

        clubRoles = List.of(생성된_일반_역할_1, 생성된_임원_역할_1);
        flushAndClear();
    }

    @Test
    void 회장은_역할을_제거할_수_있다() {
        // given
        clubRoles.forEach(it ->
                assertDoesNotThrow(() -> club.findRoleById(it.id()))
        );

        // when
        clubRoles.forEach(it ->
                deleteClubRoleUseCase.command(
                        new DeleteClubRoleUseCase.Command(
                                presidentMember.id(),
                                club.id(),
                                it.id()))
        );
        flushAndClear();

        // then
        clubRoles.forEach(it -> {
            // 제거되었으므로 찾을 수 없다
            BaseExceptionType baseExceptionType =
                    assertThrows(ClubRoleException.class, () ->
                            club.findRoleById(it.id())
                    ).exceptionType();
            assertThat(baseExceptionType).isEqualTo(NOT_FOUND_ROLE);
        });
    }

    @Test
    void 임원은_역할을_제거할_수_있다() {
        // given
        clubRoles.forEach(it ->
                assertDoesNotThrow(() -> club.findRoleById(it.id()))
        );

        // when
        clubRoles.forEach(it ->
                deleteClubRoleUseCase.command(
                        new DeleteClubRoleUseCase.Command(
                                officer.member().id(),
                                club.id(),
                                it.id()))
        );
        flushAndClear();

        // then
        clubRoles.forEach(it -> {
            // 제거되었으므로 찾을 수 없다
            BaseExceptionType baseExceptionType =
                    assertThrows(ClubRoleException.class, () ->
                            club.findRoleById(it.id())
                    ).exceptionType();
            assertThat(baseExceptionType).isEqualTo(NOT_FOUND_ROLE);
        });
    }

    @Test
    void 일반_회원은_역할을_제거할_수_없다() {
        // given
        clubRoles.forEach(it ->
                assertDoesNotThrow(() -> club.findRoleById(it.id()))
        );

        // when
        clubRoles.forEach(it -> {
            BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class, () ->
                    deleteClubRoleUseCase.command(
                            new DeleteClubRoleUseCase.Command(
                                    general.member().id(),
                                    club.id(),
                                    it.id()))
            ).exceptionType();
            assertThat(baseExceptionType).isEqualTo(NO_AUTHORITY_DELETE_ROLE);
        });
        flushAndClear();

        // then
        clubRoles.forEach(it ->
                assertDoesNotThrow(() -> club.findRoleById(it.id()))
        );

    }

    @ParameterizedTest(name = "기본 역할을 제거할 수 없다")
    @EnumSource(mode = EXCLUDE)
    void 기본_역할은_제거할_수_없다(final ClubRoleCategory category) {
        // when
        BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class, () ->
                deleteClubRoleUseCase.command(
                        new DeleteClubRoleUseCase.Command(
                                presidentMember.id(),
                                club.id(),
                                club.findDefaultRoleByCategory(category).id()
                        )
                )
        ).exceptionType();

        // then
        assertThat(baseExceptionType).isEqualTo(CAN_NOT_DELETE_DEFAULT_ROLE);
        clubRoles.forEach(it ->
                assertDoesNotThrow(() -> club.findDefaultRoleByCategory(category))
        );
    }

    @Test
    void 제거되는_역할을_가진_참여자들은_해당_분야의_기본_역할로_변경된다() {
        // given
        Member member1 = saveMember(memberRepository, member(null));
        Member member2 = saveMember(memberRepository, member(null));
        club.registerParticipant(member1);
        club.registerParticipant(member2);
        Participant general1 = club.findParticipantByMemberId(member1.id());
        Participant general2 = club.findParticipantByMemberId(member2.id());
        flushAndClear();
        club.changeParticipantRole(presidentMember.id(), general1.id(), 생성된_일반_역할_1.id());
        club.changeParticipantRole(presidentMember.id(), general2.id(), 생성된_일반_역할_1.id());
        flushAndClear();

        // when
        deleteClubRoleUseCase.command(
                new DeleteClubRoleUseCase.Command(
                        presidentMember.id(),
                        club.id(),
                        생성된_일반_역할_1.id())
        );
        flushAndClear();

        // then
        assertAll(
                () -> {
                    BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class, () -> {
                        club.findRoleById(생성된_일반_역할_1.id());
                    }).exceptionType();
                    assertThat(baseExceptionType).isEqualTo(NOT_FOUND_ROLE);
                },
                () -> assertThat(club.findParticipantByMemberId(member1.id()).clubRole())
                        .isEqualTo(club.findDefaultRoleByCategory(GENERAL)),
                () -> assertThat(club.findParticipantByMemberId(member2.id()).clubRole())
                        .isEqualTo(club.findDefaultRoleByCategory(GENERAL))
        );
    }

    @Test
    void 모임이_없는_경우_예외가_발생한다() {
        // when
        BaseExceptionType baseExceptionType = assertThrows(ClubException.class, () ->
                deleteClubRoleUseCase.command(
                        new DeleteClubRoleUseCase.Command(
                                presidentMember.id(),
                                club.id() + 11123L,
                                clubRoles.get(0).id()))
        ).exceptionType();

        // then
        assertThat(baseExceptionType).isEqualTo(NOT_FOUND_CLUB);
    }

    @Test
    void 참가자가_해당_모임에_없는경우_예외가_발생한다() {
        // when
        BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class, () ->
                deleteClubRoleUseCase.command(
                        new DeleteClubRoleUseCase.Command(
                                presidentMember.id() + 123124L,
                                club.id(),
                                clubRoles.get(0).id()))
        ).exceptionType();
        // then
        assertThat(baseExceptionType).isEqualTo(NOT_FOUND_PARTICIPANT);
    }

    @Test
    void 역할이_없는_경우_예외가_발생한다() {
        // when
        BaseExceptionType baseExceptionType = assertThrows(ClubRoleException.class, () ->
                deleteClubRoleUseCase.command(
                        new DeleteClubRoleUseCase.Command(
                                presidentMember.id(),
                                club.id(),
                                12354123L))
        ).exceptionType();

        // then
        assertThat(baseExceptionType).isEqualTo(NOT_FOUND_ROLE);
    }
}
package com.mohaeng.club.club.application.service.command;

import com.mohaeng.club.club.application.service.ClubCommandTest;
import com.mohaeng.club.club.application.usecase.command.LeaveClubUseCase;
import com.mohaeng.club.club.exception.ClubException;
import com.mohaeng.club.club.exception.ParticipantException;
import com.mohaeng.common.annotation.ApplicationTest;
import com.mohaeng.common.exception.BaseExceptionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.mohaeng.club.club.exception.ClubExceptionType.NOT_FOUND_CLUB;
import static com.mohaeng.club.club.exception.ParticipantExceptionType.NOT_FOUND_PARTICIPANT;
import static com.mohaeng.club.club.exception.ParticipantExceptionType.PRESIDENT_CAN_NOT_LEAVE_CLUB;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("LeaveClub(모임 탈) 은")
@ApplicationTest
class LeaveClubTest extends ClubCommandTest {

    @Autowired
    private LeaveClubUseCase leaveClubUseCase;

    @Test
    void 회원을_모임에서_탈퇴시킨다() {
        // given
        assertThat(club.existParticipantByMemberId(general.member().id())).isTrue();
        int before = club.currentParticipantCount();

        // when
        leaveClubUseCase.command(
                new LeaveClubUseCase.Command(general.member().id(), club.id())
        );
        leaveClubUseCase.command(
                new LeaveClubUseCase.Command(officer.member().id(), club.id())
        );

        // then
        flushAndClear();
        club = clubRepository.findById(club.id()).orElse(null);
        assertAll(
                () -> assertThat(club.existParticipantByMemberId(general.member().id())).isFalse(),
                () -> assertThat(club.existParticipantByMemberId(officer.member().id())).isFalse(),
                () -> assertThat(clubRepository.findById(club.id()).get().currentParticipantCount()).isEqualTo(before - 2)
        );
    }

    @Test
    void 모임이_존재하지_않는_경우_예외가_발생한다() {
        // given
        int currentParticipantCount = club.currentParticipantCount();

        // when
        BaseExceptionType baseExceptionType = assertThrows(ClubException.class, () ->
                leaveClubUseCase.command(
                        new LeaveClubUseCase.Command(officer.member().id(), 10000L)
                )).exceptionType();

        // then
        flushAndClear();
        club = clubRepository.findById(club.id()).orElse(null);
        assertAll(
                () -> assertThat(baseExceptionType).isEqualTo(NOT_FOUND_CLUB),
                () -> assertThat(clubRepository.findById(club.id()).get().currentParticipantCount()).isEqualTo(currentParticipantCount)
        );
    }

    @Test
    void 회원이_모임에_존재하지_않는_경우_예외를_발생시킨다() {
        // given
        int currentParticipantCount = club.currentParticipantCount();

        // when
        BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class, () ->
                leaveClubUseCase.command(
                        new LeaveClubUseCase.Command(10000L, club.id())
                )).exceptionType();

        // then
        flushAndClear();
        club = clubRepository.findById(club.id()).orElse(null);
        assertAll(
                () -> assertThat(baseExceptionType).isEqualTo(NOT_FOUND_PARTICIPANT),
                () -> assertThat(clubRepository.findById(club.id()).get().currentParticipantCount()).isEqualTo(currentParticipantCount)
        );
    }

    @Test
    void 회장은_모임에서_탈퇴할_수_없다() {
        // given
        int currentParticipantCount = club.currentParticipantCount();

        // when
        BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class, () ->
                leaveClubUseCase.command(
                        new LeaveClubUseCase.Command(presidentMember.id(), club.id())
                )).exceptionType();

        // then
        flushAndClear();
        club = clubRepository.findById(club.id()).orElse(null);
        assertAll(
                () -> assertThat(baseExceptionType).isEqualTo(PRESIDENT_CAN_NOT_LEAVE_CLUB),
                () -> assertThat(club.existParticipantByMemberId(presidentMember.id())).isTrue(),
                () -> assertThat(clubRepository.findById(club.id()).get().currentParticipantCount()).isEqualTo(currentParticipantCount)
        );
    }
}
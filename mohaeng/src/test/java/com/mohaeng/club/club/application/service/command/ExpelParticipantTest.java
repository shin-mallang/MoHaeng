package com.mohaeng.club.club.application.service.command;

import com.mohaeng.club.club.application.service.ClubCommandTest;
import com.mohaeng.club.club.application.usecase.command.ExpelParticipantUseCase;
import com.mohaeng.club.club.domain.event.ExpelParticipantEvent;
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
import static com.mohaeng.club.club.exception.ParticipantExceptionType.NO_AUTHORITY_EXPEL_PARTICIPANT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("ExpelParticipant(참여자 추방) 은")
@ApplicationTest
class ExpelParticipantTest extends ClubCommandTest {

    @Autowired
    private ExpelParticipant expelParticipant;

    @Test
    void 회원을_모임에서_추방히시키고_이벤트를_발행한다() {
        // given
        int before = club.currentParticipantCount();

        // when
        expelParticipant.command(
                new ExpelParticipantUseCase.Command(presidentMember.id(), club.id(), officer.id())
        );

        // then
        club = clubRepository.findById(club.id()).get();
        assertAll(
                () -> assertThat(club.existParticipantByMemberId(officer.member().id())).isFalse(),
                () -> assertThat(club.currentParticipantCount()).isEqualTo(before - 1),
                () -> assertThat(events.stream(ExpelParticipantEvent.class).count()).isEqualTo(1L)
        );
    }

    @Test
    void 추방하려는_회원이_모임에_존재하지_않는_경우_예외를_발생시킨다() {
        // when
        BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class, () ->
                expelParticipant.command(
                        new ExpelParticipantUseCase.Command(presidentMember.id(), club.id(), 1000L)
                )
        ).exceptionType();

        // then
        flushAndClear();
        club = clubRepository.findById(club.id()).get();
        assertAll(
                () -> assertThat(baseExceptionType).isEqualTo(NOT_FOUND_PARTICIPANT),
                () -> assertThat(events.stream(ExpelParticipantEvent.class).count()).isEqualTo(0L)
        );
    }

    @Test
    void 회장이_아닌_경우_추방할_수_없다() {
        // when
        BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class, () ->
                expelParticipant.command(
                        new ExpelParticipantUseCase.Command(officer.member().id(), club.id(), general.id())
                )
        ).exceptionType();

        // then
        flushAndClear();
        club = clubRepository.findById(club.id()).get();
        assertAll(
                () -> assertThat(baseExceptionType).isEqualTo(NO_AUTHORITY_EXPEL_PARTICIPANT),
                () -> assertThat(club.existParticipantByMemberId(general.member().id())).isTrue(),
                () -> assertThat(events.stream(ExpelParticipantEvent.class).count()).isEqualTo(0L)
        );
    }

    @Test
    void 모임이_없는경우_예외가_발생한다() {
        // when
        BaseExceptionType baseExceptionType = assertThrows(ClubException.class, () ->
                expelParticipant.command(
                        new ExpelParticipantUseCase.Command(president.member().id(), 1000L, general.id())
                )
        ).exceptionType();

        // then
        flushAndClear();
        club = clubRepository.findById(club.id()).get();
        assertAll(
                () -> assertThat(baseExceptionType).isEqualTo(NOT_FOUND_CLUB),
                () -> assertThat(club.existParticipantByMemberId(general.member().id())).isTrue(),
                () -> assertThat(events.stream(ExpelParticipantEvent.class).count()).isEqualTo(0L)
        );
    }

    @Test
    void 요청자가_해당_모임에_가입하지_않은_경우_예외가_발생한다() {
        // when
        BaseExceptionType baseExceptionType = assertThrows(ParticipantException.class, () ->
                expelParticipant.command(
                        new ExpelParticipantUseCase.Command(president.member().id() + 123L, club.id(), general.id())
                )
        ).exceptionType();

        // then
        flushAndClear();
        club = clubRepository.findById(club.id()).get();
        assertAll(
                () -> assertThat(baseExceptionType).isEqualTo(NOT_FOUND_PARTICIPANT),
                () -> assertThat(club.existParticipantByMemberId(general.member().id())).isTrue(),
                () -> assertThat(events.stream(ExpelParticipantEvent.class).count()).isEqualTo(0L)
        );
    }
}
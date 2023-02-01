package com.mohaeng.club.domain.model;

import com.mohaeng.club.exception.ClubException;
import com.mohaeng.common.exception.BaseExceptionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.mohaeng.club.exception.ClubExceptionType.CLUB_IS_EMPTY;
import static com.mohaeng.club.exception.ClubExceptionType.CLUB_IS_FULL;
import static com.mohaeng.common.fixtures.ClubFixture.club;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Club 은 ")
class ClubTest {

    @Nested
    @DisplayName("성공 테스트")
    class SuccessTest {
        @Test
        @DisplayName("participantCountUp 시 모임의 회원 수를 1 증가시킨다.")
        void success_test_1() {
            // given
            Club club = club(null);
            int currentParticipantCount = club.currentParticipantCount();

            // when
            club.participantCountUp();

            // then
            assertThat(club.currentParticipantCount())
                    .isEqualTo(currentParticipantCount + 1);
        }

        @Test
        @DisplayName("participantCountDown 시 모임의 회원 수를 1 감소시킨다.")
        void success_test_2() {
            // given
            Club club = club(null);
            club.participantCountUp();
            club.participantCountUp();
            int currentParticipantCount = club.currentParticipantCount();

            // when
            club.participantCountDown();

            // then
            assertThat(club.currentParticipantCount())
                    .isEqualTo(currentParticipantCount - 1);
        }
    }

    @Nested
    @DisplayName("실패 테스트")
    class FailTest {
        @Test
        @DisplayName("participantCountUp 시 모임의 회원 수가 가득찼다면 예외를 발생시킨다.")
        void fail_test_1() {
            // given
            Club club = new Club("name", "dex", 2);
            club.participantCountUp();
            club.participantCountUp();
            int currentParticipantCount = club.currentParticipantCount();

            // when
            BaseExceptionType baseExceptionType = assertThrows(ClubException.class,
                    club::participantCountUp)
                    .exceptionType();

            // then
            assertThat(baseExceptionType).isEqualTo(CLUB_IS_FULL);
            assertThat(club.currentParticipantCount())
                    .isEqualTo(currentParticipantCount);
        }

        @Test
        @DisplayName("participantCountDown 시 모임의 회원 수가 1인 경우 예외를 발생시킨다.")
        void fail_test_2() {
            // given
            Club club = new Club("name", "dex", 1);
            club.participantCountUp();
            int currentParticipantCount = club.currentParticipantCount();

            // when
            BaseExceptionType baseExceptionType = assertThrows(ClubException.class,
                    club::participantCountDown)
                    .exceptionType();

            // then
            assertThat(baseExceptionType).isEqualTo(CLUB_IS_EMPTY);
            assertThat(club.currentParticipantCount())
                    .isEqualTo(currentParticipantCount);
        }
    }
}
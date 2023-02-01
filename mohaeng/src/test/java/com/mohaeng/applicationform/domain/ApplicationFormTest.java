package com.mohaeng.applicationform.domain;

import com.mohaeng.applicationform.domain.model.ApplicationForm;
import com.mohaeng.applicationform.exception.ApplicationFormException;
import com.mohaeng.club.domain.model.Club;
import com.mohaeng.clubrole.domain.model.ClubRole;
import com.mohaeng.common.exception.BaseExceptionType;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.participant.domain.model.Participant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.mohaeng.applicationform.exception.ApplicationFormExceptionType.ALREADY_PROCESSED_APPLICATION_FORM;
import static com.mohaeng.applicationform.exception.ApplicationFormExceptionType.NO_AUTHORITY_PROCESS_APPLICATION_FORM;
import static com.mohaeng.common.fixtures.ClubFixture.club;
import static com.mohaeng.common.fixtures.MemberFixture.member;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("ApplicationForm 은 ")
class ApplicationFormTest {

    @Nested
    @DisplayName("성공 테스트")
    class SuccessTest {

        @Test
        @DisplayName("create() 시 회원과 모임을 가지고 생성된다.")
        void success_test_1() {
            // given
            Member member = member(1L);
            Club club = club(1L);

            // when
            ApplicationForm applicationForm = ApplicationForm.create(member, club);

            // then
            assertAll(
                    () -> assertThat(applicationForm.applicant()).isEqualTo(member),
                    () -> assertThat(applicationForm.target()).isEqualTo(club)
            );
        }

        @Test
        @DisplayName("처리(process)될 수 있다.")
        void success_test_2() {
            // given
            Member member = member(1L);
            Club club = club(1L);
            ApplicationForm applicationForm = ApplicationForm.create(member, club);
            Participant participant = mock(Participant.class);
            when(participant.isManager()).thenReturn(true);

            // when
            applicationForm.approve(participant, mock(ClubRole.class));

            // then
            assertAll(
                    () -> assertThat(applicationForm.processed()).isTrue()
            );
        }

        @Test
        @DisplayName("회장 혹은 임원이 approve() 시 Participant를 생성하고, 가입 신청서를 처리(process) 상태로 만든다.")
        void success_test_3() {
            // given
            Member member = member(1L);
            Club club = club(1L);
            ApplicationForm applicationForm = ApplicationForm.create(member, club);
            Participant participant = mock(Participant.class);
            when(participant.isManager()).thenReturn(true);

            // when
            Participant created = applicationForm.approve(participant, mock(ClubRole.class));

            // then
            assertAll(
                    () -> assertThat(applicationForm.processed()).isTrue(),
                    () -> assertThat(created).isNotNull()
            );
        }

        @Test
        @DisplayName("회장 혹은 임원이 reject() 시, 가입 신청서를 처리(process) 상태로 만든다.")
        void success_test_4() {
            // given
            Member member = member(1L);
            Club club = club(1L);
            ApplicationForm applicationForm = ApplicationForm.create(member, club);
            Participant participant = mock(Participant.class);
            when(participant.isManager()).thenReturn(true);

            // when
            applicationForm.reject(participant);

            // then
            assertAll(
                    () -> assertThat(applicationForm.processed()).isTrue()
            );
        }
    }

    @Nested
    @DisplayName("실패 테스트")
    class FailTest {

        @Test
        @DisplayName("한 번 처리된 가입 신청서는 또다시 처리하려는 경우 예외를 발생시킨다.")
        void fail_test_1() {
            // given
            Member member = member(1L);
            Club club = club(1L);
            ApplicationForm applicationForm = ApplicationForm.create(member, club);
            Participant participant = mock(Participant.class);
            when(participant.isManager()).thenReturn(true);

            // when
            applicationForm.approve(participant, mock(ClubRole.class));

            // then
            assertThat(assertThrows(ApplicationFormException.class,
                    () -> applicationForm.approve(participant, mock(ClubRole.class))
            ).exceptionType()).isEqualTo(ALREADY_PROCESSED_APPLICATION_FORM);
            assertThat(assertThrows(ApplicationFormException.class,
                    () -> applicationForm.reject(participant)
            ).exceptionType()).isEqualTo(ALREADY_PROCESSED_APPLICATION_FORM);
        }

        @Test
        @DisplayName("approve() 시 회장 혹은 임원이 아닌 경우 예외를 발생하고, 가입 신청서를 처리되지 않는다.")
        void fail_test_2() {
            // given
            Member member = member(1L);
            Club club = club(1L);
            ApplicationForm applicationForm = ApplicationForm.create(member, club);
            Participant participant = mock(Participant.class);
            when(participant.isManager()).thenReturn(false);

            // when
            BaseExceptionType baseExceptionType = assertThrows(ApplicationFormException.class, () ->
                    applicationForm.approve(participant, mock(ClubRole.class))
            ).exceptionType();

            // then
            assertAll(
                    () -> assertThat(applicationForm.processed()).isFalse(),
                    () -> assertThat(baseExceptionType).isEqualTo(NO_AUTHORITY_PROCESS_APPLICATION_FORM)
            );
        }

        @Test
        @DisplayName("reject() 시 회장 혹은 임원이 아닌 경우 예외를 발생하고, 가입 신청서를 처리되지 않는다.")
        void fail_test_3() {
            // given
            Member member = member(1L);
            Club club = club(1L);
            ApplicationForm applicationForm = ApplicationForm.create(member, club);
            Participant participant = mock(Participant.class);
            when(participant.isManager()).thenReturn(false);

            // when
            BaseExceptionType baseExceptionType = assertThrows(ApplicationFormException.class, () ->
                    applicationForm.reject(participant)
            ).exceptionType();

            // then
            assertAll(
                    () -> assertThat(applicationForm.processed()).isFalse(),
                    () -> assertThat(baseExceptionType).isEqualTo(NO_AUTHORITY_PROCESS_APPLICATION_FORM)
            );
        }

    }
}
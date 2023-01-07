package com.mohaeng.applicationform.domain;

import com.mohaeng.applicationform.domain.model.ApplicationForm;
import com.mohaeng.applicationform.exception.AlreadyProcessedApplicationFormException;
import com.mohaeng.club.domain.model.Club;
import com.mohaeng.member.domain.model.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.mohaeng.common.fixtures.ClubFixture.club;
import static com.mohaeng.common.fixtures.MemberFixture.member;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("ApplicationForm 은 ")
class ApplicationFormTest {

    @Test
    @DisplayName("회원과 모임을 가지고 생성된다.")
    void test() {
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
    void processTest() {
        // given
        Member member = member(1L);
        Club club = club(1L);
        ApplicationForm applicationForm = ApplicationForm.create(member, club);

        // when
        applicationForm.process();

        // then
        assertAll(
                () -> assertThat(applicationForm.processed()).isTrue()
        );
    }

    @Test
    @DisplayName("한 번 처리된 가입 신청서는 또다시 처리하려는 경우 예외를 발생시킨다.")
    void reProcessTest() {
        // given
        Member member = member(1L);
        Club club = club(1L);
        ApplicationForm applicationForm = ApplicationForm.create(member, club);

        // when
        applicationForm.process();

        // then
        assertAll(
                () -> assertThatThrownBy(() -> applicationForm.process()).isInstanceOf(AlreadyProcessedApplicationFormException.class)
        );
    }
}
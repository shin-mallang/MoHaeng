package com.mohaeng.applicationform.application.service;

import com.mohaeng.applicationform.application.usecase.RejectJoinClubUseCase;
import com.mohaeng.applicationform.domain.event.ApplicationProcessedEvent;
import com.mohaeng.applicationform.domain.event.OfficerRejectClubJoinApplicationEvent;
import com.mohaeng.applicationform.domain.model.ApplicationForm;
import com.mohaeng.applicationform.domain.repository.ApplicationFormRepository;
import com.mohaeng.applicationform.exception.ApplicationFormException;
import com.mohaeng.club.domain.model.Club;
import com.mohaeng.club.domain.repository.ClubRepository;
import com.mohaeng.clubrole.domain.model.ClubRole;
import com.mohaeng.clubrole.domain.repository.ClubRoleRepository;
import com.mohaeng.common.annotation.ApplicationTest;
import com.mohaeng.common.event.Events;
import com.mohaeng.common.exception.BaseExceptionType;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.member.domain.repository.MemberRepository;
import com.mohaeng.participant.domain.model.Participant;
import com.mohaeng.participant.domain.repository.ParticipantRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static com.mohaeng.applicationform.exception.ApplicationFormExceptionType.NOT_FOUND_APPLICATION_FORM;
import static com.mohaeng.applicationform.exception.ApplicationFormExceptionType.NO_AUTHORITY_PROCESS_APPLICATION_FORM;
import static com.mohaeng.clubrole.domain.model.ClubRole.defaultRoles;
import static com.mohaeng.common.fixtures.ApplicationFormFixture.applicationForm;
import static com.mohaeng.common.fixtures.ClubFixture.club;
import static com.mohaeng.common.fixtures.MemberFixture.member;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ApplicationTest
@DisplayName("RejectJoinClub 은 ")
class RejectJoinClubTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private RejectJoinClubUseCase rejectJoinClubUseCase;

    @Autowired
    private ApplicationFormRepository applicationFormRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ClubRoleRepository clubRoleRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    private ApplicationEventPublisher mockApplicationEventPublisher = mock(ApplicationEventPublisher.class);

    @BeforeEach
    void before() {
        Events.setApplicationEventPublisher(mockApplicationEventPublisher);
    }

    @AfterEach
    void after() {
        Events.setApplicationEventPublisher(applicationEventPublisher);
    }

    @Test
    @DisplayName("가입 신청서를 처리한 후, 회원을 모임에 가입시키지 않는다.")
    void success_test_1() {
        // given
        Club target = clubRepository.save(club(null));
        List<ClubRole> clubRoles = clubRoleRepository.saveAll(defaultRoles(target));
        Member managerMember = memberRepository.save(member(null));
        Participant participant = new Participant(managerMember);
        participant.joinClub(target, clubRoles.get(0));  // 회장으로 가입
        participantRepository.save(participant);

        Member applicant = memberRepository.save(member(null));
        ApplicationForm applicationForm = applicationFormRepository.save(applicationForm(applicant.id(), target.id(), null));

        em.flush();
        em.clear();

        // when
        rejectJoinClubUseCase.command(
                new RejectJoinClubUseCase.Command(applicationForm.id(), managerMember.id())
        );

        // then
        ApplicationForm findApplicationForm = applicationFormRepository.findById(applicationForm.id()).orElseThrow(() -> new IllegalArgumentException("발생하면 안됨"));
        assertAll(
                () -> assertThatThrownBy(() -> em.createQuery("select p from Participant p where p.member = :member", Participant.class)
                        .setParameter("member", applicant)
                        .getSingleResult()).isInstanceOf(NoResultException.class),
                () -> assertThat(findApplicationForm.processed()).isTrue()
        );
    }

    @Test
    @DisplayName("회장이 가입 신청을 처리한 경우 이벤트는 한개만 발행한다.")
    void success_test_2() {
        // given
        Club target = clubRepository.save(club(null));
        List<ClubRole> clubRoles = clubRoleRepository.saveAll(defaultRoles(target));
        Member presidentMember = memberRepository.save(member(null));
        Participant president = new Participant(presidentMember);
        president.joinClub(target, clubRoles.get(0));  // 회장으로 가입
        participantRepository.save(president);

        Member applicant = memberRepository.save(member(null));
        ApplicationForm applicationForm = applicationFormRepository.save(applicationForm(applicant.id(), target.id(), null));

        em.flush();
        em.clear();

        // when
        rejectJoinClubUseCase.command(
                new RejectJoinClubUseCase.Command(applicationForm.id(), presidentMember.id())
        );

        // then
        assertAll(
                () -> verify(mockApplicationEventPublisher, times(1)).publishEvent(any()),
                () -> verify(mockApplicationEventPublisher, times(1)).publishEvent(any(ApplicationProcessedEvent.class)),
                () -> verify(mockApplicationEventPublisher, times(0)).publishEvent(any(OfficerRejectClubJoinApplicationEvent.class))
        );
    }

    @Test
    @DisplayName("임원진이 가입 신청을 처리한 경우 이벤트는 두개가 발행한다.")
    void success_test_3() {
        // given
        Club target = clubRepository.save(club(null));
        List<ClubRole> clubRoles = clubRoleRepository.saveAll(defaultRoles(target));
        Member presidentMember = memberRepository.save(member(null));
        Member officerMember = memberRepository.save(member(null));
        Participant president = new Participant(presidentMember);
        Participant officer = new Participant(officerMember);
        president.joinClub(target, clubRoles.get(0));  // 회장으로 가입
        officer.joinClub(target, clubRoles.get(1));  // 회장으로 가입
        participantRepository.save(president);
        participantRepository.save(officer);

        Member applicant = memberRepository.save(member(null));
        ApplicationForm applicationForm = applicationFormRepository.save(applicationForm(applicant.id(), target.id(), null));

        em.flush();
        em.clear();

        // when
        rejectJoinClubUseCase.command(
                new RejectJoinClubUseCase.Command(applicationForm.id(), officerMember.id())
        );

        // then
        assertAll(
                () -> verify(mockApplicationEventPublisher, times(2)).publishEvent(any()),
                () -> verify(mockApplicationEventPublisher, times(1)).publishEvent(any(ApplicationProcessedEvent.class)),
                () -> verify(mockApplicationEventPublisher, times(1)).publishEvent(any(OfficerRejectClubJoinApplicationEvent.class))
        );
    }

    @Test
    @DisplayName("관리자가 아닌 경우 회원의 가입 신청을 거절할 수 없다.")
    void fail_test_1() {
        // given
        Club target = clubRepository.save(club(null));
        List<ClubRole> clubRoles = clubRoleRepository.saveAll(defaultRoles(target));
        Member managerMember = memberRepository.save(member(null));
        Participant participant = new Participant(managerMember);
        participant.joinClub(target, clubRoles.get(2));  // 일반으로 가입
        participantRepository.save(participant);

        Member applicant = memberRepository.save(member(null));
        ApplicationForm applicationForm = applicationFormRepository.save(applicationForm(applicant.id(), target.id(), null));

        em.flush();
        em.clear();

        // when
        BaseExceptionType baseExceptionType = assertThrows(ApplicationFormException.class, () ->
                rejectJoinClubUseCase.command(
                        new RejectJoinClubUseCase.Command(applicationForm.id(), managerMember.id())
                )
        ).exceptionType();

        ApplicationForm findApplicationForm = applicationFormRepository.findById(applicationForm.id()).orElseThrow(() -> new IllegalArgumentException("발생하면 안됨"));
        assertAll(
                () -> assertThat(baseExceptionType).isEqualTo(NO_AUTHORITY_PROCESS_APPLICATION_FORM),
                () -> assertThat(findApplicationForm.processed()).isFalse()
        );
    }

    @Test
    @DisplayName("이미 처리된 신청서의 경우 또다시 처리될 수 없다.")
    void fail_test_2() {
        // given
        Club target = clubRepository.save(club(null));
        List<ClubRole> clubRoles = clubRoleRepository.saveAll(defaultRoles(target));
        Member managerMember = memberRepository.save(member(null));
        Participant manager = new Participant(managerMember);
        manager.joinClub(target, clubRoles.get(0));  // 회장으로 가입
        participantRepository.save(manager);

        Member applicantMember = memberRepository.save(member(null));
        ApplicationForm applicationForm = applicationFormRepository.save(applicationForm(applicantMember.id(), target.id(), null));
        rejectJoinClubUseCase.command(
                new RejectJoinClubUseCase.Command(applicationForm.id(), managerMember.id())
        );
        em.flush();
        em.clear();

        // when
        BaseExceptionType baseExceptionType = assertThrows(ApplicationFormException.class, () ->
                rejectJoinClubUseCase.command(
                        new RejectJoinClubUseCase.Command(applicationForm.id(), managerMember.id())
                )
        ).exceptionType();

        assertAll(
                () -> assertThat(baseExceptionType).isEqualTo(NOT_FOUND_APPLICATION_FORM)
        );
    }
}
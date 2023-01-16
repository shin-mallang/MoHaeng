package com.mohaeng.applicationform.application.service;

import com.mohaeng.applicationform.application.usecase.ApproveJoinClubUseCase;
import com.mohaeng.applicationform.domain.model.ApplicationForm;
import com.mohaeng.applicationform.domain.repository.ApplicationFormRepository;
import com.mohaeng.applicationform.exception.ApplicationFormException;
import com.mohaeng.club.domain.model.Club;
import com.mohaeng.club.domain.repository.ClubRepository;
import com.mohaeng.clubrole.domain.model.ClubRole;
import com.mohaeng.clubrole.domain.model.ClubRoleCategory;
import com.mohaeng.clubrole.domain.repository.ClubRoleRepository;
import com.mohaeng.common.annotation.ApplicationTest;
import com.mohaeng.common.exception.BaseExceptionType;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.member.domain.repository.MemberRepository;
import com.mohaeng.participant.domain.model.Participant;
import com.mohaeng.participant.domain.repository.ParticipantRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.mohaeng.applicationform.exception.ApplicationFormExceptionType.ALREADY_MEMBER_JOINED_CLUB;
import static com.mohaeng.applicationform.exception.ApplicationFormExceptionType.NO_AUTHORITY_PROCESS_APPLICATION_FORM;
import static com.mohaeng.clubrole.domain.model.ClubRole.defaultRoles;
import static com.mohaeng.common.fixtures.ApplicationFormFixture.applicationForm;
import static com.mohaeng.common.fixtures.ClubFixture.club;
import static com.mohaeng.common.fixtures.MemberFixture.member;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ApplicationTest
@DisplayName("ApproveJoinClub 은 ")
class ApproveJoinClubTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private ApproveJoinClubUseCase approveJoinClubUseCase;

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

    @Test
    @DisplayName("회원을 모임에 가입시킨다.")
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
        System.out.println("-------------------SETTING-------------------");

        // when
        approveJoinClubUseCase.command(
                new ApproveJoinClubUseCase.Command(applicationForm.id(), managerMember.id())
        );

        // then
        Participant member = em.createQuery("select p from Participant p where p.member = :member", Participant.class)
                .setParameter("member", applicant)
                .getSingleResult();
        assertThat(member.clubRole().clubRoleCategory()).isEqualTo(ClubRoleCategory.GENERAL);
    }

    @Test
    @DisplayName("관리자가 아닌 경우 회원을 모임에 가입시킬 수 없다.")
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
        System.out.println("-------------------SETTING-------------------");

        // when
        BaseExceptionType baseExceptionType = assertThrows(ApplicationFormException.class, () ->
                approveJoinClubUseCase.command(
                        new ApproveJoinClubUseCase.Command(applicationForm.id(), managerMember.id())
                )
        ).exceptionType();

        assertThat(baseExceptionType).isEqualTo(NO_AUTHORITY_PROCESS_APPLICATION_FORM);
    }

    @Test
    @DisplayName("이미 가입된 회원의 경우 또다시 가입될 수 없다.")
    void fail_test_2() {
        // given
        Club target = clubRepository.save(club(null));
        List<ClubRole> clubRoles = clubRoleRepository.saveAll(defaultRoles(target));

        Member managerMember = memberRepository.save(member(null));
        Participant manager = new Participant(managerMember);
        manager.joinClub(target, clubRoles.get(0));  // 회장으로 가입
        participantRepository.save(manager);

        Member applicantMember = memberRepository.save(member(null));
        Participant applicant = new Participant(applicantMember);
        applicant.joinClub(target, clubRoles.get(2));  // 일반
        participantRepository.save(applicant);

        ApplicationForm applicationForm = applicationFormRepository.save(applicationForm(applicantMember.id(), target.id(), null));

        em.flush();
        em.clear();
        System.out.println("-------------------SETTING-------------------");

        // when
        BaseExceptionType baseExceptionType = assertThrows(ApplicationFormException.class, () ->
                approveJoinClubUseCase.command(
                        new ApproveJoinClubUseCase.Command(applicationForm.id(), managerMember.id())
                )
        ).exceptionType();

        assertThat(baseExceptionType).isEqualTo(ALREADY_MEMBER_JOINED_CLUB);
    }

    // TODO 회장이 한 경우 이벤트 한개만 발행

    // TODO 임원진이 한 경우 이벤트 두 개 발행
}
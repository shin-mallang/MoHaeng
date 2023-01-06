package com.mohaeng.applicationform.application.service;

import com.mohaeng.applicationform.application.usecase.RequestJoinClubUseCase;
import com.mohaeng.applicationform.domain.event.RequestJoinClubEvent;
import com.mohaeng.applicationform.domain.repository.ApplicationFormRepository;
import com.mohaeng.applicationform.exception.AlreadyJoinedMemberException;
import com.mohaeng.applicationform.exception.AlreadyRequestJoinClubException;
import com.mohaeng.club.domain.model.Club;
import com.mohaeng.club.domain.repository.ClubRepository;
import com.mohaeng.clubrole.domain.model.ClubRole;
import com.mohaeng.clubrole.domain.repository.ClubRoleRepository;
import com.mohaeng.common.annotation.ApplicationTest;
import com.mohaeng.common.event.Events;
import com.mohaeng.common.fixtures.ClubRoleFixture;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.member.domain.repository.MemberRepository;
import com.mohaeng.participant.domain.model.Participant;
import com.mohaeng.participant.domain.repository.ParticipantRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static com.mohaeng.common.fixtures.ApplicationForeFixture.requestJoinClubUseCaseCommand;
import static com.mohaeng.common.fixtures.ClubFixture.club;
import static com.mohaeng.common.fixtures.ClubFixture.clubWithMaxParticipantCount;
import static com.mohaeng.common.fixtures.MemberFixture.member;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ApplicationTest
@DisplayName("RequestJoinClub 은 ")
class RequestJoinClubTest {

    @Autowired
    private RequestJoinClubUseCase requestJoinClubUseCase;

    @Autowired
    private ApplicationFormRepository applicationFormRepository;

    @Autowired
    private ClubRoleRepository clubRoleRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;


    @Test
    @DisplayName("모임에 가입되지 않은 사람많이 가입 신청을 할 수 있다.")
    void test() {
        // given
        Club club = clubRepository.save(club(null));
        Member member = memberRepository.save(member(null));

        // when
        Long applicationFormId = requestJoinClubUseCase.command(requestJoinClubUseCaseCommand(member.id(), club.id()));

        // then
        assertThat(applicationFormId).isNotNull();
    }

    @Test
    @DisplayName("이미 가입된 사람이 또다시 신청하는 경우 예외가 발생한다.")
    void test7() {
        // given
        Club club = clubRepository.save(club(null));
        ClubRole presidentRole = ClubRoleFixture.presidentRole("회장", club);
        ClubRole generalRole = ClubRoleFixture.generalRole("일반", club);
        clubRoleRepository.saveAll(List.of(presidentRole, generalRole));

        Member generalMember = memberRepository.save(member(null));
        Participant president = new Participant(memberRepository.save(member(null)));
        Participant general = new Participant(generalMember);
        participantRepository.save(president);
        participantRepository.save(general);

        president.joinClub(club, presidentRole);
        general.joinClub(club, generalRole);

        // when & then
        assertThatThrownBy(() -> requestJoinClubUseCase.command(requestJoinClubUseCaseCommand(generalMember.id(), club.id())))
                .isInstanceOf(AlreadyJoinedMemberException.class);
    }


    @Test
    @DisplayName("이미 신청하였고, 아직 처리되지 않은 경우 다시 신청할 수 없다.")
    void test2() {
        // given
        Club club = clubRepository.save(club(null));
        Member member = memberRepository.save(member(null));
        requestJoinClubUseCase.command(requestJoinClubUseCaseCommand(member.id(), club.id()));

        // when & then
        assertThatThrownBy(() -> requestJoinClubUseCase.command(requestJoinClubUseCaseCommand(member.id(), club.id())))
                .isInstanceOf(AlreadyRequestJoinClubException.class);
    }

    @Test
    @DisplayName("가입이 거절되었더라도 다시 신청할 수 있다.")
    void test3() {
        // given
        Club club = clubRepository.save(club(null));
        Member member = memberRepository.save(member(null));
        Long applicationFormId = requestJoinClubUseCase.command(requestJoinClubUseCaseCommand(member.id(), club.id()));

        // 가입 처리
        applicationFormRepository.findById(applicationFormId).orElse(null).process();


        // when
        Long reApplicationFormId = requestJoinClubUseCase.command(requestJoinClubUseCaseCommand(member.id(), club.id()));

        // then
        assertThat(reApplicationFormId).isNotNull();
    }

    @Test
    @DisplayName("모임에 이미 사람이 가득 찬 경우에도 가입 신청을 보낼 수 있다.(이는 모임의 최대 인원을 늘리고 가입을 수락할 수 있게 하기 위함이다.)")
    void test4() {
        // given
        Club club = clubRepository.save(clubWithMaxParticipantCount(1));
        club.participantCountUp();  // 모임 참가자 가득 채우기

        Member member = memberRepository.save(member(null));
        Long applicationFormId = requestJoinClubUseCase.command(requestJoinClubUseCaseCommand(member.id(), club.id()));

        // 가입 처리
        applicationFormRepository.findById(applicationFormId).orElse(null).process();

        // when
        Long reApplicationFormId = requestJoinClubUseCase.command(requestJoinClubUseCaseCommand(member.id(), club.id()));

        // then
        assertThat(reApplicationFormId).isNotNull();
    }

    @Test
    @DisplayName("가입 신청을 하게되면 가입 요청 이벤트가 발행된다.")
    void test5() {
        // given
        ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);
        Events.setApplicationEventPublisher(publisher);

        Club club = clubRepository.save(club(null));
        ClubRole presidentRole = ClubRoleFixture.presidentRole("회장", club);
        ClubRole officerRole = ClubRoleFixture.officerRole("임원", club);
        ClubRole generalRole = ClubRoleFixture.generalRole("일반", club);
        clubRoleRepository.saveAll(List.of(presidentRole, officerRole, generalRole));

        Participant officer1 = new Participant(memberRepository.save(member(null)));
        Participant officer2 = new Participant(memberRepository.save(member(null)));
        Participant president = new Participant(memberRepository.save(member(null)));
        Participant general = new Participant(memberRepository.save(member(null)));
        Member applicant = memberRepository.save(member(null));

        officer1.joinClub(club, officerRole);
        officer2.joinClub(club, officerRole);
        president.joinClub(club, presidentRole);
        general.joinClub(club, generalRole);

        // when
        Long applicationFormId = requestJoinClubUseCase.command(requestJoinClubUseCaseCommand(applicant.id(), club.id()));

        // then
        verify(publisher, times(1)).publishEvent(any(RequestJoinClubEvent.class));
        Events.setApplicationEventPublisher(applicationEventPublisher);  // 안해주면 오류
    }

    // TODO 알림 기능 만들고 테스트 추가
    @Disabled("TODO 알림 기능 만들고 테스트 추가")
    @Test
    @DisplayName("가입 신청을 하게되면 `회장`과 `관리자` 에게 알림이 전송된다.")
    void test6() {

        throw new IllegalStateException("TODO: 알림 기능을 만든 이후 테스트를 작성한다.");
        // TODO 알림 기능 만들고 테스트 추가

    }
}
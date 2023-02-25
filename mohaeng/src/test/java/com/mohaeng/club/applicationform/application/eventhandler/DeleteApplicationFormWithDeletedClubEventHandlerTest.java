package com.mohaeng.club.applicationform.application.eventhandler;

import com.mohaeng.club.applicationform.domain.event.DeleteApplicationFormEvent;
import com.mohaeng.club.applicationform.domain.model.ApplicationForm;
import com.mohaeng.club.applicationform.domain.repository.ApplicationFormRepository;
import com.mohaeng.club.club.domain.event.DeleteClubEvent;
import com.mohaeng.club.club.domain.model.Club;
import com.mohaeng.club.club.domain.repository.ClubRepository;
import com.mohaeng.common.EventHandlerTest;
import com.mohaeng.member.domain.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.event.ApplicationEvents;

import java.util.List;

import static com.mohaeng.common.fixtures.ClubFixture.clubWithMember;
import static com.mohaeng.common.fixtures.MemberFixture.member;
import static com.mohaeng.common.util.RepositoryUtil.saveClub;
import static com.mohaeng.common.util.RepositoryUtil.saveMember;
import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("DeleteApplicationFormWithDeleteClubEventHandler(모임 제거 시 가입 신청서 제거 핸들러) 는")
class DeleteApplicationFormWithDeletedClubEventHandlerTest extends EventHandlerTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private ApplicationFormRepository applicationFormRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private ApplicationEvents events;

    private DeleteApplicationFormWithDeleteClubEventHandler handler;

    private Club club;

    @BeforeEach
    void init() {
        handler = new DeleteApplicationFormWithDeleteClubEventHandler(eventHistoryRepository, applicationFormRepository);
        club = saveClub(clubRepository, clubWithMember(saveMember(memberRepository, member(null))));
        applicationFormRepository.save(ApplicationForm.create(club, saveMember(memberRepository, member(null))));
        applicationFormRepository.save(ApplicationForm.create(club, saveMember(memberRepository, member(null))));
        applicationFormRepository.save(ApplicationForm.create(club, saveMember(memberRepository, member(null))));
        flushAndClear();
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
    }

    @Test
    void DeleteClubEvent_이벤트를_받아_해당_모임에_존재하는_가입_신청서를_모두_제거한다() {
        // when
        handler.handle(new DeleteClubEvent(this, List.of(), club.id(), "name", "des"));
        flushAndClear();

        // then
        assertThat(applicationFormRepository.findAll()).isEmpty();
    }

    @Test
    void 가입_신청서_제거_후_가입_신청서를_작성한_사람들에게_알림을_보내기_위해_이벤트를_발행한다() {
        // when
        DeleteClubEvent_이벤트를_받아_해당_모임에_존재하는_가입_신청서를_모두_제거한다();

        // then
        assertThat(events.stream(DeleteApplicationFormEvent.class).count()).isEqualTo(1L);
    }
}
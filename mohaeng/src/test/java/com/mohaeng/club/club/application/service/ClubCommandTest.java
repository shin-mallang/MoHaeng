package com.mohaeng.club.club.application.service;

import com.mohaeng.club.club.domain.model.Club;
import com.mohaeng.club.club.domain.model.Participant;
import com.mohaeng.club.club.domain.repository.ClubRepository;
import com.mohaeng.common.annotation.ApplicationTest;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.member.domain.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.event.ApplicationEvents;

import static com.mohaeng.common.fixtures.ClubFixture.clubWithMember;
import static com.mohaeng.common.fixtures.MemberFixture.member;
import static com.mohaeng.common.fixtures.ParticipantFixture.saveGeneral;
import static com.mohaeng.common.fixtures.ParticipantFixture.saveOfficer;
import static com.mohaeng.common.util.RepositoryUtil.saveClub;
import static com.mohaeng.common.util.RepositoryUtil.saveMember;

@SuppressWarnings("NonAsciiCharacters")
@ApplicationTest
public abstract class ClubCommandTest {

    @Autowired
    protected EntityManager em;

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected ClubRepository clubRepository;

    @Autowired
    protected ApplicationEvents events;

    protected Club club;
    protected Member presidentMember;
    protected Participant president;
    protected Participant officer;
    protected Participant general;

    @BeforeEach
    protected void 모임을_저장하고_한명의_임원진과_한명의_일반_참여자를_저장한다() {
        presidentMember = saveMember(memberRepository, member(null));
        club = saveClub(clubRepository, clubWithMember(presidentMember));
        president = club.participants().findByMemberId(presidentMember.id()).get();
        officer = saveOfficer(memberRepository, club);
        general = saveGeneral(memberRepository, club);
        flushAndClear();
    }

    protected void flushAndClear() {
        em.flush();
        em.clear();
        club = clubRepository.findById(club.id()).get();
    }
}

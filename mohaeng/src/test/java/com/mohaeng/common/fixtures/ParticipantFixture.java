package com.mohaeng.common.fixtures;

import com.mohaeng.club.domain.model.Club;
import com.mohaeng.clubrole.domain.model.ClubRole;
import com.mohaeng.member.domain.model.Member;
import com.mohaeng.participant.domain.model.Participant;
import org.springframework.test.util.ReflectionTestUtils;

public class ParticipantFixture {

    public static Participant participant(final Long id, final Member member, final Club club, final ClubRole clubRole) {
        Participant participant = new Participant(member);
        participant.joinClub(club, clubRole);
        ReflectionTestUtils.setField(participant, "id", id);
        return participant;
    }
}

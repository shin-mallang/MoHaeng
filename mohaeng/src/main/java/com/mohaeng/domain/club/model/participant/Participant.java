package com.mohaeng.domain.club.model.participant;

import com.mohaeng.domain.club.model.club.Club;
import com.mohaeng.domain.club.model.role.ClubRole;
import com.mohaeng.domain.config.BaseEntity;
import com.mohaeng.domain.member.model.Member;
import jakarta.persistence.*;

@Entity
@Table(name = "participant")
public class Participant extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;  // 회원

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id")
    private Club club;  // 가입된 club


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_role_id")
    private ClubRole clubRole;  // 모임에서의 역할

    protected Participant() {
    }

    public Participant(final Member member,
                       final Club club,
                       final ClubRole clubRole) {
        this.member = member;
        this.club = club;
        this.clubRole = clubRole;
    }

    public Member member() {
        return member;
    }

    public Club club() {
        return club;
    }

    public ClubRole clubRole() {
        return clubRole;
    }
}

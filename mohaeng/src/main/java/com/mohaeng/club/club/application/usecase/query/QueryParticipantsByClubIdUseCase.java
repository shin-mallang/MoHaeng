package com.mohaeng.club.club.application.usecase.query;

import com.mohaeng.club.club.domain.model.ClubRoleCategory;
import com.mohaeng.club.club.domain.model.Participant;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

/**
 * 모임 ID로 해당 모임에 가입된 모든 참여자에 대한 정보 조회
 */
public interface QueryParticipantsByClubIdUseCase {

    Page<Result> query(final Query query);

    record Query(
            Long memberId,
            Long clubId
    ) {
    }

    record Result(
            Long participantId,
            Long clubRoleId,
            String clubRoleName,
            ClubRoleCategory clubRoleCategory,
            Long memberId,
            String memberName,
            LocalDateTime participationDate
    ) {
        public static Result from(final Participant participant) {
            return new Result(
                    participant.id(),
                    participant.clubRole().id(),
                    participant.clubRole().name(),
                    participant.clubRole().clubRoleCategory(),
                    participant.member().id(),
                    participant.member().name(),
                    participant.createdAt()
            );
        }
    }
}

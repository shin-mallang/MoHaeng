package com.mohaeng.club.club.application.usecase.query;

import com.mohaeng.club.club.domain.model.Club;

public interface QueryClubByIdUseCase {

    Result query(final Query query);

    record Query(
            Long id
    ) {
    }

    record Result(
            Long id,
            String name,  // 모임의 이름
            String description,  // 모임의 설명
            int maxParticipantCount,  // 최대 참여자 수
            int currentParticipantCount  // 현재 가입한 회원 수
    ) {
        public static Result from(final Club club) {
            return new Result(
                    club.id(),
                    club.name(),
                    club.description(),
                    club.maxParticipantCount(),
                    club.currentParticipantCount()
            );
        }
    }
}
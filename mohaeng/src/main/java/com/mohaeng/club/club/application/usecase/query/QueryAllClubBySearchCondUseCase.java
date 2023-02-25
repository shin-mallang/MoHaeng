package com.mohaeng.club.club.application.usecase.query;

import com.mohaeng.club.club.domain.model.Club;
import com.mohaeng.club.club.domain.repository.ClubQueryRepository.ClubSearchCond;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface QueryAllClubBySearchCondUseCase {

    Page<Result> query(final Query query, Pageable pageable);

    record Query(
            ClubSearchCond clubSearchCond
    ) {
    }

    record Result(
            Long id,
            String name,  // 모임의 이름
            String description,  // 모임의 설명
            int maxParticipantCount,  // 최대 참여자 수
            int currentParticipantCount,  // 현재 가입한 회원 수
            LocalDateTime createdAt
    ) {
        public static Result from(final Club club) {
            return new Result(
                    club.id(),
                    club.name(),
                    club.description(),
                    club.maxParticipantCount(),
                    club.currentParticipantCount(),
                    club.createdAt()
            );
        }
    }
}

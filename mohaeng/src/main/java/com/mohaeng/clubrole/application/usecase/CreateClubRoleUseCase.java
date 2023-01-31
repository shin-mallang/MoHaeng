package com.mohaeng.clubrole.application.usecase;

import com.mohaeng.clubrole.domain.model.ClubRoleCategory;

/**
 * 새로운 모임 역할 생성
 * <p>
 * 구현 : participant 패키지의 CreateClubRole
 * - participant 패키지에 구현한 이유는
 * 현재 Participant -> ClubRole로의 의존성이 있기 때문에, ClubRole 에서 Participant 를 의존해 버리는 경우
 * 사이클이 돈다.
 * 이를 해결하기 위해 구현부를 Participant에 두었다.)
 */
public interface CreateClubRoleUseCase {

    Long command(final Command command);

    record Command(
            Long memberId,
            Long clubId,
            String name,
            ClubRoleCategory clubRoleCategory
    ) {
    }
}

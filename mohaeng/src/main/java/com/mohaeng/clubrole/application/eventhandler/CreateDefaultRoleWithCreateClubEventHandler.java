package com.mohaeng.clubrole.application.eventhandler;

import com.mohaeng.club.domain.event.CreateClubEvent;
import com.mohaeng.club.domain.model.Club;
import com.mohaeng.club.domain.repository.ClubRepository;
import com.mohaeng.club.exception.NotFoundClubException;
import com.mohaeng.clubrole.domain.event.CreateDefaultRoleEvent;
import com.mohaeng.clubrole.domain.model.ClubRole;
import com.mohaeng.clubrole.domain.model.ClubRoleCategory;
import com.mohaeng.clubrole.domain.repository.ClubRoleRepository;
import com.mohaeng.common.domain.BaseEntity;
import com.mohaeng.common.event.EventHandler;
import com.mohaeng.common.event.EventHistoryRepository;
import com.mohaeng.common.event.Events;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class CreateDefaultRoleWithCreateClubEventHandler extends EventHandler<CreateClubEvent> {

    private final ClubRoleRepository clubRoleRepository;
    private final ClubRepository clubRepository;


    public CreateDefaultRoleWithCreateClubEventHandler(final EventHistoryRepository eventHistoryRepository,
                                                       final ClubRepository clubRepository,
                                                       final ClubRoleRepository clubRoleRepository) {
        super(eventHistoryRepository);
        this.clubRepository = clubRepository;
        this.clubRoleRepository = clubRoleRepository;
    }

    @Transactional
    @EventListener
    @Override
    public void handle(final CreateClubEvent event) {
        Club club = clubRepository.findById(event.clubId()).orElseThrow(() -> new NotFoundClubException(event.clubId()));

        List<ClubRole> defaultClubRoles = ClubRole.defaultRoles(club);
        List<ClubRole> clubRoles = clubRoleRepository.saveAll(defaultClubRoles);

        // 기본 역할 생성 이벤트 -> 모임을 생성한 회원을 회장으로 만들기
        Events.raise(new CreateDefaultRoleEvent(this,
                event.memberId(),
                event.clubId(),
                getDefaultPresidentRoleId(clubRoles)));

        process(event);
    }

    private Long getDefaultPresidentRoleId(List<ClubRole> clubs) {
        return clubs.stream()
                .filter(it -> it.clubRoleCategory() == ClubRoleCategory.PRESIDENT)
                .map(BaseEntity::id)
                .findAny().orElseThrow(() -> new IllegalStateException("발생하면 안되는 예외"));
    }
}

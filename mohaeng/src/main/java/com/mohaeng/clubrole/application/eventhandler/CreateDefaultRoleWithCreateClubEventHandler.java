package com.mohaeng.clubrole.application.eventhandler;

import com.mohaeng.club.domain.event.CreateClubEvent;
import com.mohaeng.clubrole.domain.event.CreateDefaultRoleEvent;
import com.mohaeng.clubrole.domain.model.ClubRole;
import com.mohaeng.clubrole.domain.model.ClubRoleCategory;
import com.mohaeng.clubrole.domain.repository.ClubRoleRepository;
import com.mohaeng.common.event.EventHandler;
import com.mohaeng.common.event.EventHistoryRepository;
import com.mohaeng.common.event.Events;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class CreateDefaultRoleWithCreateClubEventHandler extends EventHandler<CreateClubEvent> {

    protected final ClubRoleRepository clubRoleRepository;

    public CreateDefaultRoleWithCreateClubEventHandler(final EventHistoryRepository eventHistoryRepository,
                                                       final ClubRoleRepository clubRoleRepository) {
        super(eventHistoryRepository);
        this.clubRoleRepository = clubRoleRepository;
    }

    @Transactional
    @EventListener
    @Override
    public void handle(final CreateClubEvent event) {
        List<ClubRole> defaultClubRoles = ClubRole.defaultRoles(event.club());
        List<ClubRole> clubRoles = clubRoleRepository.saveAll(defaultClubRoles);

        // 기본 역할 생성 이벤트 -> 모임을 생성한 회원을 회장으로 만들기
        Events.raise(new CreateDefaultRoleEvent(this,
                event.member(),
                event.club(),
                getDefaultPresidentRole(clubRoles)));

        process(event);
    }

    private ClubRole getDefaultPresidentRole(List<ClubRole> clubs) {
        return clubs.stream()
                .filter(it -> it.clubRoleCategory() == ClubRoleCategory.PRESIDENT)
                .findAny().orElseThrow(() -> new IllegalStateException("발생하면 안되는 예외"));
    }
}

package com.mohaeng.application.club.eventhandler.role;

import com.mohaeng.application.config.EventHandler;
import com.mohaeng.common.event.Event;
import com.mohaeng.domain.club.event.club.CreateClubEvent;
import com.mohaeng.domain.club.event.role.CreateDefaultRoleEvent;
import com.mohaeng.domain.club.model.role.ClubRole;
import com.mohaeng.domain.club.model.role.ClubRoleCategory;
import com.mohaeng.domain.club.repository.role.ClubRoleRepository;
import com.mohaeng.domain.config.event.EventHistoryRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Component
public class CreateDefaultRoleWithCreateClubEventHandler extends EventHandler<CreateClubEvent> {

    protected final ClubRoleRepository clubRoleRepository;

    protected CreateDefaultRoleWithCreateClubEventHandler(final EventHistoryRepository eventHistoryRepository,
                                                          final ClubRoleRepository clubRoleRepository) {
        super(eventHistoryRepository);
        this.clubRoleRepository = clubRoleRepository;
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    @Override
    public void handle(final CreateClubEvent event) {
        List<ClubRole> defaultClubRoles = ClubRole.defaultRoles(event.club());
        List<ClubRole> clubRoles = clubRoleRepository.saveAll(defaultClubRoles);

        // 기본 역할 생성 이벤트 -> 모임을 생성한 회원을 회장으로 만들기
        Event.publish(new CreateDefaultRoleEvent(this,
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

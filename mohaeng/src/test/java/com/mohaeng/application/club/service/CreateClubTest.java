package com.mohaeng.application.club.service;

import com.mohaeng.application.club.usecase.CreateClubUseCase;
import com.mohaeng.infrastructure.persistence.database.entity.club.ClubRoleJpaEntity;
import com.mohaeng.infrastructure.persistence.database.repository.club.ClubRepository;
import com.mohaeng.infrastructure.persistence.database.service.club.ClubJpaCommand;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.mohaeng.domain.club.domain.ClubRole.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
@DisplayName("CreateClub 은 ")
@Import({CreateClub.class, ClubJpaCommand.class})
class CreateClubTest {

    @Autowired
    private EntityManager em;
    @Autowired
    private ClubRepository clubRepository;
    @Autowired
    private CreateClubUseCase createClubUseCase;

    @Test
    @DisplayName("모임 이름, 모임 설명, 최대 인원을 가지고 모임을 생성한다.")
    void test() {
        Long presidentId = 10L;
        String name = "sample name";
        String description = "sample description";
        int maxPeopleCount = 100;
        Long clubId = createClubUseCase.command(
                new CreateClubUseCase.Command(presidentId, name, description, maxPeopleCount)
        );
        assertThat(clubId).isNotNull();
    }

    @Test
    @DisplayName("모임 생성 시 기본 모임 역할이 저장된다.")
    void test2() {
        Long presidentId = 10L;
        String name = "sample name";
        String description = "sample description";
        int maxPeopleCount = 100;
        Long clubId = createClubUseCase.command(
                new CreateClubUseCase.Command(presidentId, name, description, maxPeopleCount)
        );

        em.flush();
        em.clear();

        List<ClubRoleJpaEntity> clubRoleJpaEntities = clubRepository.findById(clubId).orElse(null).clubRoles();
        assertThat(clubRoleJpaEntities.size()).isEqualTo(3);
        clubRoleJpaEntities.forEach(it -> {
            switch (it.clubRoleCategory()) {
                case PRESIDENT -> assertThat(it.name()).isEqualTo(defaultPresidentRole().name());
                case OFFICER -> assertThat(it.name()).isEqualTo(defaultOfficerRole().name());
                case GENERAL -> assertThat(it.name()).isEqualTo(defaultGeneralRole().name());
                default -> throw new IllegalStateException();
            }
        });
    }
}
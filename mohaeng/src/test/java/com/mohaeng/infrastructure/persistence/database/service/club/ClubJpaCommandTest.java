package com.mohaeng.infrastructure.persistence.database.service.club;

import com.mohaeng.domain.club.domain.Club;
import com.mohaeng.domain.club.domain.ClubCommand;
import com.mohaeng.infrastructure.persistence.database.entity.club.ClubJpaEntity;
import com.mohaeng.infrastructure.persistence.database.entity.club.ClubRoleJpaEntity;
import com.mohaeng.infrastructure.persistence.database.repository.club.ClubRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static com.mohaeng.domain.club.domain.ClubRole.*;
import static com.mohaeng.domain.club.domain.enums.ClubRoleCategory.PRESIDENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
@DisplayName("ClubJpaCommand 는 ")
@Import(ClubJpaCommand.class)
class ClubJpaCommandTest {

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private ClubCommand clubCommand;

    @Test
    @DisplayName("save() 시 모임과 모임의 역할, 회장을 저장한다.")
    void save() {
        Long presidentId = 10L;
        Long save = clubCommand.save(Club.newClub("name", "des", 100, presidentId));

        ClubJpaEntity clubJpaEntity = clubRepository.findById(save).orElse(null);
        List<ClubRoleJpaEntity> clubRoleJpaEntities = clubJpaEntity.clubRoleJpaEntities();
        clubRoleJpaEntities.forEach(it -> {
            switch (it.clubRoleCategory()) {
                case PRESIDENT -> assertThat(it.name()).isEqualTo(defaultPresidentRole().name());
                case OFFICER -> assertThat(it.name()).isEqualTo(defaultOfficerRole().name());
                case GENERAL -> assertThat(it.name()).isEqualTo(defaultGeneralRole().name());
                default -> throw new IllegalStateException();
            }
        });
        Assertions.assertAll(
                () -> assertThat(clubRoleJpaEntities.size()).isEqualTo(3),
                () -> assertThat(clubJpaEntity.clubMemberJpaEntities().size()).isEqualTo(1),
                () -> assertThat(clubJpaEntity.clubMemberJpaEntities().get(0).clubRoleJpaEntity().isBasicRole()).isTrue(),
                () -> assertThat(clubJpaEntity.clubMemberJpaEntities().get(0).clubRoleJpaEntity().clubRoleCategory()).isEqualTo(PRESIDENT)
        );
    }
}
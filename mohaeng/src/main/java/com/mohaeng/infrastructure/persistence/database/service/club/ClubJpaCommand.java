package com.mohaeng.infrastructure.persistence.database.service.club;

import com.mohaeng.domain.club.domain.Club;
import com.mohaeng.domain.club.domain.ClubCommand;
import com.mohaeng.infrastructure.persistence.database.entity.club.ClubJpaEntity;
import com.mohaeng.infrastructure.persistence.database.entity.club.ClubMemberJpaEntity;
import com.mohaeng.infrastructure.persistence.database.entity.club.ClubRoleJpaEntity;
import com.mohaeng.infrastructure.persistence.database.repository.club.ClubRepository;
import com.mohaeng.infrastructure.persistence.database.service.club.mapper.ClubMemberPersistenceMapper;
import com.mohaeng.infrastructure.persistence.database.service.club.mapper.ClubPersistenceMapper;
import com.mohaeng.infrastructure.persistence.database.service.club.mapper.ClubRolePersistenceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ClubJpaCommand implements ClubCommand {

    private final ClubRepository clubRepository;

    public ClubJpaCommand(final ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    @Override
    public Long save(final Club club) {
        ClubJpaEntity clubJpaEntity = ClubPersistenceMapper.toJpaEntity(club);

        // Role 저장
        List<ClubRoleJpaEntity> clubRoleJpaEntities = ClubRolePersistenceMapper.toJpaEntities(clubJpaEntity, club.clubRoles());
        clubRoleJpaEntities.forEach(clubJpaEntity::addClubRole);

        // 회원 저장
        List<ClubMemberJpaEntity> clubMemberJpaEntities = ClubMemberPersistenceMapper.toJpaEntities(clubRoleJpaEntities, club.clubMembers());
        clubMemberJpaEntities.forEach(clubJpaEntity::addClubMember);

        return clubRepository.save(clubJpaEntity).id();
    }
}

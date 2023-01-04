package com.mohaeng.application.club.service;

import com.mohaeng.application.club.mapper.ClubApplicationMapper;
import com.mohaeng.application.club.usecase.CreateClubUseCase;
import com.mohaeng.domain.club.repository.club.ClubRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class CreateClub implements CreateClubUseCase {

    private final ClubRepository clubRepository;

    public CreateClub(final ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }


    @Override
    public Long command(final Command command) {
        return clubRepository.save(ClubApplicationMapper.toDomainEntity(command))
                .id();
    }
}

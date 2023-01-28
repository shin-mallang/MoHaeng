package com.mohaeng.applicationform.infrastructure.persistence.repository;

import com.mohaeng.applicationform.domain.model.ApplicationForm;
import com.mohaeng.applicationform.domain.repository.ApplicationFormRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JpaApplicationFormRepository extends JpaRepository<ApplicationForm, Long>, ApplicationFormRepository {

    @Override
    @Query("select af from ApplicationForm af join fetch af.target where af.id = :id")
    Optional<ApplicationForm> findWithClubById(@Param("id") final Long id);

    @Override
    @Modifying
    @Query("delete from ApplicationForm af where af.target.id = :clubId")
    void deleteAllByClubId(@Param("clubId") final Long clubId);

    @Override
    @Query("select af from ApplicationForm af join fetch af.applicant where af.target.id = :clubId and af.processed = false")
    List<ApplicationForm> findAllWithApplicantByTargetIdAndProcessedFalse(@Param("clubId") final Long clubId);
}

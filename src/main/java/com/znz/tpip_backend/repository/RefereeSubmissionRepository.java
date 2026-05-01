package com.znz.tpip_backend.repository;

import com.znz.tpip_backend.model.RefereeSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefereeSubmissionRepository extends JpaRepository<RefereeSubmission, Long> {

    // Get submission by referee
    Optional<RefereeSubmission> findByRefereeId(Long refereeId);

    // Check if referee already submitted
    boolean existsByRefereeId(Long refereeId);
}
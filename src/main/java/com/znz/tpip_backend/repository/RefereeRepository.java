package com.znz.tpip_backend.repository;

import com.znz.tpip_backend.model.Referee;
import com.znz.tpip_backend.enums.RefereeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefereeRepository extends JpaRepository<Referee, Long> {

    // ================= BY APPLICATION =================
    List<Referee> findByApplicationId(Long applicationId);

    long countByApplicationId(Long applicationId);

    // ================= STATUS FILTERING =================
    List<Referee> findByStatus(RefereeStatus status);

    List<Referee> findByApplicationIdAndStatus(Long applicationId, RefereeStatus status);

    // ================= EMAIL LOOKUP =================
    Optional<Referee> findByEmailAndApplicationId(String email, Long applicationId);

    // ================= TOKEN LOOKUP (for secure link access) =================
    Optional<Referee> findByToken(String token);
}
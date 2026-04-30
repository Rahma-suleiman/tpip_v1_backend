package com.znz.tpip_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// import com.znz.tpip_backend.enums.ApplicationStatus;
import com.znz.tpip_backend.model.Application;
// import com.znz.tpip_backend.model.Intake;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    Optional<Application> findByApplicantIdAndIntakeId(Long applicantId, Long intakeId);

    boolean existsByApplicantIdAndIntakeId(Long applicantId, Long intakeId);
}

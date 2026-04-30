package com.znz.tpip_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.znz.tpip_backend.model.Intake;

@Repository
public interface IntakeRepository extends JpaRepository<Intake, Long> {

    Optional<Intake> findByActiveTrue();

}

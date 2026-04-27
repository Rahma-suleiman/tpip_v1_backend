package com.znz.tpip_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.znz.tpip_backend.model.Mentor;

@Repository
public interface MentorRepository extends JpaRepository<Mentor, Long> {

    boolean existsByEmail(String email);

    Optional<Mentor> findByEmail(String email);

    
} 


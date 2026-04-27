package com.znz.tpip_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.znz.tpip_backend.model.School;

@Repository
public interface SchoolRepository extends JpaRepository<School, Long> {

    boolean existsByNameIgnoreCase(String normalizedName);

    
} 



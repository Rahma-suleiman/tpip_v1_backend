package com.znz.tpip_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.znz.tpip_backend.enums.ApplicationStatus;
import com.znz.tpip_backend.model.Application;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    boolean existsByUserIdAndStatus(Long id, ApplicationStatus pending);

    
} 

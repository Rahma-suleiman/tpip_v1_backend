package com.znz.tpip_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.znz.tpip_backend.model.InternActivityLog;

@Repository
public interface InternActivityLogRepository extends JpaRepository<InternActivityLog, Long> {

    
} 



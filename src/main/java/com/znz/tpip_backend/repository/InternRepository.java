package com.znz.tpip_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.znz.tpip_backend.enums.InternStatus;
import com.znz.tpip_backend.model.Intern;

@Repository
public interface InternRepository extends JpaRepository<Intern, Long> {

    // boolean existsByUserIdAndStatus(Long id, InternStatus active);

    boolean existsByApplication_User_IdAndStatus(Long id, InternStatus active);

    // boolean existsByUserIdAndStatus(Long id, InternStatus active);

    
} 


package com.znz.tpip_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.znz.tpip_backend.model.Placement;

@Repository
public interface PlacementRepository extends JpaRepository<Placement, Long> {

    Optional<Placement> findByInternId(Long internId);

    
} 


package com.znz.tpip_backend.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.znz.tpip_backend.enums.ExtensionStatus;
// import com.znz.tpip_backend.dto.ExtensionDto;
import com.znz.tpip_backend.model.Extension;

@Repository
public interface ExtensionRepository extends JpaRepository<Extension,Long> {

    // boolean existsByPlacementIdAndStatus(Long id, String name);

    List<Extension> findByPlacementId(Long placementId);

	boolean existsByPlacementIdAndStatus(Long id, ExtensionStatus active);
    
}

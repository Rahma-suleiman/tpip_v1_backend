package com.znz.tpip_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.znz.tpip_backend.enums.EvaluationType;
import com.znz.tpip_backend.model.Evaluation;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {

    boolean existsByPlacementIdAndEvaluationType(Long id, EvaluationType evaluationType);

    List<Evaluation> findByPlacementId(Long placementId);


    
} 



package com.znz.tpip_backend.repository;

import com.znz.tpip_backend.model.Programme;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProgrammeRepository extends JpaRepository<Programme, Long> {

    List<Programme> findByIsActiveTrue();

}
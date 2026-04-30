package com.znz.tpip_backend.repository;

import com.znz.tpip_backend.model.Document;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByApplicationId(Long applicationId);

    // List<Document> findByEducationId(Long educationId);

    // List<Document> findByWorkExperienceId(Long workExperienceId);
}
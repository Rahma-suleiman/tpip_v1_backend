package com.znz.tpip_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.znz.tpip_backend.model.Certificate;


@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    
} 


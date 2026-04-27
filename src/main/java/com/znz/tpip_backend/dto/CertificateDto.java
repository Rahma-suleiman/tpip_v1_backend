package com.znz.tpip_backend.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.znz.tpip_backend.enums.Grade;

import lombok.Data;

@Data
public class CertificateDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String certificateNumber;

    private LocalDate issueDate;

    private String issuedBy;

    private Grade grade;

    private String remarks;
    
    private Boolean approaved;

    // FK
    private Long internId;
}

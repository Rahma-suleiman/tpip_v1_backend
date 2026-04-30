package com.znz.tpip_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.znz.tpip_backend.enums.DocumentType;
import lombok.Data;

@Data
public class DocumentDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private DocumentType documentType;

    private String fileUrl;

    private Boolean isVerified;

    private Long applicationId;

    private Long educationId;

    private Long workExperienceId;
}
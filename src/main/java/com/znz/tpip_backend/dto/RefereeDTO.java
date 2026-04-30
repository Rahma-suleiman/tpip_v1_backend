package com.znz.tpip_backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.znz.tpip_backend.enums.RefereeStatus;

@Data
public class RefereeDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private String fullName;
    private String title;
    private String organization;
    private String email;
    private String phone;
    private String relationship;

    // submission data
    private Integer rating;
    private String narrative;
    private boolean declarationAccepted;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private RefereeStatus status;

    // fk
    private Long applicationId;
}
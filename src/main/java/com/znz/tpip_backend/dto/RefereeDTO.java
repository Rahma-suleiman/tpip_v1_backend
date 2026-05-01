package com.znz.tpip_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.znz.tpip_backend.enums.RefereeStatus;
import lombok.Data;

import java.time.LocalDateTime;
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

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String status;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String token;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime tokenExpiry;

    private Long applicationId;
}
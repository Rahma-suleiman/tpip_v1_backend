package com.znz.tpip_backend.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class IntakeDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    
    private String name;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean active;
}
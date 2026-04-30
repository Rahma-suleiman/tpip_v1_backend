package com.znz.tpip_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ProgrammeDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    
    private String name;
    private String department;
    private String field;
    private String location;
    private Integer availableSlots;
    private Integer durationMonths;
    private String description;
    private String requiredLevel;
}
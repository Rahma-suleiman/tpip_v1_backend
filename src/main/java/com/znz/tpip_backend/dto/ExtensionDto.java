package com.znz.tpip_backend.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.znz.tpip_backend.enums.ExtensionStatus;

import lombok.Data;

@Data
public class ExtensionDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private int extraDays;
    private String reason;

    private LocalDate startDate;
    private LocalDate endDate;

    private ExtensionStatus status;

    // forward r/ship
    private Long placementId;

    private Long evaluationId;
}

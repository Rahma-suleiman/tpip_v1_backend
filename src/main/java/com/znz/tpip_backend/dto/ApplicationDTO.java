package com.znz.tpip_backend.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.znz.tpip_backend.enums.ApplicationStatus;
import com.znz.tpip_backend.enums.PaymentStatus;

@Data
public class ApplicationDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String indexNumber;

    private Integer currentStep;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private ApplicationStatus status;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean locked;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime submittedAt;

    // relationships (lightweight references)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<Long> refereeIds;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long paymentId;

    // computed in service (NOT stored in DB)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int refereeCount;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private PaymentStatus paymentStatus;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<Long> programmeChoiceIds;
}
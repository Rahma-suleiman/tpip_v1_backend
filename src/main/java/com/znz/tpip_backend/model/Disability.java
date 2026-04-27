package com.znz.tpip_backend.model;

import com.znz.tpip_backend.enums.DisabilityType;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Embeddable
public class Disability {

    private Boolean hasDisability = false;

    @Enumerated(EnumType.STRING)
    private DisabilityType disabilityType;

    private String disabilityNeeds;

    // private String disabilityDescription;

    // private Boolean disabilityRequiresAssistance = false;

    // private String disabilityAssistanceDetails;
}
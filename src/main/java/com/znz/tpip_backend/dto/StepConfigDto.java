package com.znz.tpip_backend.dto;

import com.znz.tpip_backend.enums.ApplicationStep;
import lombok.Data;

@Data
public class StepConfigDto {
    private ApplicationStep step;
    private ApplicationStep nextStep;
    private ApplicationStep prevStep;
    private int order;
}
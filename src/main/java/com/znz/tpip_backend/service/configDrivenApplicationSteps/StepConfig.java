package com.znz.tpip_backend.service.configDrivenApplicationSteps;

import com.znz.tpip_backend.enums.ApplicationStep;
import com.znz.tpip_backend.model.Application;

import lombok.Getter;

import java.util.function.Predicate;

@Getter
public class StepConfig {

    private final ApplicationStep step;
    private final ApplicationStep nextStep;
    private final ApplicationStep prevStep;
    private final int order;
    private final Predicate<Application> condition;

    public StepConfig(
            ApplicationStep step,
            ApplicationStep nextStep,
            ApplicationStep prevStep,
            int order,
            Predicate<Application> condition
    ) {
        this.step = step;
        this.nextStep = nextStep;
        this.prevStep = prevStep;
        this.order = order;
        this.condition = condition;
    }
}
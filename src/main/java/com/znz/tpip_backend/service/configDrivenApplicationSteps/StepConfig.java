package com.znz.tpip_backend.service.configDrivenApplicationSteps;

import com.znz.tpip_backend.enums.ApplicationStep;
import com.znz.tpip_backend.model.Application;

import java.util.function.Predicate;

public class StepConfig {

    private final ApplicationStep nextStep;
    private final Predicate<Application> condition;

    public StepConfig(ApplicationStep nextStep, Predicate<Application> condition) {
        this.nextStep = nextStep;
        this.condition = condition;
    }

    public ApplicationStep getNextStep() {
        return nextStep;
    }

    public Predicate<Application> getCondition() {
        return condition;
    }
}
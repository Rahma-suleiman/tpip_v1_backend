package com.znz.tpip_backend.service.adminWorkflow;

import com.znz.tpip_backend.enums.AdminState;
import com.znz.tpip_backend.model.Application;

import java.util.function.Predicate;

public class AdminStepConfig {

    private final AdminState nextState;
    private final Predicate<Application> condition;

    public AdminStepConfig(AdminState nextState,
                           Predicate<Application> condition) {
        this.nextState = nextState;
        this.condition = condition;
    }

    public AdminState getNextState() {
        return nextState;
    }

    public Predicate<Application> getCondition() {
        return condition;
    }
}
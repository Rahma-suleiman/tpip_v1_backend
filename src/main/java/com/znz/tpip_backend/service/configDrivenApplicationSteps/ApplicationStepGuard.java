package com.znz.tpip_backend.service.configDrivenApplicationSteps;

import com.znz.tpip_backend.enums.ApplicationStep;
import com.znz.tpip_backend.model.Application;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStepGuard {

    public void validateStep(Application app, ApplicationStep expectedStep) {

        if (app.isLocked()) {
            throw new IllegalStateException("Application is locked");
        }

        if (app.getCurrentStep() != expectedStep) {
            throw new IllegalStateException(
                    "You cannot access this step. Current step is: "
                            + app.getCurrentStep()
            );
        }
    }
}
package com.znz.tpip_backend.service.configDrivenApplicationSteps;

import com.znz.tpip_backend.model.Application;
import com.znz.tpip_backend.repository.ApplicationRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationWorkflowListener {

    private final ApplicationRepository applicationRepository;
    private final ApplicationWorkflowConfig workflowConfig;

    @EventListener
    public void handle(ApplicationStepUpdatedEvent event) {

        Application app = applicationRepository.findById(event.getApplicationId())
                .orElseThrow(() -> new RuntimeException("Application not found"));

        // STOP if locked
        if (app.isLocked()) return;

        // GET CONFIG FOR CURRENT STEP
        StepConfig config = workflowConfig.getConfig(app.getCurrentStep());

        // CHECK CONDITION
        if (config.getCondition().test(app)) {

            // MOVE TO NEXT STEP
            app.setCurrentStep(config.getNextStep());
        }

        applicationRepository.save(app);
    }
}
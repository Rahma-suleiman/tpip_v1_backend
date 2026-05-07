package com.znz.tpip_backend.service.adminWorkflow;

import com.znz.tpip_backend.enums.AdminState;
import com.znz.tpip_backend.event.AdminWorkflowEvent;
import com.znz.tpip_backend.model.Application;
import com.znz.tpip_backend.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AdminWorkflowListener {

    private final ApplicationRepository applicationRepository;
    private final AdminWorkflowConfig workflowConfig;

    @EventListener
    public void handle(AdminWorkflowEvent event) {

        Application app = applicationRepository.findById(event.getApplicationId())
                .orElseThrow(() -> new RuntimeException("Application not found"));

        // 🔒 Do nothing if locked
        if (app.isLocked()) return;

        AdminState currentState = app.getAdminState();

        if (currentState == null) {
            currentState = AdminState.SUBMITTED;
        }

        List<AdminStepConfig> configs = workflowConfig.getConfigs(currentState);

        // 🔥 Find FIRST valid transition (based on condition)
        for (AdminStepConfig config : configs) {

            if (config.getCondition().test(app)) {

                // Avoid unnecessary update
                if (config.getNextState() != currentState) {
                    app.setAdminState(config.getNextState());
                    applicationRepository.save(app);
                }

                break; // only one transition per event
            }
        }
    }
}
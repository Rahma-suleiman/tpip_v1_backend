// package com.znz.tpip_backend.service.adminWorkflow;

// import com.znz.tpip_backend.enums.AdminState;
// import com.znz.tpip_backend.model.Application;
// import lombok.Getter;
// import org.springframework.stereotype.Component;

// import jakarta.annotation.PostConstruct;
// import java.util.EnumMap;
// import java.util.Map;
// import java.util.function.Predicate;

// @Component
// public class AdminWorkflowConfig {

//     @Getter
//     private final Map<AdminState, AdminStepConfig> workflow =
//             new EnumMap<>(AdminState.class);

//     @PostConstruct
//     public void init() {

//         // ================= REVIEW FLOW =================
//         register(AdminState.SUBMITTED,
//                 AdminState.UNDER_REVIEW,
//                 app -> true);

//         register(AdminState.UNDER_REVIEW,
//                 AdminState.RETURNED_FOR_CORRECTION,
//                 Application::hasMissingDocuments);

//         register(AdminState.UNDER_REVIEW,
//                 AdminState.REVIEWED,
//                 Application::isAdminReviewComplete);

//         // ================= INTERVIEW FLOW =================
//         register(AdminState.REVIEWED,
//                 AdminState.INTERVIEW_SCHEDULED,
//                 Application::requiresInterview);

//         register(AdminState.REVIEWED,
//                 AdminState.UNDER_APPROVAL,
//                 app -> !app.requiresInterview());

//         register(AdminState.INTERVIEW_COMPLETED,
//                 AdminState.UNDER_APPROVAL,
//                 app -> true);

//         // ================= FINAL DECISION =================
//         register(AdminState.UNDER_APPROVAL,
//                 AdminState.APPROVED,
//                 Application::isEligibleForApproval);

//         register(AdminState.UNDER_APPROVAL,
//                 AdminState.REJECTED,
//                 Application::isRejected);

//         register(AdminState.UNDER_APPROVAL,
//                 AdminState.WAITLISTED,
//                 Application::isBorderline);
//     }

//     private void register(AdminState from,
//                           AdminState to,
//                           Predicate<Application> condition) {

//         workflow.put(from, new AdminStepConfig(to, condition));
//     }

//     public AdminStepConfig getConfig(AdminState state) {
//         return workflow.get(state);
//     }
// }

package com.znz.tpip_backend.service.adminWorkflow;

import com.znz.tpip_backend.enums.AdminState;
import com.znz.tpip_backend.model.Application;
import lombok.Getter;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.function.Predicate;

@Component
public class AdminWorkflowConfig {

    @Getter
    private final Map<AdminState, List<AdminStepConfig>> workflow =
            new EnumMap<>(AdminState.class);

    @PostConstruct
    public void init() {

        workflow.clear();

        // ================= START =================
        register(AdminState.SUBMITTED,
                AdminState.UNDER_REVIEW,
                app -> true);

        // ================= REVIEW =================
        register(AdminState.UNDER_REVIEW,
                AdminState.REVIEWED,
                app -> true);

        register(AdminState.UNDER_REVIEW,
                AdminState.RETURNED_FOR_CORRECTION,
                Application::hasMissingDocuments);

        // ================= APPROVAL PIPELINE =================
        register(AdminState.REVIEWED,
                AdminState.UNDER_APPROVAL,
                app -> true);

        // ================= FINAL DECISION =================
        register(AdminState.UNDER_APPROVAL,
                AdminState.APPROVED,
                Application::isEligibleForApproval);

        register(AdminState.UNDER_APPROVAL,
                AdminState.REJECTED,
                app -> true);

        register(AdminState.UNDER_APPROVAL,
                AdminState.WAITLISTED,
                Application::isBorderline);
    }

    private void register(AdminState from,
                          AdminState to,
                          Predicate<Application> condition) {

        workflow
                .computeIfAbsent(from, k -> new ArrayList<>())
                .add(new AdminStepConfig(to, condition));
    }

    public List<AdminStepConfig> getConfigs(AdminState state) {

        List<AdminStepConfig> configs = workflow.get(state);

        if (configs == null || configs.isEmpty()) {
            throw new IllegalStateException(
                    "No workflow config for state: " + state);
        }

        return configs;
    }
}
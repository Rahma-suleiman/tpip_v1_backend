// package com.znz.tpip_backend.service.adminWorkflow;

// import com.znz.tpip_backend.enums.AdminState;
// import com.znz.tpip_backend.event.AdminWorkflowEvent;
// import com.znz.tpip_backend.model.Application;
// import com.znz.tpip_backend.repository.ApplicationRepository;
// import lombok.RequiredArgsConstructor;
// import org.springframework.stereotype.Service;

// @Service
// @RequiredArgsConstructor
// public class AdminReviewService {

//     private final ApplicationRepository applicationRepository;
//     private final AdminEventPublisherService eventPublisher;
//     private final AdminWorkflowConfig workflowConfig;

//     // ================= START REVIEW =================
//     public void startReview(Long applicationId, Long adminId) {

//         Application app = get(applicationId);

//         validateTransition(app.getAdminState(), AdminState.UNDER_REVIEW);

//         app.setAdminState(AdminState.UNDER_REVIEW);

//         applicationRepository.save(app);

//         eventPublisher.publish(app.getId(), adminId, AdminState.UNDER_REVIEW);
//     }

//     // ================= RETURN FOR CORRECTION =================
//     public void returnForCorrection(Long applicationId, Long adminId, String reason) {

//         Application app = get(applicationId);

//         app.setAdminState(AdminState.RETURNED_FOR_CORRECTION);
//         app.addAdminComment(reason);

//         applicationRepository.save(app);

//         eventPublisher.publish(app.getId(), adminId, AdminState.RETURNED_FOR_CORRECTION);
//     }

//     // ================= MARK REVIEWED =================
//     public void markReviewed(Long applicationId, Long adminId) {

//         Application app = get(applicationId);

//         validateTransition(app.getAdminState(), AdminState.REVIEWED);

//         app.setAdminState(AdminState.REVIEWED);

//         applicationRepository.save(app);

//         eventPublisher.publish(app.getId(), adminId, AdminState.REVIEWED);
//     }

//     // ================= APPROVE =================
//     public void approve(Long applicationId, Long adminId) {

//         Application app = get(applicationId);

//         validateTransition(app.getAdminState(), AdminState.APPROVED);

//         app.setAdminState(AdminState.APPROVED);
//         app.setLocked(true);

//         applicationRepository.save(app);

//         eventPublisher.publish(app.getId(), adminId, AdminState.APPROVED);
//     }

//     // ================= REJECT =================
//     public void reject(Long applicationId, Long adminId, String reason) {

//         Application app = get(applicationId);

//         app.setAdminState(AdminState.REJECTED);
//         app.addAdminComment(reason);
//         app.setLocked(true);

//         applicationRepository.save(app);

//         eventPublisher.publish(app.getId(), adminId, AdminState.REJECTED);
//     }

//     // ================= WAITLIST =================
//     public void waitlist(Long applicationId, Long adminId) {

//         Application app = get(applicationId);

//         app.setAdminState(AdminState.WAITLISTED);

//         applicationRepository.save(app);

//         eventPublisher.publish(app.getId(), adminId, AdminState.WAITLISTED);
//     }

//     // ================= GET =================
//     private Application get(Long id) {
//         return applicationRepository.findById(id)
//                 .orElseThrow(() -> new RuntimeException("Application not found"));
//     }

//     // ================= VALIDATION =================
//     private void validateTransition(AdminState from, AdminState to) {

//         AdminStepConfig config = workflowConfig.getConfig(from);

//         if (config == null || config.getNextState() != to) {
//             throw new IllegalStateException(
//                     "Invalid admin state transition: " + from + " → " + to
//             );
//         }
//     }
// }
package com.znz.tpip_backend.service.adminWorkflow;

import com.znz.tpip_backend.enums.AdminState;
import com.znz.tpip_backend.model.Application;
import com.znz.tpip_backend.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminReviewService {

    private final ApplicationRepository applicationRepository;
    private final AdminWorkflowConfig workflowConfig;
    private final AdminEventPublisherService eventPublisher;

    // ================= START REVIEW =================
    public void startReview(Long applicationId) {

        Application app = get(applicationId);

        validate(app, AdminState.UNDER_REVIEW);

        app.setAdminState(AdminState.UNDER_REVIEW);

        applicationRepository.save(app);

        eventPublisher.publish(app.getId(), AdminState.UNDER_REVIEW);
    }

    // ================= RETURN FOR CORRECTION =================
    public void returnForCorrection(Long applicationId, String reason) {

        Application app = get(applicationId);

        validate(app, AdminState.RETURNED_FOR_CORRECTION);

        app.setAdminState(AdminState.RETURNED_FOR_CORRECTION);
        app.addAdminComment(reason);

        applicationRepository.save(app);

        eventPublisher.publish(app.getId(), AdminState.RETURNED_FOR_CORRECTION);
    }

    // ================= MARK REVIEWED =================
    public void markReviewed(Long applicationId) {

        Application app = get(applicationId);

        validate(app, AdminState.REVIEWED);

        app.setAdminState(AdminState.REVIEWED);

        applicationRepository.save(app);

        eventPublisher.publish(app.getId(), AdminState.REVIEWED);
    }

    // ================= MOVE TO APPROVAL =================
    public void moveToApproval(Long applicationId) {

        Application app = get(applicationId);

        validate(app, AdminState.UNDER_APPROVAL);

        app.setAdminState(AdminState.UNDER_APPROVAL);

        applicationRepository.save(app);

        eventPublisher.publish(app.getId(), AdminState.UNDER_APPROVAL);
    }

    // ================= APPROVE =================
    public void approve(Long applicationId) {

        Application app = get(applicationId);

        validate(app, AdminState.APPROVED);

        app.setAdminState(AdminState.APPROVED);
        app.setLocked(true);

        applicationRepository.save(app);

        eventPublisher.publish(app.getId(), AdminState.APPROVED);
    }

    // ================= REJECT =================
    public void reject(Long applicationId, String reason) {

        Application app = get(applicationId);

        validate(app, AdminState.REJECTED);

        app.setAdminState(AdminState.REJECTED);
        app.addAdminComment(reason);
        app.setLocked(true);

        applicationRepository.save(app);

        eventPublisher.publish(app.getId(), AdminState.REJECTED);
    }

    // ================= WAITLIST =================
    public void waitlist(Long applicationId) {

        Application app = get(applicationId);

        validate(app, AdminState.WAITLISTED);

        app.setAdminState(AdminState.WAITLISTED);

        applicationRepository.save(app);

        eventPublisher.publish(app.getId(), AdminState.WAITLISTED);
    }

    // ================= INTERNAL =================
    private Application get(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));
    }

    private void validate(Application app, AdminState target) {

        AdminState current = app.getAdminState();

        if (current == null) {
            current = AdminState.SUBMITTED;
        }

        List<AdminStepConfig> configs = workflowConfig.getConfigs(current);

        boolean valid = configs.stream()
                .anyMatch(cfg ->
                        cfg.getNextState() == target &&
                        cfg.getCondition().test(app)
                );

        if (!valid) {
            throw new IllegalStateException(
                    "Invalid transition: " + current + " → " + target
            );
        }
    }
}
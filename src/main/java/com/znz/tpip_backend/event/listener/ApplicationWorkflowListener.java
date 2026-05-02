package com.znz.tpip_backend.event.listener;

import com.znz.tpip_backend.event.ApplicationStepUpdatedEvent;
import com.znz.tpip_backend.enums.ApplicationStep;
import com.znz.tpip_backend.model.Application;
import com.znz.tpip_backend.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationWorkflowListener {

    private final ApplicationRepository applicationRepository;

    @EventListener
    public void handle(ApplicationStepUpdatedEvent event) {

        Application app = applicationRepository.findById(event.getApplicationId())
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (app.isLocked())
            return;

        evaluateAndAdvance(app);

        applicationRepository.save(app);
    }

    // This allows:Auto jump from PERSONAL → SUBMISSION in one go if everything is
    // filled
    // private void evaluateAndAdvance(Application app) {
    //     boolean moved;

    //     do {
    //         moved = advanceOneStep(app);
    //     } while (moved);
    // }

    private void evaluateAndAdvance(Application app) {
        advanceOneStep(app); // ONLY ONE STEP PER EVENT
    }

    private boolean advanceOneStep(Application app) {

        switch (app.getCurrentStep()) {

            case PERSONAL_INFO -> {
                if (isPersonalInfoComplete(app)) {
                    app.setCurrentStep(ApplicationStep.EDUCATION);
                    return true;
                }
            }
            case EDUCATION -> {
                if (isEducationComplete(app)) {
                    app.setCurrentStep(ApplicationStep.WORK_EXPERIENCE);
                    return true;
                }
            }
            case WORK_EXPERIENCE -> {
                if (isWorkExperienceComplete(app)) {
                    app.setCurrentStep(ApplicationStep.PROGRAMME_CHOICE);
                    return true;
                }
            }
            case PROGRAMME_CHOICE -> {
                if (isProgrammeChoiceComplete(app)) {
                    app.setCurrentStep(ApplicationStep.REFEREES);
                    return true;
                }
            }

            case REFEREES -> {
                if (isRefereesComplete(app)) {
                    app.setCurrentStep(ApplicationStep.PAYMENT);
                    return true;
                }
            }

            case PAYMENT -> {
                if (isPaymentComplete(app)) {
                    app.setCurrentStep(ApplicationStep.SUBMISSION);
                    return true;
                }
            }
            // case PROGRAMME_CHOICE -> {
            // if (isProgrammeChoiceComplete(app)) {
            // app.setCurrentStep(ApplicationStep.SUBMISSION);
            // return true;
            // }
            // }
            default -> {
                return false;
            }
        }

        return false;
    }
    // ================= RULES =================

    private boolean isPersonalInfoComplete(Application app) {
        return app.getApplicant() != null
                && app.getApplicant().getUser() != null;
    }

    private boolean isEducationComplete(Application app) {
        return app.getApplicant().getEducations() != null
                && !app.getApplicant().getEducations().isEmpty();
    }

    private boolean isWorkExperienceComplete(Application app) {
        Boolean hasExp = app.getApplicant().getHasWorkExperience();

        return !Boolean.TRUE.equals(hasExp) ||
                (app.getApplicant().getWorkExperiences() != null
                        && !app.getApplicant().getWorkExperiences().isEmpty());
    }

    private boolean isProgrammeChoiceComplete(Application app) {
        return app.getProgrammeChoices() != null
                && !app.getProgrammeChoices().isEmpty();
    }

    private boolean isRefereesComplete(Application app) {
        return app.getReferees() != null
                && app.getReferees().size() >= 2;
    }

    private boolean isPaymentComplete(Application app) {
        return app.getPayment() != null
                && app.getPayment().getStatus() == com.znz.tpip_backend.enums.PaymentStatus.PAID;
    }
}

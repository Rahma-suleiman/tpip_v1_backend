package com.znz.tpip_backend.service;

import org.springframework.stereotype.Service;

import com.znz.tpip_backend.enums.ApplicationStep;
import com.znz.tpip_backend.enums.PaymentStatus;
import com.znz.tpip_backend.model.Application;
import com.znz.tpip_backend.repository.ApplicationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApplicationWorkflowService {

    private final ApplicationRepository applicationRepository;

    // ================= ENTRY POINT =================
    public void evaluateAndAdvance(Long applicationId) {

        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (app.isLocked()) return;

        switch (app.getCurrentStep()) {

            case PERSONAL_INFO -> {
                if (isPersonalInfoComplete(app)) {
                    move(app, ApplicationStep.EDUCATION);
                }
            }

            case EDUCATION -> {
                if (isEducationComplete(app)) {
                    move(app, ApplicationStep.WORK_EXPERIENCE);
                }
            }

            case WORK_EXPERIENCE -> {
                if (isWorkExperienceComplete(app)) {
                    move(app, ApplicationStep.PROGRAMME_CHOICE);
                }
            }

            case PROGRAMME_CHOICE -> {
                if (isProgrammeChoiceComplete(app)) {
                    move(app, ApplicationStep.REFEREES);
                }
            }

            case REFEREES -> {
                if (isRefereesComplete(app)) {
                    move(app, ApplicationStep.PAYMENT);
                }
            }

            case PAYMENT -> {
                if (isPaymentComplete(app)) {
                    move(app, ApplicationStep.SUBMISSION);
                }
            }

            default -> {}
        }
    }

    private void move(Application app, ApplicationStep next) {
        app.setCurrentStep(next);
        applicationRepository.save(app);
    }

    // ================= COMPLETION CHECKS =================

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

        if (Boolean.FALSE.equals(hasExp)) return true;

        return app.getApplicant().getWorkExperiences() != null
                && !app.getApplicant().getWorkExperiences().isEmpty();
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
                && app.getPayment().getStatus() == PaymentStatus.PAID;
    }
}
package com.znz.tpip_backend.service;

import com.znz.tpip_backend.enums.PaymentStatus;
import com.znz.tpip_backend.model.Application;
import com.znz.tpip_backend.model.Payment;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStepEvaluator {

    public boolean isPersonalInfoComplete(Application app) {
        return app.getApplicant() != null
                && app.getApplicant().getUser() != null;
    }

    public boolean isEducationComplete(Application app) {
        return app.getApplicant().getEducations() != null
                && !app.getApplicant().getEducations().isEmpty();
    }

    public boolean isWorkExperienceComplete(Application app) {
        Boolean hasExp = app.getApplicant().getHasWorkExperience();

        return !Boolean.TRUE.equals(hasExp) ||
                (app.getApplicant().getWorkExperiences() != null
                        && !app.getApplicant().getWorkExperiences().isEmpty());
    }

    public boolean isProgrammeChoiceComplete(Application app) {
        return app.getProgrammeChoices() != null
                && !app.getProgrammeChoices().isEmpty();
    }

    public boolean isRefereesComplete(Application app) {
        return app.getReferees() != null
                && app.getReferees().size() >= 2;
    }

    public boolean isPaymentComplete(Application app) {
        Payment payment = app.getPayment();

        return payment != null
                && payment.getStatus() == PaymentStatus.PAID;
    }
}
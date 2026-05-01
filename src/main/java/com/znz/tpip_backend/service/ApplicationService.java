package com.znz.tpip_backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.znz.tpip_backend.dto.ApplicationDTO;
import com.znz.tpip_backend.dto.ApplicationProgressDto;
// import com.znz.tpip_backend.controller.ApplicationProgressDto;
import com.znz.tpip_backend.enums.ApplicationStatus;
import com.znz.tpip_backend.enums.ApplicationStep;
import com.znz.tpip_backend.enums.PaymentStatus;
import com.znz.tpip_backend.model.Applicant;
import com.znz.tpip_backend.model.Application;
import com.znz.tpip_backend.model.Intake;
import com.znz.tpip_backend.model.Payment;
import com.znz.tpip_backend.repository.ApplicationRepository;
import com.znz.tpip_backend.repository.IntakeRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final IntakeRepository intakeRepository;

    public ApplicationDTO getApplicationDTO(Long applicationId) {

        Application app = getApplication(applicationId);

        return mapToDTO(app);
    }

    // ================= CREATE OR GET =================
    public ApplicationDTO createOrGetApplication(Long applicantId) {

        Intake activeIntake = intakeRepository.findByActiveTrue()
                .orElseThrow(() -> new RuntimeException("No active intake found"));

        Application app = applicationRepository
                .findByApplicantIdAndIntakeId(applicantId, activeIntake.getId())
                .orElseGet(() -> {

                    Application newApp = new Application();

                    Applicant applicant = new Applicant();
                    applicant.setId(applicantId);

                    newApp.setApplicant(applicant);
                    newApp.setIntake(activeIntake);

                    newApp.setStatus(ApplicationStatus.DRAFT);
                    newApp.setCurrentStep(ApplicationStep.PERSONAL_INFO);
                    newApp.setLocked(false);
                    newApp.setIndexNumber(generateApplicationIndex(activeIntake));

                    return applicationRepository.save(newApp);
                });

        return mapToDTO(app);
    }

    // ================= MOVE STEP =================
     public ApplicationDTO moveToStep(Long applicationId, ApplicationStep nextStep) {

        Application app = getApplication(applicationId);

        checkIfLocked(app);

        validateStepCompletion(app, app.getCurrentStep());

        if (!isValidStepTransition(app.getCurrentStep(), nextStep)) {
            throw new IllegalStateException("Invalid step transition");
        }

        app.setCurrentStep(nextStep);

        return mapToDTO(applicationRepository.save(app));
    }

    // ================= SUBMIT =================
       public ApplicationDTO submitApplication(Long applicationId) {

        Application app = getApplication(applicationId);

        checkIfLocked(app);

        if (app.getCurrentStep() != ApplicationStep.SUBMISSION) {
            throw new IllegalStateException("Complete all steps before submission");
        }

        validateSubmissionReadiness(app);

        app.setStatus(ApplicationStatus.SUBMITTED);
        app.setSubmittedAt(LocalDateTime.now());

        app.setLocked(true);
        app.setLockedAt(LocalDateTime.now());

        return mapToDTO(applicationRepository.save(app));
    }

    // ================= STATUS UPDATE =================
    public ApplicationDTO updateStatus(Long applicationId, ApplicationStatus newStatus) {

        Application app = getApplication(applicationId);

        checkIfLocked(app);

        if (!isValidStatusTransition(app.getStatus(), newStatus)) {
            throw new IllegalStateException(
                    "Invalid status transition from " + app.getStatus() + " to " + newStatus);
        }

        app.setStatus(newStatus);

        return mapToDTO(applicationRepository.save(app));
    }

    // ================= COMMON FETCH =================
    private Application getApplication(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));
    }

    // ================= STEP VALIDATION =================
    private boolean isValidStepTransition(ApplicationStep current, ApplicationStep next) {

        // allow going back freely (no restriction)
        if (next.getOrder() < current.getOrder()) {
            return true;
        }

        // only allow moving forward ONE step
        return next.getOrder() == current.getOrder() + 1;
    }

    // ================= STATUS VALIDATION =================
    private boolean isValidStatusTransition(ApplicationStatus current, ApplicationStatus next) {

        return switch (current) {

            case DRAFT -> next == ApplicationStatus.SUBMITTED;

            case SUBMITTED -> next == ApplicationStatus.UNDER_REVIEW;

            case UNDER_REVIEW ->
                next == ApplicationStatus.INTERVIEW_SCHEDULED ||
                        next == ApplicationStatus.ACCEPTED ||
                        next == ApplicationStatus.REJECTED;

            case INTERVIEW_SCHEDULED ->
                next == ApplicationStatus.INTERVIEWED;

            case INTERVIEWED ->
                next == ApplicationStatus.ACCEPTED ||
                        next == ApplicationStatus.REJECTED;

            case ACCEPTED ->
                next == ApplicationStatus.PLACED;

            default -> false;
        };
    }

    // ================= INDEX GENERATOR =================
    private String generateApplicationIndex(Intake intake) {

        String year = intake.getName();
        long timestamp = System.currentTimeMillis();

        return "APP/" + year + "/" + timestamp;
    }

    private void validateStepCompletion(Application app, ApplicationStep step) {

        switch (step) {

            case PERSONAL_INFO -> validatePersonalInfo(app);

            case EDUCATION -> validateEducation(app);

            case WORK_EXPERIENCE -> validateWorkExperience(app);

            case PROGRAMME_CHOICE -> validateProgrammeChoice(app);

            case REFEREES -> validateReferees(app);

            case PAYMENT -> validatePayment(app);

            // case SUBMISSION -> validateSubmissionReadiness(app);
            case SUBMISSION -> {
                // FINAL SAFETY CHECK ONLY
                if (!app.isLocked()) {
                    throw new IllegalStateException("Application must be locked after submission");
                }
            }
        }
    }

    private void validatePersonalInfo(Application app) {

        if (app.getApplicant() == null) {
            throw new IllegalStateException("Applicant missing");
        }

        // Example (expand when you add PersonalInfo entity)
        if (app.getApplicant().getUser() == null) {
            throw new IllegalStateException("User profile incomplete");
        }
    }

    private void validateEducation(Application app) {

        if (app.getApplicant().getEducations() == null ||
                app.getApplicant().getEducations().isEmpty()) {

            throw new IllegalStateException("At least one education record required");
        }
    }

    private void validateWorkExperience(Application app) {

        Boolean hasExperience = app.getApplicant().getHasWorkExperience();

        if (Boolean.TRUE.equals(hasExperience)) {

            if (app.getApplicant().getWorkExperiences() == null ||
                    app.getApplicant().getWorkExperiences().isEmpty()) {

                throw new IllegalStateException("Work experience marked YES but no data found");
            }
        }
    }

    private void validateProgrammeChoice(Application app) {

        if (app.getProgrammeChoices() == null || app.getProgrammeChoices().isEmpty()) {
            throw new IllegalStateException("At least one programme choice required");
        }

        if (app.getProgrammeChoices().size() > 3) {
            throw new IllegalStateException("Maximum 3 programme choices allowed");
        }

        // ================= DUPLICATE PROGRAMME CHECK (STREAMS) =================
        boolean hasDuplicateProgrammes = app.getProgrammeChoices()
                .stream()
                .map(pc -> pc.getProgramme().getId())
                .distinct()
                .count() != app.getProgrammeChoices().size();

        if (hasDuplicateProgrammes) {
            throw new IllegalStateException("Duplicate programme selection not allowed");
        }

        // ================= MISSING RANK CHECK (STREAMS) =================
        boolean hasInvalidRank = app.getProgrammeChoices()
                .stream()
                .anyMatch(pc -> pc.getPreferenceRank() == null);

        if (hasInvalidRank) {
            throw new IllegalStateException("All programme choices must have preference rank");
        }

        // ================= OPTIONAL: RANK DUPLICATE CHECK =================
        boolean hasDuplicateRanks = app.getProgrammeChoices()
                .stream()
                .map(pc -> pc.getPreferenceRank())
                .distinct()
                .count() != app.getProgrammeChoices().size();

        if (hasDuplicateRanks) {
            throw new IllegalStateException("Preference ranks must be unique (1, 2, 3)");
        }
    }

    private void validateReferees(Application app) {

        if (app.getReferees() == null ||
                app.getReferees().size() < 2) {

            throw new IllegalStateException("Minimum 2 referees required");
        }

        boolean missingEmail = app.getReferees()
                .stream()
                .anyMatch(r -> r.getEmail() == null || r.getEmail().isBlank());

        if (missingEmail) {
            throw new IllegalStateException("All referees must have email");
        }
    }

    private void validatePayment(Application app) {

        Payment payment = app.getPayment();

        if (payment == null) {
            throw new IllegalStateException("Payment not completed");
        }

        if (payment.getStatus() != PaymentStatus.PAID) {
            throw new IllegalStateException("Payment not confirmed");
        }
    }

    private void validateSubmissionReadiness(Application app) {

        validatePersonalInfo(app);
        validateEducation(app);
        validateWorkExperience(app);
        validateProgrammeChoice(app);
        validateReferees(app);
        validatePayment(app);
    }

    // private void validateSubmissionCore(Application app) {

    // validatePersonalInfo(app);
    // validateEducation(app);
    // validateProgrammeChoice(app);

    // // TEMP disabled modules
    // // validateReferees(app);
    // // validatePayment(app);
    // }

    public ApplicationProgressDto getCurrentStep(Long applicationId) {

        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        ApplicationProgressDto dto = new ApplicationProgressDto();

        dto.setApplicationId(app.getId());
        dto.setApplicantId(app.getApplicant().getId());
        dto.setCurrentStep(app.getCurrentStep());
        dto.setStatus(app.getStatus());
        dto.setLocked(app.isLocked());

        // OPTIONAL ENHANCEMENTS FOR UI
        int totalSteps = com.znz.tpip_backend.enums.ApplicationStep.values().length;
        int currentOrder = app.getCurrentStep().getOrder();

        int progress = (int) ((currentOrder * 100.0) / totalSteps);
        dto.setProgressPercentage(progress);

        dto.setCurrentStepLabel(
                app.getCurrentStep().name().replace("_", " "));

        return dto;
    }

    private void checkIfLocked(Application app) {
        if (app.isLocked()) {
            throw new IllegalStateException("Application is locked. No further modifications allowed.");
        }
    }

    private ApplicationDTO mapToDTO(Application app) {

        ApplicationDTO dto = new ApplicationDTO();

        dto.setId(app.getId());
        dto.setIndexNumber(app.getIndexNumber());

        dto.setCurrentStep(app.getCurrentStep().getOrder());
        dto.setStatus(app.getStatus());

        dto.setLocked(app.isLocked());
        dto.setLockedAt(app.getLockedAt());
        dto.setSubmittedAt(app.getSubmittedAt());

        // ================= RELATIONSHIPS =================

        dto.setRefereeIds(
                app.getReferees() != null
                        ? app.getReferees().stream().map(r -> r.getId()).toList()
                        : List.of());

        dto.setProgrammeChoiceIds(
                app.getProgrammeChoices() != null
                        ? app.getProgrammeChoices().stream().map(pc -> pc.getId()).toList()
                        : List.of());

        dto.setPaymentId(
                app.getPayment() != null ? app.getPayment().getId() : null);

        // ================= COMPUTED =================

        dto.setRefereeCount(
                app.getReferees() != null ? app.getReferees().size() : 0);

        dto.setPaymentStatus(
                app.getPayment() != null ? app.getPayment().getStatus() : null);

        return dto;
    }
}
// Next we will validate:
// 👉 "data inside each step"

// Example:

// Step 1 → must have PersonalInfo
// Step 2 → must have Education
// Step 3 → WorkExperience OR marked “no experience”
// Step 4 → must have 1–3 programme choices
// Step 5 → must have ≥ 2 referees
// Step 6 → must have payment

package com.znz.tpip_backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.znz.tpip_backend.dto.ApplicationDTO;
import com.znz.tpip_backend.dto.ApplicationFormDto;
import com.znz.tpip_backend.dto.ApplicationProgressDto;
import com.znz.tpip_backend.dto.PaymentDTO;
import com.znz.tpip_backend.dto.PersonalInfoDto;
// import com.znz.tpip_backend.dto.ProgrammeChoiceDto;
import com.znz.tpip_backend.dto.StepConfigDto;
import com.znz.tpip_backend.email.RefereeEmailService;
// import com.znz.tpip_backend.controller.ApplicationProgressDto;
import com.znz.tpip_backend.enums.ApplicationStatus;
import com.znz.tpip_backend.enums.ApplicationStep;
import com.znz.tpip_backend.enums.PaymentStatus;
import com.znz.tpip_backend.enums.RefereeStatus;
import com.znz.tpip_backend.model.*;
import com.znz.tpip_backend.repository.ApplicationRepository;
import com.znz.tpip_backend.repository.IntakeRepository;
// import com.znz.tpip_backend.repository.ProgrammeChoiceRepository;
import com.znz.tpip_backend.repository.ProgrammeRepository;
import com.znz.tpip_backend.service.configDrivenApplicationSteps.ApplicationWorkflowConfig;
import com.znz.tpip_backend.service.configDrivenApplicationSteps.StepConfig;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final IntakeRepository intakeRepository;
    // private final ApplicationStepEvaluator evaluator;
    private final ApplicationWorkflowConfig workflowConfig;
    private final ProgrammeRepository programmeRepository;

    private final RefereeEmailService refereeEmailService;
    private final TokenService tokenService;

    private final PaymentReferenceService referenceService;

    public List<ApplicationDTO> getAllApplications() {
        return applicationRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    // ================= GET APPLICATION BY ID =================
    public ApplicationDTO getApplicationById(Long id) {

        Application app = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));

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
                    newApp.setApplicationIndexNumber(generateApplicationIndex(activeIntake));

                    return applicationRepository.save(newApp);
                });

        return mapToDTO(app);
    }

    // ================= MOVE STEP =================
    // public ApplicationDTO moveToStep(Long applicationId, ApplicationStep
    // nextStep) {

    // Application app = getApplication(applicationId);

    // checkIfLocked(app);

    // validateStepCompletion(app, app.getCurrentStep());

    // if (!isValidStepTransition(app.getCurrentStep(), nextStep)) {
    // throw new IllegalStateException("Invalid step transition");
    // }

    // app.setCurrentStep(nextStep);

    // return mapToDTO(applicationRepository.save(app));
    // }
    public ApplicationDTO moveToStep(
            Long applicationId,
            ApplicationStep nextStep) {

        Application app = getApplication(applicationId);

        checkIfLocked(app);

        // VALIDATE ONLY WHEN MOVING FORWARD
        if (nextStep.getOrder() > app.getCurrentStep().getOrder()) {

            validateStepCompletion(
                    app,
                    app.getCurrentStep());
        }

        if (!isValidStepTransition(
                app.getCurrentStep(),
                nextStep)) {

            throw new IllegalStateException(
                    "Invalid step transition");
        }

        app.setCurrentStep(nextStep);

        return mapToDTO(
                applicationRepository.save(app));
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
                // if (!app.isLocked()) {
                // throw new IllegalStateException("Application must be locked after
                // submission");
                // }
            }
        }
    }

    private void validatePersonalInfo(Application app) {

        if (app.getApplicant() == null) {
            throw new IllegalStateException("Applicant missing");
        }

        PersonalInfo info = app.getApplicant().getPersonalInfo();

        if (info == null) {
            throw new IllegalStateException("Personal information not completed");
        }

        if (info.getFirstName() == null || info.getFirstName().isBlank()) {
            throw new IllegalStateException("First name is required");
        }

        if (info.getLastName() == null || info.getLastName().isBlank()) {
            throw new IllegalStateException("Last name is required");
        }

        if (info.getEmail() == null || info.getEmail().isBlank()) {
            throw new IllegalStateException("Email is required");
        }

        if (info.getDateOfBirth() == null) {
            throw new IllegalStateException("Date of birth is required");
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

    public ApplicationProgressDto resumeProgress(Long applicantId) {

        Intake activeIntake = intakeRepository.findByActiveTrue()
                .orElseThrow(() -> new RuntimeException("No active intake found"));

        Application app = applicationRepository
                .findByApplicantIdAndIntakeId(applicantId, activeIntake.getId())
                .orElseThrow(() -> new RuntimeException("No application found"));

        ApplicationProgressDto dto = new ApplicationProgressDto();

        dto.setApplicationId(app.getId());
        dto.setApplicantId(app.getApplicant().getId());
        dto.setCurrentStep(app.getCurrentStep());
        dto.setStatus(app.getStatus());

        // 👉 NEXT STEP LOGIC
        dto.setNextStep(determineNextStep(app));

        // progress
        int totalSteps = ApplicationStep.values().length;
        dto.setProgressPercentage(
                (app.getCurrentStep().getOrder() * 100) / totalSteps);

        return dto;
    }

    private ApplicationStep determineNextStep(Application app) {

        StepConfig config = workflowConfig.getConfig(app.getCurrentStep());

        if (config == null) {
            return ApplicationStep.SUBMISSION;
        }

        if (!config.getCondition().test(app)) {
            return app.getCurrentStep();
        }

        return config.getNextStep();
    }

    public ApplicationProgressDto getApplicationProgress(Long applicationId) {

        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        ApplicationProgressDto dto = new ApplicationProgressDto();

        dto.setApplicationId(app.getId());
        dto.setApplicantId(app.getApplicant().getId());
        dto.setCurrentStep(app.getCurrentStep());
        dto.setStatus(app.getStatus());
        dto.setLocked(app.isLocked());

        int totalSteps = ApplicationStep.values().length;
        int currentOrder = app.getCurrentStep().getOrder();

        // int progress = (int) ((currentcode Order * 100.0) / totalSteps);
        int progress = Math.round(((float) (currentOrder + 1) / totalSteps) * 100);

        dto.setProgressPercentage(progress);
        dto.setCurrentStepLabel(app.getCurrentStep().name().replace("_", " "));

        // optional UX improvement
        dto.setNextStep(determineNextStep(app));

        return dto;
    }

    public List<StepConfigDto> getWorkflowSteps() {

        return workflowConfig.getAllSteps()
                .stream()
                .map(config -> {
                    StepConfigDto dto = new StepConfigDto();

                    dto.setStep(config.getStep());
                    dto.setNextStep(config.getNextStep());
                    dto.setPrevStep(config.getPrevStep());
                    dto.setOrder(config.getOrder());

                    return dto;
                })
                .toList();
    }

    public ApplicationDTO saveStepData(Long appId, ApplicationFormDto dto) {

        Application app = getApplication(appId);

        checkIfLocked(app);

        switch (app.getCurrentStep()) {

            case PERSONAL_INFO -> updatePersonalInfo(app, dto);

            case EDUCATION -> updateEducation(app, dto);

            case WORK_EXPERIENCE -> updateWork(app, dto);

            // case PROGRAMME_CHOICE -> updateProgramme(app, dto);
            case PROGRAMME_CHOICE -> updateProgrammeChoice(app, dto);

            case REFEREES -> updateReferees(app, dto);

            case PAYMENT -> updatePayment(app, dto);

            case SUBMISSION -> {
                // no save needed
            }

            default -> throw new IllegalStateException(
                    "Invalid step: " + app.getCurrentStep());
        }

        // AUTO MOVE TO NEXT STEP
        ApplicationStep nextStep = determineNextStep(app);

        app.setCurrentStep(nextStep);

        return mapToDTO(applicationRepository.save(app));
    }

    public ApplicationDTO moveNext(Long appId) {

        Application app = getApplication(appId);

        StepConfig config = workflowConfig.getConfig(app.getCurrentStep());

        if (!config.getCondition().test(app)) {
            throw new IllegalStateException("Step not completed yet");
        }

        ApplicationStep next = config.getNextStep();

        if (next == null) {
            throw new IllegalStateException("No next step found");
        }

        app.setCurrentStep(next);

        return mapToDTO(applicationRepository.save(app));
    }

    // ================= PERSONAL INFO =================
    private void updatePersonalInfo(Application app, ApplicationFormDto dto) {

        if (dto.getPersonalInfo() == null) {
            throw new IllegalStateException("Personal info is required");
        }

        Applicant applicant = app.getApplicant();

        // ✅ FIX: correct type
        PersonalInfoDto dtoInfo = dto.getPersonalInfo();

        PersonalInfo info = applicant.getPersonalInfo();

        if (info == null) {
            info = new PersonalInfo();
            info.setApplicant(applicant);
            applicant.setPersonalInfo(info);
        }

        // ================= REQUIRED FIELDS =================
        if (dtoInfo.getFirstName() == null || dtoInfo.getLastName() == null) {
            throw new IllegalStateException("First name and last name are required");
        }

        if (dtoInfo.getDateOfBirth() == null) {
            throw new IllegalStateException("Date of birth is required");
        }

        if (dtoInfo.getEmail() == null) {
            throw new IllegalStateException("Email is required");
        }

        // ================= MAP DATA =================
        info.setFirstName(dtoInfo.getFirstName());
        info.setMiddleName(dtoInfo.getMiddleName());
        info.setLastName(dtoInfo.getLastName());
        info.setDateOfBirth(dtoInfo.getDateOfBirth());

        info.setGender(dtoInfo.getGender());
        info.setNationality(dtoInfo.getNationality());

        info.setPhoneNumber(dtoInfo.getPhoneNumber());
        info.setAlternativePhone(dtoInfo.getAlternativePhone());

        info.setEmail(dtoInfo.getEmail());

        info.setRegion(dtoInfo.getRegion());
        info.setDistrict(dtoInfo.getDistrict());

        // ================= NEXT OF KIN =================
        NextOfKin nok = new NextOfKin();
        nok.setKinFullName(dtoInfo.getNextOfKinName());
        nok.setKinRelationship(dtoInfo.getNextOfKinRelationship());
        nok.setKinPhoneNumber(dtoInfo.getNextOfKinPhone());
        info.setNextOfKin(nok);

        // ================= DISABILITY =================
        Disability disability = new Disability();
        disability.setHasDisability(dtoInfo.getHasDisability());
        disability.setDisabilityType(dtoInfo.getDisabilityType());
        disability.setDisabilityNeeds(dtoInfo.getDisabilityNeeds());
        info.setDisability(disability);
    }

    private void updateEducation(Application app, ApplicationFormDto dto) {

        if (dto.getEducations() == null || dto.getEducations().isEmpty()) {
            throw new IllegalStateException("Education data required");
        }

        Applicant applicant = app.getApplicant();

        // IMPORTANT: clear properly (avoid orphan issues)
        applicant.getEducations().clear();

        dto.getEducations().forEach(e -> {

            Education edu = new Education();

            edu.setApplicant(applicant);

            edu.setLevel(e.getLevel());
            edu.setInstitutionName(e.getInstitutionName());

            edu.setProgrammeName(e.getProgrammeName()); // ✅ missing before

            edu.setCompletionYear(e.getCompletionYear()); // ❗ FIXED (was yearFrom/yearTo)

            edu.setGpa(e.getGpa());
            edu.setClassification(e.getClassification());

            edu.setIsVerified(false); // default for new entries

            applicant.getEducations().add(edu);
        });
    }

    private void updateWork(Application app, ApplicationFormDto dto) {

        Applicant applicant = app.getApplicant();

        applicant.setHasWorkExperience(dto.getHasWorkExperience());

        // IMPORTANT: clear properly (avoid NPE safety)
        if (applicant.getWorkExperiences() == null) {
            applicant.setWorkExperiences(new ArrayList<>());
        } else {
            applicant.getWorkExperiences().clear();
        }

        // only if user has experience
        if (Boolean.TRUE.equals(dto.getHasWorkExperience())
                && dto.getWorkExperiences() != null) {

            dto.getWorkExperiences().forEach(w -> {

                WorkExperience work = new WorkExperience();

                // ================= EMPLOYER DETAILS =================
                work.setEmployerName(w.getEmployerName());
                work.setEmployerAddress(w.getEmployerAddress());
                work.setEmployerPhone(w.getEmployerPhone());
                work.setEmployerEmail(w.getEmployerEmail());

                // ================= JOB DETAILS =================
                work.setJobTitle(w.getJobTitle());
                work.setDepartment(w.getDepartment());
                work.setResponsibilities(w.getResponsibilities());

                // ================= EMPLOYMENT PERIOD =================
                work.setStartDate(w.getStartDate());
                work.setEndDate(w.getEndDate());
                work.setIsCurrentlyEmployed(w.getIsCurrentlyEmployed());

                // ================= EMPLOYMENT TYPE =================
                work.setEmploymentType(w.getEmploymentType());

                // ================= LOCATION =================
                work.setCountry(w.getCountry());
                work.setRegion(w.getRegion());
                work.setDistrict(w.getDistrict());
                work.setCity(w.getCity());

                // ================= RELATIONSHIP (IMPORTANT) =================
                work.setApplicant(applicant);

                applicant.getWorkExperiences().add(work);
            });
        }
    }

    // ================= PROGRAMME CHOICE =================
    private void updateProgrammeChoice(Application app, ApplicationFormDto dto) {

        // ================= VALIDATION =================
        if (dto.getProgrammeChoices() == null ||
                dto.getProgrammeChoices().isEmpty()) {

            throw new IllegalStateException("Programme choices required");
        }

        // ================= INIT/CLEAR =================
        if (app.getProgrammeChoices() == null) {

            app.setProgrammeChoices(new ArrayList<>());

        } else {

            app.getProgrammeChoices().clear();
        }

        // ================= MAP + SAVE =================
        List<ProgrammeChoice> choices = dto.getProgrammeChoices()
                .stream()

                // skip empty rows from frontend
                .filter(choiceDto -> choiceDto.getProgrammeId() != null)

                .map(choiceDto -> {

                    Programme programme = programmeRepository
                            .findById(choiceDto.getProgrammeId())
                            .orElseThrow(() -> new RuntimeException(
                                    "Programme not found with id: "
                                            + choiceDto.getProgrammeId()));

                    ProgrammeChoice choice = new ProgrammeChoice();

                    // ================= RELATIONSHIPS =================
                    choice.setApplication(app);
                    choice.setProgramme(programme);

                    // ================= BASIC DATA =================
                    choice.setPreferenceRank(
                            choiceDto.getPreferenceRank());

                    // ================= ELIGIBILITY =================
                    applyEligibility(
                            choice,
                            app.getApplicant(),
                            programme);

                    return choice;
                })
                .toList();

        // ================= FINAL VALIDATION =================
        if (choices.isEmpty()) {

            throw new IllegalStateException(
                    "At least one valid programme choice required");
        }

        app.getProgrammeChoices().addAll(choices);
    }

    private void applyEligibility(
            ProgrammeChoice choice,
            Applicant applicant,
            Programme programme) {

        List<Education> educations = applicant.getEducations();

        if (educations == null || educations.isEmpty()) {

            choice.setIsEligible(false);
            choice.setMatchScore(0);
            choice.setEligibilityRemark("No education found");

            return;
        }

        Education highest = getHighestEducation(educations);

        boolean eligible = highest.getLevel().ordinal() >= programme.getRequiredLevel().ordinal();

        choice.setIsEligible(eligible);

        choice.setMatchScore(
                eligible ? 80 : 40);

        choice.setEligibilityRemark(
                eligible
                        ? "Eligible"
                        : "Not eligible");
    }

    private Education getHighestEducation(
            List<Education> educations) {

        return educations.stream()
                .max((e1, e2) -> Integer.compare(
                        e1.getLevel().ordinal(),
                        e2.getLevel().ordinal()))
                .orElseThrow(() -> new RuntimeException("No education found"));
    }

    private void updateReferees(Application app, ApplicationFormDto dto) {

        if (dto.getReferees() == null || dto.getReferees().size() < 2) {
            throw new IllegalStateException("Minimum 2 referees required");
        }

        // clear existing
        app.getReferees().clear();

        dto.getReferees().forEach(r -> {

            Referee ref = new Referee();

            ref.setApplication(app);

            ref.setFullName(r.getFullName());
            ref.setTitle(r.getTitle());
            ref.setOrganization(r.getOrganization());
            ref.setEmail(r.getEmail());
            ref.setPhone(r.getPhone());
            ref.setRelationship(r.getRelationship());

            // ================= FIX: ADD TOKEN LOGIC =================
            String token = tokenService.generateToken();

            ref.setStatus(RefereeStatus.PENDING);
            ref.setToken(token);
            ref.setTokenExpiry(tokenService.expiryTime());

            app.getReferees().add(ref);

            // optional: send email immediately
            refereeEmailService.sendInvitation(ref);
        });
    }
    // private void updateReferees(Application app, ApplicationFormDto dto) {

    // if (dto.getReferees() == null || dto.getReferees().size() < 2) {
    // throw new IllegalStateException("Minimum 2 referees required");
    // }

    // // clear existing referees
    // app.getReferees().clear();

    // dto.getReferees().forEach(r -> {

    // Referee ref = new Referee();

    // ref.setApplication(app);

    // ref.setFullName(r.getFullName());
    // ref.setTitle(r.getTitle());
    // ref.setOrganization(r.getOrganization());
    // ref.setEmail(r.getEmail());
    // ref.setPhone(r.getPhone());
    // ref.setRelationship(r.getRelationship());

    // // default status (optional but good practice)
    // ref.setStatus(RefereeStatus.PENDING);

    // app.getReferees().add(ref);
    // });
    // }

    private void updatePayment(Application app, ApplicationFormDto dto) {

        if (dto.getPayment() == null) {
            throw new IllegalStateException("Payment data required");
        }

        PaymentDTO paymentDTO = dto.getPayment();

        Payment payment = app.getPayment();

        // ================= CREATE PAYMENT =================
        if (payment == null) {

            payment = new Payment();

            payment.setApplication(app);

            // AUTO GENERATED
            payment.setReferenceNumber(
                    referenceService.generateReference(app.getId()));

            payment.setInitiatedAt(LocalDateTime.now());

            // DEFAULT STATUS
            payment.setStatus(PaymentStatus.PENDING);

            app.setPayment(payment);
        }

        // ================= UPDATE PAYMENT =================
        payment.setUpdatedAt(LocalDateTime.now());

        payment.setAmount(paymentDTO.getAmount());
        payment.setCurrency(
                paymentDTO.getCurrency() != null
                        ? paymentDTO.getCurrency()
                        : "TZS");

        payment.setMethod(paymentDTO.getMethod());
        payment.setChannel(paymentDTO.getChannel());

        payment.setPayerPhone(paymentDTO.getPayerPhone());
        payment.setPayerName(paymentDTO.getPayerName());

        // NEVER overwrite reference number
    }

    // ================= MAP ENTITY TO DTO =================
    private ApplicationDTO mapToDTO(Application app) {

        ApplicationDTO dto = new ApplicationDTO();

        dto.setId(app.getId());
        dto.setApplicationIndexNumber(app.getApplicationIndexNumber());

        dto.setCurrentStep(app.getCurrentStep());
        dto.setStatus(app.getStatus());

        dto.setLocked(app.isLocked());
        dto.setSubmittedAt(app.getSubmittedAt());
        dto.setLockedAt(app.getLockedAt());

        // ================= REFEREES =================
        if (app.getReferees() != null) {

            dto.setRefereeIds(
                    app.getReferees()
                            .stream()
                            .map(Referee::getId)
                            .toList());

            dto.setRefereeCount(app.getReferees().size());

        } else {

            dto.setRefereeIds(List.of());
            dto.setRefereeCount(0);
        }

        // ================= PAYMENT =================
        if (app.getPayment() != null) {

            dto.setPaymentId(app.getPayment().getId());
            dto.setPaymentStatus(app.getPayment().getStatus());

        }

        // ================= PROGRAMME CHOICES =================
        if (app.getProgrammeChoices() != null) {

            dto.setProgrammeChoiceIds(
                    app.getProgrammeChoices()
                            .stream()
                            .map(ProgrammeChoice::getId)
                            .toList());

        } else {

            dto.setProgrammeChoiceIds(List.of());
        }

        return dto;
    }
}
// Next we will validate:
// "data inside each step"

// Example:

// Step 1 → must have PersonalInfo
// Step 2 → must have Education
// Step 3 → WorkExperience OR marked “no experience”
// Step 4 → must have 1–3 programme choices
// Step 5 → must have ≥ 2 referees
// Step 6 → must have payment

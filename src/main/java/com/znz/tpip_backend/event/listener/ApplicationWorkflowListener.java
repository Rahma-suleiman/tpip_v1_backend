// package com.znz.tpip_backend.event.listener;

// import com.znz.tpip_backend.event.ApplicationStepUpdatedEvent;
// import com.znz.tpip_backend.enums.ApplicationStep;
// import com.znz.tpip_backend.model.Application;
// import com.znz.tpip_backend.repository.ApplicationRepository;
// import com.znz.tpip_backend.service.ApplicationStepEvaluator;

// import lombok.RequiredArgsConstructor;
// import org.springframework.context.event.EventListener;
// import org.springframework.stereotype.Component;

// @Component
// @RequiredArgsConstructor
// public class ApplicationWorkflowListener {

//     private final ApplicationRepository applicationRepository;
//     private final ApplicationStepEvaluator evaluator;

//     @EventListener
//     public void handle(ApplicationStepUpdatedEvent event) {

//         Application app = applicationRepository.findById(event.getApplicationId())
//                 .orElseThrow(() -> new RuntimeException("Application not found"));

//         if (app.isLocked())
//             return;

//         evaluateAndAdvance(app);

//         applicationRepository.save(app);
//     }

//     private void evaluateAndAdvance(Application app) {
//         advanceOneStep(app); // ONLY ONE STEP PER EVENT
//     }

//     private boolean advanceOneStep(Application app) {

//         switch (app.getCurrentStep()) {

//             case PERSONAL_INFO -> {
//                 if (evaluator.isPersonalInfoComplete(app)) {
//                     app.setCurrentStep(ApplicationStep.EDUCATION);
//                     return true;
//                 }
//             }
//             case EDUCATION -> {
//                 if (evaluator.isEducationComplete(app)) {
//                     app.setCurrentStep(ApplicationStep.WORK_EXPERIENCE);
//                     return true;
//                 }
//             }
//             case WORK_EXPERIENCE -> {
//                 if (evaluator.isWorkExperienceComplete(app)) {
//                     app.setCurrentStep(ApplicationStep.PROGRAMME_CHOICE);
//                     return true;
//                 }
//             }
//             case PROGRAMME_CHOICE -> {
//                 if (evaluator.isProgrammeChoiceComplete(app)) {
//                     app.setCurrentStep(ApplicationStep.REFEREES);
//                     return true;
//                 }
//             }

//             case REFEREES -> {
//                 if (evaluator.isRefereesComplete(app)) {
//                     app.setCurrentStep(ApplicationStep.PAYMENT);
//                     return true;
//                 }
//             }

//             case PAYMENT -> {
//                 if (evaluator.isPaymentComplete(app)) {
//                     app.setCurrentStep(ApplicationStep.SUBMISSION);
//                     return true;
//                 }
//             }
//             default -> {
//                 return false;
//             }
//         }

//         return false;
//     }
   
// }

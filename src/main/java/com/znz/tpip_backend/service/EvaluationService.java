// package com.znz.tpip_backend.service;

// import java.time.LocalDate;
// import java.util.List;

// import org.modelmapper.ModelMapper;
// import org.springframework.stereotype.Service;

// import com.znz.tpip_backend.dto.EvaluationDto;
// import com.znz.tpip_backend.enums.EvaluationStatus;
// import com.znz.tpip_backend.enums.EvaluationType;
// import com.znz.tpip_backend.enums.PlacementStatus;
// import com.znz.tpip_backend.model.Evaluation;
// import com.znz.tpip_backend.model.Feedback;
// import com.znz.tpip_backend.model.Intern;
// import com.znz.tpip_backend.model.Mentor;
// import com.znz.tpip_backend.model.Placement;
// import com.znz.tpip_backend.repository.EvaluationRepository;
// import com.znz.tpip_backend.repository.FeedbackRepository;
// import com.znz.tpip_backend.repository.InternRepository;
// import com.znz.tpip_backend.repository.MentorRepository;
// import com.znz.tpip_backend.repository.PlacementRepository;

// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class EvaluationService {

//     // @Autowired
//     private final EvaluationRepository evaluationRepository;
//     private final PlacementRepository placementRepository;
//     // private final MentorRepository mentorRepository;
//     // private final InternRepository internRepository;
//     private final FeedbackRepository feedbackRepository;
//     private final ExtensionService extensionService;
//     private final ModelMapper modelMapper;

//     public List<EvaluationDto> getAllEvaluations() {
//         List<Evaluation> evaluations = evaluationRepository.findAll();
//         return evaluations.stream()
//                 .map(this::mapToDto)
//                 .toList();
//     }

//     public EvaluationDto getEvaluationById(Long id) {
//         Evaluation evaluation = evaluationRepository.findById(id)
//                 .orElseThrow(() -> new IllegalStateException("Evaluation not found"));

//         return mapToDto(evaluation);
//     }

//     public List<EvaluationDto> getByPlacement(Long placementId) {
//         return evaluationRepository.findByPlacementId(placementId)
//                 .stream()
//                 // .map(this::mapToDto) OR
//                 .map(e -> mapToDto(e))
//                 .toList();
//     }

//     public EvaluationDto createEvaluation(EvaluationDto dto, Long loggedInMentorId) {
//         // validate ids/inputs
//         Placement placement = placementRepository.findById(dto.getPlacementId())
//                 .orElseThrow(() -> new IllegalStateException("Placement not found"));
//         // 2. Get mentor + intern from placement (NOT from DTO lookup)
//         Mentor mentor = placement.getMentor();
//         Intern intern = placement.getIntern();

//         // validate r/ships
//         // if (!placement.getMentor().getId().equals(mentor.getId())
//         // || !placement.getIntern().getId().equals(intern.getId())) {
//         // throw new IllegalStateException("Invalid placement relationship");
//         // }
//         // ONLY assigned mentor can evaluate
//         if (!mentor.getId().equals(loggedInMentorId)) {
//             throw new IllegalStateException("You are not allowed to evaluate this intern");

//         }
//         // ONLY ACTIVE placement can b evaluated
//         if (placement.getStatus() != PlacementStatus.ACTIVE) {
//             throw new IllegalStateException("Evaluation allowed only during ACTIVE placement");

//         }
//         // prevent duplicate evaluation type per placement
//         boolean exists = evaluationRepository.existsByPlacementIdAndEvaluationType(placement.getId(),
//                 dto.getEvaluationType());

//         if (exists) {
//             throw new IllegalStateException("Evaluation already exists for this type");
//         }
//         // validate score
//         if (dto.getScore() < 0 || dto.getScore() > 100) {
//             throw new IllegalStateException("Score must be between 0 and 100");
//         }
//         // ✔ REASSESSMENT must have extension
//         if (dto.getEvaluationType() == EvaluationType.REASSESSMENT
//                 && placement.getExtensions().isEmpty()) {
//             throw new IllegalStateException("Reassessment requires an existing extension");
//         }

//         // calculate summary BY Fetches ALL feedback records for that placement
//         List<Feedback> feedbacks = feedbackRepository.findByPlacementId(placement.getId());

//         double avgRating = feedbacks.stream() // Converts list into a stream (Starts going thro the list 1 by 1, so we
//                                               // can process each feedback 1 by 1)
//                 .mapToInt(Feedback::getRating) // Convert each item into an integer (number) -> For each feedback, take
//                                                // its rating) OR Go through each feedback→ take its rating→ turn it into
//                                                // a number/integer list
//                 .average() // Calculates average:(4 + 3 + 5) / 3 = 4.0
//                 .orElse(0); // Handles case when there are NO feedbacks: Example:[] → no ratings → Then:
//                             // average = 0 (prevents error,keeps system safe)
//         int totalMentoringSessions = feedbacks.size(); // Counts number of feedback entries From:[4, 3, 5]👉
//                                                        // Result:totalSessions = 3;

//         // create new evaluation
//         Evaluation evaluation = new Evaluation();

//         evaluation.setPlacement(placement);

//         evaluation.setScore(dto.getScore());
//         evaluation.setRemarks(dto.getRemarks());
//         evaluation.setEvaluationType(dto.getEvaluationType());
//         evaluation.setEvaluationDate(LocalDate.now());

//         evaluation.setAverageRating(avgRating);
//         evaluation.setTotalSessions(totalMentoringSessions);

//         // set status based on FINAL
//         if (dto.getEvaluationType() == EvaluationType.FINAL) {
//             // FINAL evaluation determines pass/fail/extension
//             if (dto.getScore() >= 50) {
//                 evaluation.setStatus(EvaluationStatus.PASSED);
//             } else {
//                 evaluation.setStatus(EvaluationStatus.REQUIRES_EXTENSION);

//             }
//         } else if (dto.getEvaluationType() == EvaluationType.REASSESSMENT) {
//             // REASSESSMENT only after extension → REASSESSMENT determines pass/fail (no
//             // extension)
//             if (dto.getScore() >= 50) {
//                 evaluation.setStatus(EvaluationStatus.PASSED);
//             } else {
//                 evaluation.setStatus(EvaluationStatus.FAILED);

//             }

//         } else {
//             // MIDTERM evaluation is always PENDING (created but not finalized)
//             evaluation.setStatus(EvaluationStatus.PENDING);

//         }

//         Evaluation savedEvaluation = evaluationRepository.save(evaluation);

//         // handle post evaluation actions
//         handlePostEvaluation(savedEvaluation);

//         return mapToDto(savedEvaluation);
//     }

//     private void handlePostEvaluation(Evaluation savedEvaluation) {

//         // Automatically mark placement as COMPLETED or TERMINATED based on evaluation
//         if (savedEvaluation.getEvaluationType() == EvaluationType.FINAL
//                 || savedEvaluation.getEvaluationType() == EvaluationType.REASSESSMENT) {

//             Placement updatePlacement = savedEvaluation.getPlacement();

//             if (savedEvaluation.getStatus() == EvaluationStatus.PASSED) {
//                 updatePlacement.setStatus(PlacementStatus.COMPLETED);
//             } else if (savedEvaluation.getStatus() == EvaluationStatus.FAILED) {
//                 updatePlacement.setStatus(PlacementStatus.TERMINATED);
//             }
//             placementRepository.save(updatePlacement);
//         }

//         // ✔ Auto-create extension if FINAL evaluation score < 50 and no existing
//         // extension
//         if (savedEvaluation.getEvaluationType() == EvaluationType.FINAL && savedEvaluation.getScore() < 50) {
//             extensionService.createExtensionFromEvaluation(savedEvaluation.getId());
//         }
//     }

//     public EvaluationDto updateEvaluation(Long id, EvaluationDto dto, Long loggedInMentorId) {
//         Evaluation evaluation = evaluationRepository.findById(id)
//                 .orElseThrow(() -> new IllegalStateException("Evaluation not found"));

//         // ONLY assigned mentor can edit
//         if (!evaluation.getPlacement().getMentor().getId().equals(loggedInMentorId)) {
//             throw new IllegalStateException("You are not allowed to edit this evaluation");
//         }
//         // ONLY PENDING evaluation can be edited
//         if (evaluation.getStatus() != EvaluationStatus.PENDING) {
//             throw new IllegalStateException("Only PENDING evaluation can be edited");
//             // throw new IllegalStateException("Cannot edit finalized evaluation");
//         }
//         // validate score
//         if (dto.getScore() < 0 || dto.getScore() > 100) {
//             throw new IllegalStateException("Score must be between 0 and 100");
//         }

//         evaluation.setScore(dto.getScore());

//         if (dto.getRemarks() != null) {
//             evaluation.setRemarks(dto.getRemarks());
//         }
//         Evaluation updatedEvaluation = evaluationRepository.save(evaluation);
//         return mapToDto(updatedEvaluation);
//     }

//     public void deleteEvaluation(Long id) {
//         Evaluation evaluation = evaluationRepository.findById(id)
//                 .orElseThrow(() -> new IllegalStateException("Evaluation not found"));
//         evaluationRepository.delete(evaluation);
//     }

//     private EvaluationDto mapToDto(Evaluation e) {

//         EvaluationDto dto = modelMapper.map(e, EvaluationDto.class);

//         if (e.getPlacement() != null) {
//             dto.setPlacementId(e.getPlacement().getId());

//             if (e.getPlacement().getIntern() != null) {
//                 dto.setInternId(e.getPlacement().getIntern().getId());

//                 if (e.getPlacement().getIntern().getApplication() != null) {
//                     dto.setInternName(e.getPlacement().getIntern().getApplication().getFirstName() + " "
//                             + e.getPlacement().getIntern().getApplication().getLastName());
//                 }
//             }

//             if (e.getPlacement().getMentor() != null) {
//                 dto.setMentorId(e.getPlacement().getMentor().getId());

//                 dto.setMentorName(e.getPlacement().getMentor().getName());
//             }
//         }

//         if (e.getExtension() != null) {
//             dto.setExtensionId(e.getExtension().getId());
//         }

//         return dto;
//     }
// }
// // MIDTERM EVALUATION
// // Header : 9
// // {
// // "score": 78,
// // "evaluationDate": "2026-04-23",
// // "evaluationType": "MIDTERM",
// // "remarks": "Good progress in teaching practice, needs improvement in class
// // control.",
// // "placementId": 1
// // }
// // Header : 8
// // {
// // "score": 45,
// // "evaluationDate": "2026-04-23",
// // "evaluationType": "MIDTERM",
// // "remarks": "Struggles with lesson delivery and classroom management.",
// // "placementId": 2
// // }
// // FINAL EVALUATION
// // Header : 9
// // {
// // "score": 78,
// // "evaluationDate": "2026-08-30",
// // "evaluationType": "FINAL",
// // "remarks": "Excellent performance, good classroom control and lesson
// // delivery.",
// // "placementId": 1
// // }
// // Header : 8
// // {
// // "score": 45,
// // "evaluationDate": "2026-08-30",
// // "evaluationType": "FINAL",
// // "remarks": "Weak teaching delivery and classroom management issues.",
// // "placementId": 2
// // }

// // ✔ Rules implemented:
// // Mentor must match placement
// // Intern must match placement
// // Only ACTIVE placement can be evaluated
// // Only ONE evaluation per type per placement
// // FINAL determines pass/fail/extension
// // REASSESSMENT only after extension
// // Score must be 0–100
// // Auto-calculate average rating from feedback

// // Intern logs activities
// // ↓
// // Mentor reviews → gives feedback
// // ↓
// // System accumulates/collects logs
// // ↓
// // Evaluation created (MIDTERM / FINAL)
// // ↓
// // Evaluation stores:
// // - avg rating
// // - total sessions
// // - remarks
// // - score
// // ↓
// // Final decision (pass / extend)
// // ↓
// // IF score < 50 → status = REQUIRES_EXTENSION
// // ↓
// // System creates Extension automatically
// // ↓
// // Placement continues (extended period)
// // ↓
// // Intern continues logging
// // ↓
// // Mentor gives feedback
// // ↓
// // REASSESSMENT evaluation
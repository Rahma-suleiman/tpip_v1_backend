// package com.znz.tpip_backend.service;

// import java.time.LocalDate;
// import java.util.List;
// import java.util.stream.Collectors;

// import org.modelmapper.ModelMapper;
// import org.springframework.stereotype.Service;

// import com.znz.tpip_backend.dto.FeedbackDto;
// import com.znz.tpip_backend.enums.PlacementStatus;
// import com.znz.tpip_backend.model.Feedback;
// import com.znz.tpip_backend.model.Intern;
// import com.znz.tpip_backend.model.Mentor;
// import com.znz.tpip_backend.model.Placement;
// import com.znz.tpip_backend.repository.FeedbackRepository;
// import com.znz.tpip_backend.repository.InternRepository;
// import com.znz.tpip_backend.repository.MentorRepository;
// import com.znz.tpip_backend.repository.PlacementRepository;

// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class FeedbackService {

//     private final FeedbackRepository feedbackRepository;
//     private final ModelMapper modelMapper;
//     private final MentorRepository mentorRepository;
//     private final InternRepository internRepository;
//     private final PlacementRepository placementRepository;

//     public List<FeedbackDto> getAllFeedbacks() {
//         return feedbackRepository.findAll().stream()
//                 .map(this::mapToDto)
//                 .collect(Collectors.toList());
//     }

//     public FeedbackDto getFeedbackById(Long id) {
//         Feedback feedback = feedbackRepository.findById(id)
//                 .orElseThrow(() -> new IllegalStateException("Feedback not found with id: " + id));
//         return mapToDto(feedback);
//     }

//     public List<FeedbackDto> getLogsByIntern(Long internId) {
//         return feedbackRepository.findByInternIdOrderByDateDesc(internId)
//                 .stream()
//                 .map(this::mapToDto)
//                 .collect(Collectors.toList());
//     }

//     public List<FeedbackDto> getLogsByMentor(Long mentorId) {
//         return feedbackRepository.findByMentorIdOrderByDateDesc(mentorId)
//                 .stream()
//                 .map(feedback -> this.mapToDto(feedback))
//                 .collect(Collectors.toList());
//     }

//     public List<FeedbackDto> getLogsByPlacement(Long placementId) {
//         return feedbackRepository.findByPlacementIdOrderByDateDesc(placementId)
//                 .stream()
//                 .map(this::mapToDto)
//                 .collect(Collectors.toList());
//     }

//     // Mentor only can add feedback for intern, so we need to check if the mentor
//     // and intern exist
//     public FeedbackDto addFeedback(FeedbackDto feedbackDto, long loggedInMentorId) {

//         // Validate input
//         if (feedbackDto.getMentorId() == null || feedbackDto.getInternId() == null
//                 || feedbackDto.getPlacementId() == null) {
//             throw new IllegalArgumentException("Mentor ID, Intern ID, and Placement ID must be provided");
//         }

//         // Check if mentor exists
//         Mentor mentor = mentorRepository.findById(feedbackDto.getMentorId())
//                 .orElseThrow(() -> new IllegalStateException("Mentor not found with id: " + feedbackDto.getMentorId()));

//         // Check if intern exists
//         Intern intern = internRepository.findById(feedbackDto.getInternId())
//                 .orElseThrow(() -> new IllegalStateException("Intern not found with id: " + feedbackDto.getInternId()));

//         // Check if placement exists bcz feedback is related to placement
//         Placement placement = placementRepository.findById(feedbackDto.getPlacementId())
//                 .orElseThrow(() -> new IllegalStateException(
//                         "Placement not found with id: " + feedbackDto.getPlacementId()));

//         // RULE 1: Must match placement
//         if (!placement.getMentor().getId().equals(mentor.getId()) ||
//                 !placement.getIntern().getId().equals(intern.getId())) {
//             throw new IllegalStateException("Invalid placement relationship");
//         }

//         // RULE 2: Only owner mentor can add (WILL B HANDLED B JWT)
//         if (!mentor.getId().equals(loggedInMentorId)) {
//             throw new IllegalStateException("You can only add feedback as the assigned mentor");
//         }
//         // RULE 2: Only ACTIVE
//         if (placement.getStatus() != PlacementStatus.ACTIVE) {
//             throw new IllegalStateException("Only ACTIVE internships can receive feedback");
//         }
//         // OPTIONAL RULE: one log per day
//         LocalDate feedbackDate = feedbackDto.getDate() != null
//         ? feedbackDto.getDate()
//         : LocalDate.now();

//         boolean exists = feedbackRepository
//         .existsByMentorIdAndInternIdAndDate(
//                 mentor.getId(),
//                 intern.getId(),
//                 feedbackDate);

// if (exists) {
//     throw new IllegalStateException("You already submitted log for this date");
// }
//         // 2. CREATE LOG
//         Feedback feedback = new Feedback();
//         feedback.setMentor(mentor);
//         feedback.setIntern(intern);
//         feedback.setPlacement(placement);

//         if (feedbackDto.getRating() < 1 || feedbackDto.getRating() > 5) {
//             throw new IllegalArgumentException("Rating must be between 1 and 5");
//         }
//         feedback.setRating(feedbackDto.getRating());
//         feedback.setComment(feedbackDto.getComment());

//         feedback.setSessionTopic(feedbackDto.getSessionTopic());
//         feedback.setPerformanceLevel(feedbackDto.getPerformanceLevel());
//         feedback.setRecommendations(feedbackDto.getRecommendations());

//         feedback.setDate(feedbackDate);

//         Feedback saved = feedbackRepository.save(feedback);

//         return mapToDto(saved);
//     }

//     // mentor only
//     // 4. UPDATE (MENTOR ONLY)
//     public FeedbackDto editFeedback(Long id, FeedbackDto dto, long loggedInMentorId) {

//         Feedback feedback = feedbackRepository.findById(id)
//                 .orElseThrow(() -> new IllegalStateException("Feedback not found"));

//         // // RULE 1: Only owner mentor can edit (WILL B HANDLED B JWT)
//         if (!feedback.getMentor().getId().equals(loggedInMentorId)) {
//         throw new IllegalStateException("You are not allowed to edit this feedback");
//         }

//         // RULE 2: Only while internship is ACTIVE
//         Placement placement = feedback.getPlacement();
//         if (placement.getStatus() != PlacementStatus.ACTIVE) {
//             throw new IllegalStateException("Cannot edit feedback after internship ends");
//         }

//         // Allow edit within e.g. 3 days:
//         if (feedback.getDate().isBefore(LocalDate.now().minusDays(3))) {
//             throw new IllegalStateException("Edit period expired");
//         }
//         // allow update only basic fields
//         if (dto.getRating() > 0) {
//             feedback.setRating(dto.getRating());
//         }

//         if (dto.getComment() != null) {
//             feedback.setComment(dto.getComment().trim());
//         }
//         if (dto.getSessionTopic() != null) {
//             feedback.setSessionTopic(dto.getSessionTopic().trim());
//         }

//         if (dto.getPerformanceLevel() != null) {
//             feedback.setPerformanceLevel(dto.getPerformanceLevel());
//         }

//         if (dto.getRecommendations() != null) {
//             feedback.setRecommendations(dto.getRecommendations().trim());
//         }
//         Feedback updated = feedbackRepository.save(feedback);

//         return mapToDto(updated);
//     }

//     // public void deleteFeedback(Long id, Long loggedInMentorId) {
//     public void deleteFeedback(Long id) {

//         Feedback feedback = feedbackRepository.findById(id)
//                 .orElseThrow(() -> new IllegalStateException("Feedback not found"));

//         // 🔒 RULE 1: Only owner can delete(APPLIED IN JWT)
//         // if (!feedback.getMentor().getId().equals(loggedInMentorId)) {
//         // throw new IllegalStateException("You are not allowed to delete this
//         // feedback");
//         // }

//         // 🔒 RULE 2: Only before internship starts
//         if (feedback.getPlacement().getStatus() == PlacementStatus.ACTIVE) {
//             throw new IllegalStateException("Cannot delete feedback during active internship");
//         }

//         feedbackRepository.delete(feedback);
//     }
//     // RECOMMENDED Reason: Feedback = official record ,Used for evaluation,Must not
//     // be removed
//     // public void deleteFeedback(Long id) {
//     // throw new IllegalStateException("Deleting feedback is not allowed");
//     // }

//     private FeedbackDto mapToDto(Feedback feedback) {
//         FeedbackDto dto = modelMapper.map(feedback, FeedbackDto.class);

//         if (feedback.getMentor() != null) {
//             dto.setMentorId(feedback.getMentor().getId());
//             dto.setMentorName(feedback.getMentor().getName());
//         }
//         if (feedback.getIntern() != null) {
//             dto.setInternId(feedback.getIntern().getId());

//             if (feedback.getIntern().getApplication() != null) {
//                 dto.setInternName(
//                         feedback.getIntern().getApplication().getFirstName() + " " +
//                                 feedback.getIntern().getApplication().getLastName());
//             }
//         }

//         if (feedback.getPlacement() != null) {
//             dto.setPlacementId(feedback.getPlacement().getId());
//         }
//         return dto;
//     }
// }
// // Intern assigned → Placement ACTIVE
// // ↓
// // Mentor logs in
// // ↓
// // Mentor conducts session
// // ↓
// // Mentor records mentoring log/Create feedback log (multiple allowed)
// // ↓
// // System stores multiple logs over time
// // ↓
// // Admin reviews progress/logs
// // ↓
// // Final evaluation decision based on logs

// // {
// //   "rating": 4,
// //   "comment": "Good lesson delivery with clear explanation of fractions. Students were engaged during group activities.",
// //   "date": "2026-04-18",
// //   "sessionTopic": "Teaching Fractions using Visual Aids",
// //   "performanceLevel": "GOOD",
// //   "recommendations": "Work more on explaining decimal conversions using simpler examples.",
// //   "mentorId": 9,
// //   "internId": 1,
// //   "placementId": 1
// // }
// // {
// //   "rating": 3,
// //   "comment": "The lesson was well structured, but some students struggled with understanding decimals.",
// //   "date": "2026-04-20",
// //   "sessionTopic": "Fractions and Decimal Conversion Practice",
// //   "performanceLevel": "AVERAGE",
// //   "recommendations": "Use more step-by-step demonstrations and real-life examples for decimals.",
// //   "mentorId": 9,
// //   "internId": 1,
// //   "placementId": 1
// // }
// // {
// //   "rating": 4,
// //   "comment": "Interactive teaching style and good use of examples. Students gradually became engaged.",
// //   "date": "2026-04-19",
// //   "sessionTopic": "Introduction to Nouns and Verbs",
// //   "performanceLevel": "GOOD",
// //   "recommendations": "Start lessons with icebreakers to boost early participation.",
// //   "mentorId": 8,
// //   "internId": 2,
// //   "placementId": 2
// // }
// // {
// //   "rating": 3,
// //   "comment": "Lesson content was correct, but initial student engagement was low.",
// //   "date": "2026-04-19",
// //   "sessionTopic": "Practice Exercises on Parts of Speech",
// //   "performanceLevel": "AVERAGE",
// //   "recommendations": "Encourage participation earlier using questions or short activities.",
// //   "mentorId": 8,
// //   "internId": 2,
// //   "placementId": 2
// // }
// package com.znz.tpip_backend.controller;

// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import com.znz.tpip_backend.dto.FeedbackDto;
// import com.znz.tpip_backend.service.FeedbackService;

// @RestController
// @RequestMapping("/api/v1/tpip/feedback")
// public class FeedbackController {

//     @Autowired
//     private FeedbackService feedbackService;

//     @GetMapping
//     public ResponseEntity<List<FeedbackDto>> getAllFeedbacks() {
//         List<FeedbackDto> feedbacks = feedbackService.getAllFeedbacks();
//         return new ResponseEntity<>(feedbacks, HttpStatus.OK);
//     }

//     @GetMapping("/{id}")
//     public ResponseEntity<FeedbackDto> getFeedbackById(@PathVariable Long id) {
//         FeedbackDto feedback = feedbackService.getFeedbackById(id);
//         return ResponseEntity.ok(feedback);
//     }

//     // For now ABT EDIT AND ADD, we simulate loggedInMentorId using header (later JWT will replace it):
//       @PostMapping
//     public ResponseEntity<FeedbackDto> addFeedback(
//             @RequestBody FeedbackDto feedbackDto,
//             @RequestHeader("mentorId") Long mentorId // simulate JWT
//     ) {
//         return new ResponseEntity<>(
//                 feedbackService.addFeedback(feedbackDto, mentorId),
//                 HttpStatus.CREATED
//         );
//     }

//     // ✅ FIXED
//     @PutMapping("/{id}")
//     public ResponseEntity<FeedbackDto> editFeedback(
//             @PathVariable Long id,
//             @RequestBody FeedbackDto feedbackDto,
//             @RequestHeader("mentorId") Long mentorId
//     ) {
//         return ResponseEntity.ok(
//                 feedbackService.editFeedback(id, feedbackDto, mentorId)
//         );
//     }

//     @DeleteMapping("/{id}")
//     public ResponseEntity<Void> deleteFeedback(@PathVariable Long id) {
//         feedbackService.deleteFeedback(id);
//         return ResponseEntity.noContent().build();
//     }
// }

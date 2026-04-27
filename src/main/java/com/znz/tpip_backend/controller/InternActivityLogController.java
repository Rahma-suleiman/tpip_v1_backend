// package com.znz.tpip_backend.controller;

// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import com.znz.tpip_backend.dto.ActivityLogReviewDto;
// import com.znz.tpip_backend.dto.InternActivityLogDto;
// import com.znz.tpip_backend.enums.ActivityLogStatus;
// import com.znz.tpip_backend.service.InternActivityLogService;

// @RestController
// @RequestMapping("/api/v1/tpip/internActivityLog")
// public class InternActivityLogController {

//     @Autowired
//     private InternActivityLogService internActivityLogService;

//     @GetMapping
//     public ResponseEntity<List<InternActivityLogDto>> getAllActivityLogs() {
//         List<InternActivityLogDto> activityLog = internActivityLogService.getAllActivityLogs();
//         return new ResponseEntity<>(activityLog, HttpStatus.OK);
//     }

//     @GetMapping("/{id}")
//     public ResponseEntity<InternActivityLogDto> getActivityLogById(@PathVariable Long id) {
//         InternActivityLogDto activityLog = internActivityLogService.getActivityLogById(id);
//         return ResponseEntity.ok(activityLog);
//     }

//     @PostMapping
//     public ResponseEntity<InternActivityLogDto> addActivityLog(@RequestBody InternActivityLogDto internActivityLogDto) {
//         InternActivityLogDto activityLog = internActivityLogService.addActivityLog(internActivityLogDto);
//         return new ResponseEntity<>(activityLog, HttpStatus.CREATED);
//     }

//     @PutMapping("/{id}/submit")
//     public ResponseEntity<InternActivityLogDto> submitLog( @PathVariable Long id, @RequestParam Long internId) {
//         InternActivityLogDto activityLog = internActivityLogService.submitLog(id, internId);
//         return ResponseEntity.ok(activityLog);
//     }

//     @PostMapping("/{id}/review") // meaning: “Go to a specific activity log by its ID, then perform a review
//                                  // action on it.”
//     public ResponseEntity<InternActivityLogDto> reviewActivityLog(@PathVariable Long id,
//             @RequestBody ActivityLogReviewDto request) {
//         InternActivityLogDto activityLog = internActivityLogService.reviewActivityLog(id, request.getMentorComment(),
//                 request.getStatus());
//         return new ResponseEntity<>(activityLog, HttpStatus.OK);
//     }

//     @PutMapping("/{id}")
//     public ResponseEntity<InternActivityLogDto> editActivityLog(@PathVariable Long id,
//             @RequestBody InternActivityLogDto internActivityLogDto) {
//         InternActivityLogDto activityLog = internActivityLogService.editActivityLog(id, internActivityLogDto);
//         return new ResponseEntity<>(activityLog, HttpStatus.OK);
//     }

//     @DeleteMapping("/{id}")
//     public ResponseEntity<Void> deleteActivityLog(@PathVariable Long id) {
//         internActivityLogService.deleteActivityLog(id);
//         return ResponseEntity.noContent().build();
//     }
// }

// package com.znz.tpip_backend.service;

// import java.time.LocalDate;
// import java.util.List;

// import org.modelmapper.ModelMapper;
// import org.springframework.stereotype.Service;

// import com.znz.tpip_backend.dto.InternActivityLogDto;
// import com.znz.tpip_backend.enums.ActivityLogStatus;
// import com.znz.tpip_backend.enums.PlacementStatus;
// import com.znz.tpip_backend.model.Intern;
// import com.znz.tpip_backend.model.InternActivityLog;
// import com.znz.tpip_backend.model.Placement;
// import com.znz.tpip_backend.repository.InternActivityLogRepository;
// import com.znz.tpip_backend.repository.InternRepository;

// // import lombok.RequiredArgsConstructor;

// @Service
// // @RequiredArgsConstructor 
// public class InternActivityLogService {

//     private final InternActivityLogRepository internActivityLogRepository;
//     private final ModelMapper modelMapper;
//     private final InternRepository internRepository;
    
//     public InternActivityLogService(InternActivityLogRepository internActivityLogRepository, ModelMapper modelMapper,
//             InternRepository internRepository) {
//         this.internActivityLogRepository = internActivityLogRepository;
//         this.modelMapper = modelMapper;
//         this.internRepository = internRepository;
//     }

//     // GET ALL LOGS
//     public List<InternActivityLogDto> getAllActivityLogs() {
//         return internActivityLogRepository.findAll()
//                 .stream()
//                 .map(this::mapToDto)
//                 .toList();
//     }

//     // GET BY ID
//     public InternActivityLogDto getActivityLogById(Long id) {
//         InternActivityLog log = internActivityLogRepository.findById(id)
//                 .orElseThrow(() -> new RuntimeException("Activity log not found"));
//         return mapToDto(log);
//     }

//     // CREATE LOG → DRAFT
//     public InternActivityLogDto addActivityLog(InternActivityLogDto dto) {

//         Intern intern = internRepository.findById(dto.getInternId())
//                 .orElseThrow(() -> new RuntimeException("Intern not found"));

//         Placement placement = intern.getPlacement();

//         if (placement == null) {
//             throw new IllegalStateException("Intern is not assigned to any placement");
//         }

//         if (placement.getStatus() != PlacementStatus.ACTIVE) {
//             throw new IllegalStateException("Internship has not started yet");
//         }

//         if (dto.getHoursSpent() <= 0) {
//             throw new IllegalArgumentException("Hours spent must be greater than 0");
//         }

//         InternActivityLog log = new InternActivityLog();
//         log.setIntern(intern);
//         log.setDate(dto.getDate() != null ? dto.getDate() : LocalDate.now());

//         log.setSubject(dto.getSubject());
//         log.setTopicTaught(dto.getTopicTaught());
//         log.setClassLevel(dto.getClassLevel());
//         log.setActivitiesDone(dto.getActivitiesDone());
//         log.setChallenges(dto.getChallenges());
//         log.setHoursSpent(dto.getHoursSpent());

//         // SYSTEM CONTROLLED
//         log.setStatus(ActivityLogStatus.DRAFT);
//         log.setReviewDate(null);
//         log.setMentorComment(null);

//         return mapToDto(internActivityLogRepository.save(log));
//     }

//     // SUBMIT → SUBMITTED
//     public InternActivityLogDto submitLog(Long id, Long internId) {

//         InternActivityLog log = internActivityLogRepository.findById(id)
//                 .orElseThrow(() -> new RuntimeException("Activity log not found"));

//         if (!log.getIntern().getId().equals(internId)) {
//             throw new IllegalStateException("You can only submit your own logs");
//         }

//         if (log.getStatus() != ActivityLogStatus.DRAFT) {
//             throw new IllegalStateException("Only draft logs can be submitted");
//         }

//         log.setStatus(ActivityLogStatus.SUBMITTED);

//         return mapToDto(internActivityLogRepository.save(log));
//     }

//     // EDIT → ONLY DRAFT OR NEEDS_REVISION
//     public InternActivityLogDto editActivityLog(Long id, InternActivityLogDto dto) {

//         InternActivityLog log = internActivityLogRepository.findById(id)
//                 .orElseThrow(() -> new RuntimeException("Activity log not found"));

//         if (dto.getInternId() == null ||
//                 !log.getIntern().getId().equals(dto.getInternId())) {
//             throw new IllegalStateException("You can only edit your own logs");
//         }

//         if (log.getStatus() != ActivityLogStatus.DRAFT &&
//                 log.getStatus() != ActivityLogStatus.NEEDS_REVISION) {
//             throw new IllegalStateException("Only draft or revision logs can be edited");
//         }

//         if (dto.getDate() != null) log.setDate(dto.getDate());
//         if (dto.getSubject() != null) log.setSubject(dto.getSubject());
//         if (dto.getTopicTaught() != null) log.setTopicTaught(dto.getTopicTaught());
//         if (dto.getClassLevel() != null) log.setClassLevel(dto.getClassLevel());
//         if (dto.getActivitiesDone() != null) log.setActivitiesDone(dto.getActivitiesDone());
//         if (dto.getChallenges() != null) log.setChallenges(dto.getChallenges());
//         if (dto.getHoursSpent() > 0) log.setHoursSpent(dto.getHoursSpent());

//         return mapToDto(internActivityLogRepository.save(log));
//     }

//     // MENTOR REVIEW
//     public InternActivityLogDto reviewActivityLog(Long id,
//             String mentorComment, ActivityLogStatus status) {

//         if (status != ActivityLogStatus.APPROVED &&
//                 status != ActivityLogStatus.NEEDS_REVISION &&
//                 status != ActivityLogStatus.REJECTED) {
//             throw new IllegalStateException("Invalid review status");
//         }

//         InternActivityLog log = internActivityLogRepository.findById(id)
//                 .orElseThrow(() -> new RuntimeException("Activity log not found"));

//         Placement placement = log.getIntern().getPlacement();

//          // BASIC SAFETY (optional but good)
//     if (placement == null || placement.getMentor() == null) {
//         throw new IllegalStateException("No mentor assigned to this intern");
//     }

//     // ONLY SUBMITTED CAN BE REVIEWED
//     if (log.getStatus() != ActivityLogStatus.SUBMITTED) {
//         throw new IllegalStateException("Only submitted logs can be reviewed");
//     }
//         log.setMentorComment(mentorComment);
//         log.setReviewDate(LocalDate.now());

//         // ✅ FIXED FLOW RULE
//         if (status == ActivityLogStatus.NEEDS_REVISION) {
//             log.setStatus(ActivityLogStatus.DRAFT); // BACK TO EDITABLE STATE
//         } else {
//             log.setStatus(status); // APPROVED or REJECTED
//         }

//         return mapToDto(internActivityLogRepository.save(log));
//     }

//     // DELETE → ONLY DRAFT
//     public void deleteActivityLog(Long id) {

//         InternActivityLog log = internActivityLogRepository.findById(id)
//                 .orElseThrow(() -> new IllegalStateException("Activity log not found"));

//         if (log.getStatus() != ActivityLogStatus.DRAFT) {
//             throw new IllegalStateException("Only draft logs can be deleted");
//         }

//         internActivityLogRepository.delete(log);
//     }

//     // DTO MAPPING
//     private InternActivityLogDto mapToDto(InternActivityLog log) {
//         InternActivityLogDto dto = modelMapper.map(log, InternActivityLogDto.class);

//         if (log.getIntern() != null) {

//             dto.setInternId(log.getIntern().getId());

//             if (log.getIntern().getApplication() != null) {
//                 dto.setInternName(
//                         log.getIntern().getApplication().getFirstName() + " " +
//                                 log.getIntern().getApplication().getLastName());
//             }

//             if (log.getIntern().getPlacement() != null) {

//                 if (log.getIntern().getPlacement().getMentor() != null) {
//                     dto.setMentorId(log.getIntern().getPlacement().getMentor().getId());
//                     dto.setMentorName(log.getIntern().getPlacement().getMentor().getName());
//                 }

//                 if (log.getIntern().getPlacement().getSchool() != null) {
//                     dto.setSchoolId(log.getIntern().getPlacement().getSchool().getId());
//                     dto.setSchoolName(log.getIntern().getPlacement().getSchool().getName());
//                 }
//             }
//         }
//         return dto;
//     }
// }
// // Intern assigned to Placement
// // ↓
// // Placement contains Mentor + School
// // ↓
// // Intern submits daily activity log
// // ↓
// // System attaches log to Intern ONLY
// // ↓
// // Mentor reviews log via Placement
// // ↓
// // Mentor adds comment (optional via service layer)


// // {
// //   "date": "2026-04-18",
// //   "subject": "Mathematics",
// //   "topicTaught": "Fractions and Decimals",
// //   "classLevel": "Grade 5",
// //   "activitiesDone": "Introduced fractions using visual aids, group exercises, and board demonstrations.",
// //   "challenges": "Some students had difficulty understanding decimal conversion.",
// //   "hoursSpent": 3.5,
// //   "internId": 1
// // }
// // {
// //   "date": "2026-04-18",
// //   "subject": "English",
// //   "topicTaught": "Parts of Speech - Nouns and Verbs",
// //   "classLevel": "Grade 4",
// //   "activitiesDone": "Conducted interactive lesson with examples, class participation, and short exercises.",
// //   "challenges": "Students were shy to participate initially.",
// //   "hoursSpent": 2.5,
// //   "internId": 2
// // }
// // REVIEW PAYLOAD
// // id = 1
// // {
// //   "mentorComment": "Good lesson structure and clear explanation of fractions. Students showed good understanding.",
// //   "status": "APPROVED"
// // }
// // id = 2
// // {
// //   "mentorComment": "Lesson was good, but encourage more student participation and engagement from the beginning.",
// //   "status": "NEEDS_REVISION"
// // }

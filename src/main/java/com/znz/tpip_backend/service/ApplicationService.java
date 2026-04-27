// package com.znz.tpip_backend.service;

// import java.time.LocalDate;
// import java.util.List;
// import java.util.stream.Collectors;

// import org.modelmapper.ModelMapper;
// import org.springframework.stereotype.*;

// import com.znz.tpip_backend.dto.ApplicationDto;
// import com.znz.tpip_backend.enums.ApplicationStatus;
// import com.znz.tpip_backend.enums.InternStatus;
// import com.znz.tpip_backend.model.Application;
// import com.znz.tpip_backend.model.Intern;
// import com.znz.tpip_backend.model.User;
// import com.znz.tpip_backend.repository.ApplicationRepository;
// import com.znz.tpip_backend.repository.InternRepository;
// import com.znz.tpip_backend.repository.UserRepository;

// import jakarta.transaction.Transactional;
// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class ApplicationService {

//     private final ApplicationRepository applicationRepository;
//     private final UserRepository userRepository;
//     private final InternRepository internRepository;
//     private final ModelMapper modelMapper;
//     private final InternService internService;

//     public List<ApplicationDto> getAllApplications() {
//         List<Application> applications = applicationRepository.findAll();
//         return applications.stream()
//                 .map(app -> {
//                     ApplicationDto appDto = modelMapper.map(app, ApplicationDto.class);

//                     // FK
//                     if (app.getUser() != null) {
//                         appDto.setUserId(app.getUser().getId());
//                     }
//                     // reverse
//                     appDto.setInternId(app.getIntern() != null ? app.getIntern().getId() : null);
//                     return appDto;
//                 }).collect(Collectors.toList());
//     }

//     public ApplicationDto getApplicationById(Long id) {
//         Application app = applicationRepository.findById(id)
//                 .orElseThrow(() -> new IllegalStateException("Application not found with id" + id));

//         ApplicationDto appDto = modelMapper.map(app, ApplicationDto.class);
//         // FK
//         if (app.getUser() != null) {
//             appDto.setUserId(app.getUser().getId());
//         }
//         // reverse
//         appDto.setInternId(app.getIntern() != null ? app.getIntern().getId() : null);
//         return appDto;
//     }

//     public ApplicationDto submitApplication(ApplicationDto applicationDto) {

//         // Validate user
//         if (applicationDto.getUserId() == null) {
//             throw new IllegalStateException("User is required for every application");

//         }
//         User user = userRepository.findById(applicationDto.getUserId())
//                 .orElseThrow(() -> new IllegalStateException("user not found with id" + applicationDto.getUserId()));

//         // prevent duplicate pending applications for the same user and also if has
//         // alrdy applied byt status is rejected, re-application is allowed
//         boolean hasPendingApplication = applicationRepository.existsByUserIdAndStatus(user.getId(),
//                 ApplicationStatus.PENDING);

//         if (hasPendingApplication) {
//             throw new IllegalStateException("User already has pending application");
//         }

//         // Block application if already intern,re-application not allowed
//         boolean isAlreadyIntern = internRepository.existsByApplication_User_IdAndStatus(user.getId(),
//                 InternStatus.ACTIVE);
//         if (isAlreadyIntern) {
//             throw new IllegalStateException("User is already an active intern and cannot apply again");
//         }

//         Application app = modelMapper.map(applicationDto, Application.class);

//         app.setUser(user);

//         app.setStatus(ApplicationStatus.PENDING);

//         app.setApplicationDate(LocalDate.now());

//         // clear admin review fields-since not yet reviewed
//         app.setReviewDate(null);
//         app.setReviewedBy(null);

//         Application savedApp = applicationRepository.save(app);

//         ApplicationDto appResponse = modelMapper.map(savedApp, ApplicationDto.class);

//         appResponse.setUserId(savedApp.getUser() != null ? savedApp.getUser().getId() : null);

//         return appResponse;

//     }
//     // Why This Matches Your TPIP Flow
//     // TPIP Step Now Handled
//     // User submits application ✅ addApplication()
//     // Status starts as pending ✅ ApplicationStatus.PENDING
//     // Admin reviews later ✅ reviewedBy, reviewDate untouched
//     // Prevent multiple applications ✅ check added
//     // Track submission date ✅ applicationDate

//     public ApplicationDto editApplication(Long id, ApplicationDto applicationDto) {

//         Application existingApp = applicationRepository.findById(id)
//                 .orElseThrow(() -> new IllegalStateException("Application not found with id" + id));

//         if (existingApp.getStatus() != ApplicationStatus.PENDING) {
//             throw new IllegalStateException("Only pending application can be edited");
//         }

//         applicationDto.setId(existingApp.getId());
//         applicationDto.setStatus(existingApp.getStatus());

//         applicationDto.setReviewDate(existingApp.getReviewDate());
//         applicationDto.setReviewedBy(existingApp.getReviewedBy());

//         modelMapper.map(applicationDto, existingApp);

//         // update user if provided
//         if (applicationDto.getUserId() != null) {
//             User user = userRepository.findById(applicationDto.getUserId())
//                     .orElseThrow(() -> new IllegalStateException("Invalid user id" + applicationDto.getUserId()));
//             existingApp.setUser(user);
//         }
//         Application updatedApp = applicationRepository.save(existingApp);

//         ApplicationDto appResponse = modelMapper.map(updatedApp, ApplicationDto.class);

//         appResponse.setUserId(updatedApp.getUser() != null ? updatedApp.getUser().getId() : null);

//         return appResponse;
//     }

//     // Now It Matches TPIP Flow
//     // Rule Status
//     // User edits before review ✅ Allowed
//     // (You should NOT allow editing if:APPROVED n REJECTED), After admin review →
//     // Approved/Rejected → no normal editing
//     // User edits after approval ❌ Blocked
//     // User edits after rejection ❌ Blocked
//     // Admin fields protected ✅ Yes
//     // Status protected ✅ Yes

//     public void deleteApplication(Long id) {
//         Application app = applicationRepository.findById(id)
//                 .orElseThrow(() -> new IllegalStateException("Application not found with id " + id));

//         applicationRepository.delete(app);
//     }

//     // we used @Transactional BCZ : i did - APPROVE application → CREATE intern
//     // If intern creation fails → application already approved (data inconsistency)
//     @Transactional // ensures both operations succeed or fail together
//     public ApplicationDto reviewApplication(Long id, ApplicationStatus status, String reviewerName) {


//             // 1. Find application
//             Application app = applicationRepository.findById(id)
//                     .orElseThrow(() -> new IllegalStateException("Application not found with id " + id));

//             // 2. Prevent double review of already processed applications(app shld b
//             // reviewed once)
//             if (app.getStatus() != ApplicationStatus.PENDING) {
//                 throw new IllegalStateException("Application already reviewed");
//             }

//             // 3. Update admin review data
//             app.setStatus(status);
//             app.setReviewedBy(reviewerName);
//             app.setReviewDate(LocalDate.now());

//             applicationRepository.save(app);

//             Intern savedIntern = null;
//             // 4. IF APPROVED → create intern
//             if (status == ApplicationStatus.APPROVED) {

//                 if (app.getUser() == null) {
//                     throw new IllegalStateException("User missing in application");
//                 }

//                 if (app.getIntern() != null) {
//                     throw new IllegalStateException("Application already has an intern");
//                 }

//                 // move this part to internService in createInternFromApplication()

//                 // Intern intern = new Intern();
//                 // intern.setApplication(app);

//                 // intern.setStatus(InternStatus.ACTIVE);
//                 // intern.setStartDate(LocalDate.now());
//                 // intern.setEducationLevel(app.getEducationLevel());
//                 // intern.setSpecialization(app.getCourseStudied());
//                 // intern.setGraduationYear(app.getGraduationYear());

//                 // savedIntern = internRepository.save(intern);

//                 // BEST WAY: BCZ of Separation of responsibilities : reviewApplication()= for
//                 // admin decision(approave/reject) while createInternFromApplication()= for
//                 // intern creation logic
//                 savedIntern = internService.createInternFromApplication(app);

//                 // TODO: assign school + mentor (later step in flow)
                
//             }
//             applicationRepository.save(app);

//             // 5. IF REJECTED → allow re-application (handled by logic in addApplication)
//             // nothing extra needed here

//             // 6. Return DTO
//             ApplicationDto dto = modelMapper.map(app, ApplicationDto.class);
//             // dto.setUserId(app.getUser().getId() != null ? app.getUser().getId() : null);
//             dto.setUserId(app.getUser() != null ? app.getUser().getId() : null);
//             // dto.setUserId(app.getUser() != null ? app.getUser().getId() : null);
//             dto.setInternId(savedIntern != null ? savedIntern.getId() : null);

//             return dto;
     
//     }

// }

// // FINAL TPIP FLOW (CLEAN ARCHITECTURE)
// // 1. User submits application
// // ↓
// // 2. Admin reviews application
// // ↓
// // 3. If APPROVED:
// // ↓
// // 4. InternService.createInternFromApplication()
// // ↓
// // 5. Admin assigns school/mentor (PlacementService)
// // ↓
// // 6. Internship starts

// // SUBMIT APPLICATION
// // {
// // "firstName": "Rahma",
// // "lastName": "Suleiman",
// // "gender": "FEMALE",
// // "dateOfBirth": "2000-04-15",
// // "phoneNumber": "0712345678",
// // "email": "rahma@gmail.com",
// // "address": "Dar es Salaam",
// // "educationLevel": "DEGREE",
// // "courseStudied": "Computer Science",
// // "institutionName": "SUZA",
// // "graduationYear": 2024,
// // "qualificationFile": "file.pdf",
// // "preferredRegion": "URBAN_WEST",
// // "preferredDistrict": "MJINI",
// // "preferredSchoolType": "PRIMARY",
// // "applicationDate": "2026-04-15",
// // "cvFile": "cv.pdf",
// // "transcriptFile": "transcript.pdf",
// // "userId": 1
// // }

// // {
// // "firstName": "Shuayb",
// // "lastName": "Ali",
// // "gender": "MALE",
// // "dateOfBirth": "2000-05-10",
// // "phoneNumber": "0712345678",
// // "email": "shuayb@gmail.com",
// // "address": "Zanzibar",
// // "educationLevel": "DEGREE",
// // "courseStudied": "Information Technology",
// // "institutionName": "SUZA",
// // "graduationYear": 2024,
// // "qualificationFile": "qualification.pdf",
// // "preferredRegion": "URBAN_WEST",
// // "preferredDistrict": "MJINI",
// // "preferredSchoolType": "PRIMARY",
// // "applicationDate": "2026-04-15",
// // "cvFile": "cv.pdf",
// // "transcriptFile": "transcript.pdf",
// // "userId": 2
// // }
// // {
// //   "firstName": "Kauthar",
// //   "lastName": "Ali",
// //   "gender": "FEMALE",
// //   "dateOfBirth": "2000-06-15",
// //   "phoneNumber": "0712345678",
// //   "email": "kau@gmail.com",
// //   "address": "Zanzibar",
// //   "educationLevel": "DIPLOMA",
// //   "courseStudied": "Information Technology",
// //   "institutionName": "SUZA",
// //   "graduationYear": 2024,
// //   "qualificationFile": "qualification.pdf",
// //   "preferredRegion": "URBAN_WEST",
// //   "preferredDistrict": "MJINI",
// //   "preferredSchoolType": "PRIMARY",
// //   "applicationDate": "2026-04-16",
// //   "cvFile": "cv.pdf",
// //   "transcriptFile": "transcript.pdf",
// //   "userId": 3
// // }
// // REVIEW
// // {
// //     "status":"APPROVED",
// //     "reviewerName":"Admin John"
// // }
// // {
// //     "status":"REJECTED",
// //     "reviewerName":"Admin John"
// // }
// // {
// //     "status":"APPROVED",
// //     "reviewerName":"Admin Junaid"
// // }
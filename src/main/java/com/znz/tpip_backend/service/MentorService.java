// package com.znz.tpip_backend.service;

// import java.util.List;

// import org.modelmapper.ModelMapper;
// import org.springframework.stereotype.Service;

// import com.znz.tpip_backend.dto.MentorDto;
// import com.znz.tpip_backend.enums.MentorStatus;
// import com.znz.tpip_backend.enums.PlacementStatus;
// import com.znz.tpip_backend.model.Feedback;
// import com.znz.tpip_backend.model.Mentor;
// import com.znz.tpip_backend.model.Placement;
// import com.znz.tpip_backend.model.School;
// import com.znz.tpip_backend.repository.MentorRepository;
// import com.znz.tpip_backend.repository.SchoolRepository;

// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class MentorService {

//     private final MentorRepository mentorRepository;
//     private final ModelMapper modelMapper;
//     private final SchoolRepository schoolRepository;

//     public List<MentorDto> getAllMentors() {
//         return mentorRepository.findAll()
//                 .stream()
//                 .map(this::mapToDto)
//                 .toList();
//     }

//     public MentorDto getMentorById(Long id) {
//         Mentor mentor = mentorRepository.findById(id)
//                 .orElseThrow(() -> new IllegalStateException("Mentor not found with id " + id));

//         return mapToDto(mentor);
//     }

//     public MentorDto addMentor(MentorDto mentorDto) {

//         // 1. VALIDATE
//         if (mentorDto.getName() == null || mentorDto.getName().isBlank()) {
//             throw new IllegalArgumentException("Mentor name is required");
//         }

//         if (mentorDto.getEmail() == null || mentorDto.getEmail().isBlank()) {
//             throw new IllegalArgumentException("Email is required");
//         }

//         if (mentorDto.getSchoolId() == null) {
//             throw new IllegalArgumentException("School is required");
//         }

//         // 2. NORMALIZE
//         String name = mentorDto.getName().trim();
//         String email = mentorDto.getEmail().trim().toLowerCase();

//         // 3. CHECK DUPLICATE
//         mentorRepository.findByEmail(email).ifPresent(m -> {
//             throw new IllegalStateException("Email already exists");
//         });

//         // 4. GET SCHOOL
//         School school = schoolRepository.findById(mentorDto.getSchoolId())
//                 .orElseThrow(() -> new IllegalStateException("School not found"));

//         // 5. MANUAL MAP (IMPORTANT FIX)
//         Mentor mentor = new Mentor();
//         mentor.setName(name);
//         mentor.setEmail(email);
//         mentor.setQualificationLevel(mentorDto.getQualificationLevel());
//         mentor.setYearsOfExperience(mentorDto.getYearsOfExperience());
//         mentor.setSpecialization(mentorDto.getSpecialization());

//         // 6. RELATION + DEFAULTS
//         mentor.setSchool(school);
//         // mentor.setPassword(null);
//         mentor.setStatus(MentorStatus.ACTIVE);

//         Mentor savedMentor = mentorRepository.save(mentor);

//         return mapToDto(savedMentor);
//     }
//     // public MentorDto addMentor(MentorDto mentorDto) {

//     // // 1. VALIDATE
//     // if (mentorDto.getName() == null || mentorDto.getName().isBlank()) {
//     // throw new IllegalArgumentException("Mentor name is required");
//     // }

//     // if (mentorDto.getEmail() == null || mentorDto.getEmail().isBlank()) {
//     // throw new IllegalArgumentException("Email is required");
//     // }

//     // if (mentorDto.getSchoolId() == null) {
//     // throw new IllegalArgumentException("School is required");
//     // }

//     // // 2. NORMALIZE
//     // String name = mentorDto.getName().trim();
//     // String email = mentorDto.getEmail().trim().toLowerCase();

//     // if (mentorRepository.existsByEmail(email)) {
//     // throw new IllegalStateException("Email already exists");
//     // }

//     // // 3. GET SCHOOL
//     // School school = schoolRepository.findById(mentorDto.getSchoolId())
//     // .orElseThrow(() -> new IllegalStateException("School not found"));

//     // // 4. MAP
//     // mentorDto.setName(name);
//     // mentorDto.setEmail(email);

//     // Mentor mentor = modelMapper.map(mentorDto, Mentor.class);

//     // // 5. RELATION + DEFAULTS
//     // mentor.setSchool(school);
//     // mentor.setPassword(null);
//     // mentor.setStatus(MentorStatus.ACTIVE);

//     // Mentor savedMentor = mentorRepository.save(mentor);

//     // return mapToDto(savedMentor);
//     // }

//     // public void setPassword(String email, String password) {
//     // Mentor mentor = mentorRepository.findByEmail(email)
//     // .orElseThrow(() -> new IllegalStateException("Mentor not found"));

//     // if (mentor.getPassword() != null) {
//     // throw new IllegalStateException("Password already set");
//     // }

//     // mentor.setPassword(passwordEncoder.encode(password));

//     // mentorRepository.save(mentor);
//     // }

//     public MentorDto editMentor(Long id, MentorDto mentorDto) {

//         // 1. FIND EXISTING MENTOR
//         Mentor mentor = mentorRepository.findById(id)
//                 .orElseThrow(() -> new IllegalStateException("Mentor not found with id " + id));

//         // 2. BLOCK IF HAS ACTIVE INTERNS
//         boolean hasActivePlacement = mentor.getPlacements()
//                 .stream()
//                 .anyMatch(p -> p.getStatus() == PlacementStatus.ACTIVE);

//         if (hasActivePlacement) {
//             throw new IllegalStateException("Cannot edit mentor with ACTIVE interns");
//         }

//         // 3. VALIDATE INPUT (ONLY IF PROVIDED)
//         if (mentorDto.getName() != null && mentorDto.getName().isBlank()) {
//             throw new IllegalArgumentException("Mentor name cannot be blank");
//         }

//         if (mentorDto.getEmail() != null && mentorDto.getEmail().isBlank()) {
//             throw new IllegalArgumentException("Email cannot be blank");
//         }

//         // 4. NORMALIZE + UPDATE NAME
//         if (mentorDto.getName() != null) {
//             mentor.setName(mentorDto.getName().trim());
//         }

//         // 5. NORMALIZE + CHECK EMAIL
//         if (mentorDto.getEmail() != null) {

//             String normalizedEmail = mentorDto.getEmail().trim().toLowerCase();

//             // check duplicate BUT ignore current mentor
//             boolean emailExists = mentorRepository.existsByEmail(normalizedEmail)
//                     && !mentor.getEmail().equals(normalizedEmail);

//             if (emailExists) {
//                 throw new IllegalStateException("Email already exists");
//             }

//             mentor.setEmail(normalizedEmail);
//         }

//         // 6. UPDATE OTHER FIELDS (SAFE)
//         if (mentorDto.getQualificationLevel() != null) {
//             mentor.setQualificationLevel(mentorDto.getQualificationLevel());
//         }

//         if (mentorDto.getYearsOfExperience() > 0) {
//             mentor.setYearsOfExperience(mentorDto.getYearsOfExperience());
//         }

//         if (mentorDto.getSpecialization() != null) {
//             mentor.setSpecialization(mentorDto.getSpecialization().trim());
//         }

//         // 7. UPDATE SCHOOL (OPTIONAL)
//         if (mentorDto.getSchoolId() != null) {
//             School school = schoolRepository.findById(mentorDto.getSchoolId())
//                     .orElseThrow(() -> new IllegalStateException("School not found"));

//             mentor.setSchool(school);
//         }

//         // 8. SAVE
//         Mentor updated = mentorRepository.save(mentor);

//         return mapToDto(updated);
//     }

//     // RULE: cannot delete if assigned interns
//     public void deleteMentor(Long id) {
//         Mentor mentor = mentorRepository.findById(id)
//                 .orElseThrow(() -> new IllegalStateException("Mentor not found"));

//         boolean hasActivePlacement = mentor.getPlacements()
//                 .stream()
//                 .anyMatch(p -> p.getStatus() == PlacementStatus.ACTIVE);

//         if (hasActivePlacement) {
//             throw new IllegalStateException("Cannot delete mentor with ACTIVE placements");
//         }
//         mentorRepository.delete(mentor);
//     }

//     private MentorDto mapToDto(Mentor mentor) {
//         MentorDto dto = modelMapper.map(mentor, MentorDto.class);

//         // manually map fk
//         if (mentor.getSchool() != null) {
//             dto.setSchoolId(mentor.getSchool().getId());
//             dto.setSchoolName(mentor.getSchool().getName());
//         }
//         // manually map reverse r/ship ids only
//         dto.setPlacementIds(
//                 mentor.getPlacements() != null
//                         ? mentor.getPlacements()
//                                 .stream()
//                                 .map(Placement::getId)
//                                 .toList()
//                         : List.of());
//         dto.setFeedbackIds(
//                 mentor.getFeedbacks() != null
//                         ? mentor.getFeedbacks()
//                                 .stream()
//                                 .map(Feedback::getId)
//                                 .toList()
//                         : List.of());
//         // if (mentor.getEvaluations() != null) {
//         //     dto.setEvaluationIds(
//         //             mentor.getEvaluations()
//         //                     .stream()
//         //                     .map(eva -> eva.getId())
//         //                     .toList());
//         // }
//         return dto;

//     }
// }
// // CORRECT TPIP DESIGN (what you already did )
// // Flow:
// // Admin creates mentor + assigns school 
// // ↓
// // Mentor exists (no password)
// // ↓
// // Mentor sets password
// // ↓
// // Mentor logs in
// // WHY THIS IS CORRECT
// // Because:
// // Mentor belongs to a school (real life)
// // Admin controls mentor creation
// // Mentor should NOT self-register (security issue)

// // Admin creates mentor
// // ↓
// // Mentor exists (no password)
// // ↓
// // Mentor sets password
// // ↓
// // Mentor logs in
// // ↓
// // Gets JWT token
// // ↓
// // Access protected endpoints

// // OPTION 1 (RECOMMENDED) — Admin Creates + Mentor Sets Password
// // Flow:
// // 1. Admin creates mentor
// // {
// // "name": "Mr. Ali",
// // "email": "ali@gmail.com",
// // "schoolId": 1
// // }

// // 👉 System:

// // saves mentor
// // sets:
// // password = null ❗
// // status = ACTIVE
// // 2. Mentor sets password (FIRST TIME LOGIN)

// // 👉 Endpoint:

// // POST /auth/set-password
// // {
// // "email": "ali@gmail.com",
// // "password": "123456"
// // }

// // 👉 System:

// // finds mentor by email
// // encodes password (VERY IMPORTANT)
// // saves it
// // 3. Mentor logs in

// // 👉 Endpoint:

// // POST /auth/login
// // {
// // "email": "ali@gmail.com",
// // "password": "123456"
// // }

// // 👉 System:

// // validates password
// // returns JWT token
// // ✔ WHY THIS IS BEST
// // Admin controls who becomes mentor ✅
// // Mentor sets own password ✅
// // Secure & realistic system ✅




// // /ADD MENTOR 
// // {
// //     "name": "Ali Hassan",
// //     "email": "ali.hassan@gmail.com",
// //     "qualificationLevel": "DEGREE",
// //     "yearsOfExperience": 5,
// //     "specialization": "Mathematics",
// //     "status": "ACTIVE",
// //     "schoolId": 1
// //   }
// //  {
// //     "name": "Fatma Suleiman",
// //     "email": "fatma.suleiman@gmail.com",
// //     "qualificationLevel": "DIPLOMA",
// //     "yearsOfExperience": 3,
// //     "specialization": "English",
// //     "status": "ACTIVE",
// //     "schoolId": 2
// //   }
// //  {
// //     "name": "Hassan Omar",
// //     "email": "hassan.omar@gmail.com",
// //     "qualificationLevel": "DEGREE",
// //     "yearsOfExperience": 7,
// //     "specialization": "Physics",
// //     "status": "ACTIVE",
// //     "schoolId": 3
// //   }
// // {
// //     "name": "Salma Ali",
// //     "email": "salma.ali@gmail.com",
// //     "qualificationLevel": "DEGREE",
// //     "yearsOfExperience": 6,
// //     "specialization": "Biology",
// //     "status": "ACTIVE",
// //     "schoolId": 4
// //   }
// //   {
// //     "name": "Juma Abdallah",
// //     "email": "juma.abdallah@gmail.com",
// //     "qualificationLevel": "DIPLOMA",
// //     "yearsOfExperience": 4,
// //     "specialization": "Geography",
// //     "status": "ACTIVE",
// //     "schoolId": 5
// //   }
// // {
// //     "name": "Asha Mohammed",
// //     "email": "asha.mohammed@gmail.com",
// //     "qualificationLevel": "DEGREE",
// //     "yearsOfExperience": 8,
// //     "specialization": "Chemistry",
// //     "status": "ACTIVE",
// //     "schoolId": 6
// //   }
// package com.znz.tpip_backend.service;

// import java.time.LocalDate;
// import java.util.List;
// import java.util.stream.Collectors;

// import org.modelmapper.ModelMapper;
// import org.springframework.stereotype.Service;

// import com.znz.tpip_backend.dto.AssignRequestDto;
// import com.znz.tpip_backend.dto.PlacementDto;
// import com.znz.tpip_backend.enums.PlacementStatus;
// import com.znz.tpip_backend.model.Extension;
// import com.znz.tpip_backend.model.Intern;
// import com.znz.tpip_backend.model.Mentor;
// import com.znz.tpip_backend.model.Placement;
// import com.znz.tpip_backend.model.School;
// import com.znz.tpip_backend.repository.InternRepository;
// import com.znz.tpip_backend.repository.MentorRepository;
// import com.znz.tpip_backend.repository.PlacementRepository;
// import com.znz.tpip_backend.repository.SchoolRepository;

// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class PlacementService {

//     private final PlacementRepository placementRepository;
//     private final InternRepository internRepository;
//     private final SchoolRepository schoolRepository;
//     private final MentorRepository mentorRepository;
//     private final ModelMapper modelMapper;

//     // 1. OK BUT duplicate mapping to dto for fk in every methods
//     // public List<PlacementDto> getAllPlacements() {
//     // List<Placement> placements = placementRepository.findAll();
//     // return placements.stream()
//     // .map(placement -> {
//     // PlacementDto placementDto = modelMapper.map(placement, PlacementDto.class);

//     // // fk
//     // if (placement.getSchool() != null) {
//     // placementDto.setSchoolId(placement.getSchool().getId());
//     // }
//     // if (placement.getMentor() != null) {
//     // placementDto.setMentorId(placement.getMentor().getId());
//     // }
//     // if (placement.getIntern() != null) {
//     // placementDto.setInternId(placement.getIntern().getId());
//     // }
//     // return placementDto;
//     // }).collect(Collectors.toList());
//     // }

//     // BEST(USING mapToDto()):Cleaner, reusable and no duplication of fk mapping to
//     // dto
//     public List<PlacementDto> getAllPlacements() {
//         return placementRepository.findAll()
//                 .stream()
//                 .map(this::mapToDto) // ✅ CLEAN
//                 .collect(Collectors.toList());
//     }
//     // public PlacementDto getPlacementById(Long id) {
//     // Placement placement = placementRepository.findById(id)
//     // .orElseThrow(() -> new IllegalStateException("placement not found with id" +
//     // id));
//     // PlacementDto placementDto = modelMapper.map(placement, PlacementDto.class);
//     // // fk
//     // if (placement.getSchool() != null) {
//     // placementDto.setSchoolId(placement.getSchool().getId());
//     // }
//     // if (placement.getMentor() != null) {
//     // placementDto.setMentorId(placement.getMentor().getId());
//     // }
//     // if (placement.getIntern() != null) {
//     // placementDto.setInternId(placement.getIntern().getId());
//     // }

//     // return placementDto;
//     // }
//     public PlacementDto getPlacementById(Long id) {
//         Placement placement = placementRepository.findById(id)
//                 .orElseThrow(() -> new IllegalStateException("placement not found with id " + id));

//         return mapToDto(placement);
//     }

//     // // its like to create/add new placement
//     // // assignIntern() → create or update (only before start)
//     // public PlacementDto assignOrReassignIntern(AssignRequestDto request) {
//     // Intern intern = internRepository.findById(request.getInternId())
//     // .orElseThrow(() -> new IllegalStateException("intern not found with id" +
//     // request.getInternId()));
//     // School school = schoolRepository.findById(request.getSchoolId())
//     // .orElseThrow(() -> new IllegalStateException("school not found with id" +
//     // request.getSchoolId()));
//     // Mentor mentor = mentorRepository.findById(request.getMentorId())
//     // .orElseThrow(() -> new IllegalStateException("mentor not found with id" +
//     // request.getMentorId()));

//     // // check if placement alrdy exists for this intern
//     // Placement placement =
//     // placementRepository.findByInternId(request.getInternId()).orElse(null);

//     // if (placement != null) {
//     // // block if internship alrdy started/STRICT RULE: cannot reassign if ACTIVE
//     // if (placement.getStatus() == PlacementStatus.ACTIVE) {
//     // throw new IllegalStateException("Cannot reassign after internship started");
//     // }
//     // // update existing placement
//     // placement.setSchool(school);
//     // placement.setMentor(mentor);

//     // } else {
//     // // create new placement/first time assignment
//     // placement = new Placement();
//     // placement.setIntern(intern);
//     // placement.setSchool(school);
//     // placement.setMentor(mentor);

//     // // Assigned but not started yet
//     // placement.setAssignedDate(LocalDate.now());
//     // placement.setStatus(PlacementStatus.ASSIGNED);

//     // // after assigned when shld intern start internship
//     // // BEST PRACTICE: internship starts later (NOT now)
//     // // placement.setStartDate(null);
//     // // placement.setEndDate(null);

//     // }

//     // Placement savedPlacement = placementRepository.save(placement);
//     // // 1. manual mapping to dto
//     // // PlacementDto placeResponse = modelMapper.map(savedPlacement,
//     // // PlacementDto.class);
//     // // // manually map fk
//     // // placeResponse.setInternId(savedPlacement.getIntern().getId());
//     // // placeResponse.setSchoolId(savedPlacement.getSchool().getId());
//     // // placeResponse.setMentorId(savedPlacement.getMentor().getId());

//     // // return placeResponse;

//     // // 2. mapping to dto using helper mapToDto()
//     // return mapToDto(savedPlacement);
//     // }

//     public PlacementDto assignOrReassignIntern(AssignRequestDto request) {

//         // 1. FETCH DATA
//         Intern intern = internRepository.findById(request.getInternId())
//                 .orElseThrow(() -> new IllegalStateException("Intern not found"));

//         School school = schoolRepository.findById(request.getSchoolId())
//                 .orElseThrow(() -> new IllegalStateException("School not found"));

//         Mentor mentor = mentorRepository.findById(request.getMentorId())
//                 .orElseThrow(() -> new IllegalStateException("Mentor not found"));

//         // 2. 🔥 VALIDATION RULES (VERY IMPORTANT)

//         // RULE 1: Mentor must belong to same/selected school
//         if (mentor.getSchool() == null ||
//                 !mentor.getSchool().getId().equals(school.getId())) {
//             throw new IllegalStateException("Mentor does not belong to selected school");
//         }

//         // RULE 2: School capacity check
//         int current = school.getCurrentInternCount();
//         int capacity = school.getCapacity();

//         // int current = school.getCurrentInternCount() != null
//         // ? school.getCurrentInternCount()
//         // : 0;

//         // int capacity = school.getCapacity() != null
//         // ? school.getCapacity()
//         // : 0;
//         if (current >= capacity) {
//             throw new IllegalStateException("School has reached maximum intern capacity");
//         }
//         // RULE 3: Mentor workload limit (max 5 ACTIVE interns)
//         long activeInterns = mentor.getPlacements() == null
//                 ? 0
//                 : mentor.getPlacements().stream()
//                         .filter(p -> p.getStatus() == PlacementStatus.ACTIVE)
//                         .count();

//         if (activeInterns >= 5) {
//             throw new IllegalStateException("Mentor already has maximum interns");
//         }

//         // 3. CHECK EXISTING PLACEMENT
//         Placement placement = placementRepository
//                 .findByInternId(request.getInternId())
//                 .orElse(null);

//         if (placement != null) {

//             // RULE 4: Cannot reassign ACTIVE internship
//             if (placement.getStatus() == PlacementStatus.ACTIVE) {
//                 throw new IllegalStateException("Cannot reassign after internship started");
//             }

//             // UPDATE EXISTING
//             placement.setSchool(school);
//             placement.setMentor(mentor);

//         } else {

//             // CREATE NEW
//             placement = new Placement();
//             placement.setIntern(intern);
//             placement.setSchool(school);
//             placement.setMentor(mentor);

//             placement.setAssignedDate(LocalDate.now());
//             placement.setStatus(PlacementStatus.ASSIGNED);

//             // increase school count ONLY when new placement
//             school.setCurrentInternCount(current + 1);

//             schoolRepository.save(school);

//         }

//         Placement saved = placementRepository.save(placement);

//         return mapToDto(saved);
//     }

//     public PlacementDto updatePlacement(Long id, PlacementDto placementDto) {

//         // 1. FIND PLACEMENT
//         Placement placement = placementRepository.findById(id)
//                 .orElseThrow(() -> new IllegalStateException("Placement not found with id " + id));

//         // 2. BLOCK IF ACTIVE
//         if (placement.getStatus() == PlacementStatus.ACTIVE) {
//             throw new IllegalStateException("Cannot edit after internship started");
//         }

//         // 3. FETCH NEW RELATIONS
//         School newSchool = schoolRepository.findById(placementDto.getSchoolId())
//                 .orElseThrow(() -> new IllegalStateException("School not found"));

//         Mentor newMentor = mentorRepository.findById(placementDto.getMentorId())
//                 .orElseThrow(() -> new IllegalStateException("Mentor not found"));

//         // 4. RULE: Mentor must belong to School
//         // if (!newMentor.getSchool().getId().equals(newSchool.getId())) {
//         // throw new IllegalStateException("Mentor does not belong to selected school");
//         // }
//         if (newMentor.getSchool() == null ||
//                 !newMentor.getSchool().getId().equals(newSchool.getId())) {
//             throw new IllegalStateException("Mentor does not belong to selected school");
//         }

//         // 5. HANDLE SCHOOL COUNT CHANGE
//         School oldSchool = placement.getSchool();

//         if (oldSchool != null && !oldSchool.getId().equals(newSchool.getId())) {

//             int oldCount = oldSchool.getCurrentInternCount();
//             int newCount = newSchool.getCurrentInternCount();

//             // decrease old school count
//             oldSchool.setCurrentInternCount(Math.max(0, oldCount - 1));

//             // increase new school count
//             newSchool.setCurrentInternCount(newCount + 1);

//             // SAVE BOTH SCHOOLS ✔ IMPORTANT FIX
//             schoolRepository.save(oldSchool);
//             schoolRepository.save(newSchool);
//         }

//         // 6. UPDATE DATA
//         placement.setSchool(newSchool);
//         placement.setMentor(newMentor);

//         // 7. SAVE
//         Placement updated = placementRepository.save(placement);

//         return mapToDto(updated);
//     }

//     // startInternship() → move to ACTIVE
//     public PlacementDto startInternship(Long id) {
//         Placement placement = placementRepository.findById(id)
//                 .orElseThrow(() -> new IllegalStateException("placement not found with id" + id));

//         // ONLY allow if assigned
//         if (placement.getStatus() != PlacementStatus.ASSIGNED) {
//             throw new IllegalStateException(
//                     "Only ASSIGNED placements can be started. Current status: " + placement.getStatus());
//         }
//         LocalDate start = LocalDate.now();
//         LocalDate end = start.plusMonths(12);

//         placement.setStartDate(start);
//         placement.setEndDate(end);
//         placement.setStatus(PlacementStatus.ACTIVE);

//         Placement savedPlacement = placementRepository.save(placement);

//         // 1. manual mapping
//         // PlacementDto placeResponse = modelMapper.map(savedPlacement,
//         // PlacementDto.class);
//         // // manually map fk
//         // placeResponse.setInternId(savedPlacement.getIntern().getId());
//         // placeResponse.setSchoolId(savedPlacement.getSchool().getId());
//         // placeResponse.setMentorId(savedPlacement.getMentor().getId());

//         // return placeResponse;

//         // 2. mapping using helper
//         return mapToDto(savedPlacement);

//     }

//     public void deletePlacement(Long id) {

//         Placement placement = placementRepository.findById(id)
//                 .orElseThrow(() -> new IllegalStateException("placement not found with id" + id));
//         if (placement.getStatus() == PlacementStatus.ACTIVE) {
//             throw new IllegalStateException("Cannot delete ACTIVE placement");
//         }

//         // decrease count
//         School school = placement.getSchool();
//         school.setCurrentInternCount(school.getCurrentInternCount() - 1);
//         schoolRepository.save(school);

//         placementRepository.delete(placement);
//     }

//     // This is just a helper (support) method
//     // Its job is ONLY ONE thing: Convert a Placement (Entity) → PlacementDto
//     private PlacementDto mapToDto(Placement placement) {
//         PlacementDto dto = modelMapper.map(placement, PlacementDto.class);
//         // 2. manually set fk ids(BESST IDS +NAMES in JSON Response"
//         Intern intern = placement.getIntern();

//         if (intern != null) {
//             dto.setInternId(intern.getId());
//             // dto.setInternName(placement.getIntern().getApplication().getFirstName() + " "
//             // + placement.getIntern().getApplication().getLastName());
//             // with abv app will crash if application is null better use the below
//             if (intern.getApplication() != null) {
//                 dto.setInternName(
//                         intern.getApplication().getFirstName() + " " +
//                                 intern.getApplication().getLastName());
//             }
//         }

//         if (placement.getSchool() != null) {
//             dto.setSchoolId(placement.getSchool().getId());
//             dto.setSchoolName(placement.getSchool().getName());
//         }
//         if (placement.getMentor() != null) {
//             dto.setMentorId(placement.getMentor().getId());
//             dto.setMentorName(placement.getMentor().getName());
//         }
//         // result in JSON RESPONSE
//         // {
//         // "internId": 1,
//         // "internName": "Rahma Suleiman",
//         // "schoolId": 2,
//         // "schoolName": "Kiembe Samaki Secondary",
//         // "mentorId": 3,
//         // "mentorName": "Mr. Ali"
//         // }

//         // 3. manually map reverse r/ship ids only (example: extensionIds)
//         // using Method Reference
//         dto.setExtensionIds(
//                 placement.getExtensions() != null

//                         // ? → IF condition is true
//                         // .stream() → convert list to stream (process one by one)
//                         // .map(...) → transform each Extension to something else
//                         // Extension → class name
//                         // :: → method reference (call method)
//                         // getId → method to get ID from each Extension
//                         // .toList() → convert result back to List<Long>
//                         ? placement.getExtensions()
//                                 .stream()
//                                 .map(Extension::getId)
//                                 .toList()
//                         // : → ELSE (if extensions is null)
//                         // List.of() → return empty list (safe, no null)
//                         : List.of());
//         return dto;
//     }

// }

// // {
// // "internId": 1,
// // "schoolId": 6,
// // "mentorId": 9
// // }
// // {
// // "internId": 2,
// // "schoolId": 5,
// // "mentorId": 8
// // }
// // PROJECT FLOW
// // Application Approved
// // ↓
// // Intern Created
// // ↓
// // (Admin decides placement)
// // ↓
// // Assign / Reassign
// // ↓
// // Placement = ASSIGNED
// // ↓
// // Start Internship
// // ↓
// // Placement = ACTIVE
// // ↓
// // No more edits ❌

// // 🚀 If you want next (very useful)

// // Now you are ready for:

// // 🔥 completeInternship() (PASS / FAIL)
// // 🔥 extendInternship() (if failed)
// // 🔥 Activity logs (daily tracking)
// // 🔥 Evaluation system
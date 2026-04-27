// package com.znz.tpip_backend.service;

// import java.util.List;

// import org.modelmapper.ModelMapper;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import com.znz.tpip_backend.dto.SchoolDto;
// import com.znz.tpip_backend.model.Mentor;
// import com.znz.tpip_backend.model.Placement;
// import com.znz.tpip_backend.model.School;
// import com.znz.tpip_backend.repository.SchoolRepository;

// @Service
// public class SchoolService {

//     @Autowired
//     private SchoolRepository schoolRepository;
//     @Autowired
//     private ModelMapper modelMapper;

//     public List<SchoolDto> getAllSchools() {
//         List<School> schools = schoolRepository.findAll();
//         return schools.stream()
//                 .map(this::mapToDto)
//                 .toList();
//     }

//     public SchoolDto getSchoolById(Long id) {
//         School school = schoolRepository.findById(id)
//             .orElseThrow(() -> new IllegalStateException("School not found with id " + id));

//         return mapToDto(school);
//     }

//     // School is created by Admin before any placement.
//     public SchoolDto createSchool(SchoolDto schoolDto) {

//         // 1. VALIDATE INPUT FIRST
//         if (schoolDto.getName() == null || schoolDto.getName().isBlank()) {
//             throw new IllegalStateException("School name is required");
//         }

//         if (schoolDto.getCapacity() <= 0) {
//             throw new IllegalStateException("School capacity must be greater than 0");
//         }

//         if (schoolDto.getSchoolTypes() == null || schoolDto.getSchoolTypes().isEmpty()) {
//             throw new IllegalStateException("At least one school type is required");
//         }

//         // TRIM removes extra spaces Example: User sends: "name": " Kiembe Samaki Secondary " with trim result will b = "Kiembe Samaki Secondary")
//         // LOWERCASE makes comparison consistent Example 1: user sends- "email": "SCHOOL@GMAIL. WE  Normalize this way(using toLowerCase()): school.setEmail(schoolDto.getEmail().toLowerCase()); Result: "school@gmail.com")
//         // String normalizedName = schoolDto.getName().trim().toLowerCase();
//         String normalizedName = schoolDto.getName().trim();

//         // 3. CHECK DUPLICATE (after normalization)
//         if (schoolRepository.existsByNameIgnoreCase(normalizedName)) {
//             throw new IllegalStateException("School already exists with name: " + schoolDto.getName());
//         }

//         // 4. MAP DTO → ENTITY
//         School school = modelMapper.map(schoolDto, School.class);

//         // apply normalized name
//         school.setName(normalizedName);

//         // 5. SYSTEM CONTROLLED FIELD
//         school.setCurrentInternCount(0);

//         School savedSchool = schoolRepository.save(school);

//         // return modelMapper.map(savedSchool, SchoolDto.class);
//         return mapToDto(savedSchool);
//     }
// // https://github.com/Rahma-suleiman/tpip-backend-project
//     public SchoolDto editSchool(Long id, SchoolDto schoolDto) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'editSchool'");
//     }

//     public void deleteSchool(Long id) {
//         School school = schoolRepository.findById(id)
//             .orElseThrow(() -> new IllegalStateException("School not found with id " + id));
//         if(school.getCurrentInternCount() > 0) {
//             throw new IllegalStateException("Cannot delete school with active interns");    

//         }
//         schoolRepository.delete(school);
//     }


//     private SchoolDto mapToDto(School school) {
//         SchoolDto dto = modelMapper.map(school, SchoolDto.class);
        
//         dto.setPlacementIds(
//                 school.getPlacements() != null
//                         ? school.getPlacements()
//                                 .stream()
//                                 .map(Placement::getId)
//                                 .toList()
//                         : List.of());
//         dto.setMentorIds(
//                 school.getMentors() != null
//                         ? school.getMentors()
//                                 .stream()
//                                 .map(Mentor::getId)
//                                 .toList()
//                         : List.of());
//         return dto;

//     }
// }
// // {
// //   "name": "Kiembe Samaki Primary School",
// //   "location": "Kiembe Samaki, Zanzibar",
// //   "region": "URBAN_WEST",
// //   "district": "MJINI",
// //   "schoolTypes": ["PRIMARY"],
// //   "capacity": 20,
// //   "currentInternCount": 0,
// //   "phoneNumber": "0771000001",
// //   "email": "kiembe.primary@school.tz",
// //   "headTeacherName": "Mwalimu Asha Ali"
// // }
// // {
// //   "name": "Mpendae Primary School",
// //   "location": "Mpendae, Zanzibar",
// //   "region": "URBAN_WEST",
// //   "district": "MAGHARIBI_A",
// //   "schoolTypes": ["PRIMARY"],
// //   "capacity": 25,
// //   "currentInternCount": 0,
// //   "phoneNumber": "0771000002",
// //   "email": "mpendae.primary@school.tz",
// //   "headTeacherName": "Fatma Juma"
// // }
// // {
// //   "name": "Bububu Secondary School",
// //   "location": "Bububu, Zanzibar",
// //   "region": "URBAN_WEST",
// //   "district": "MAGHARIBI_B",
// //   "schoolTypes": ["SECONDARY"],
// //   "capacity": 15,
// //   "currentInternCount": 0,
// //   "phoneNumber": "0771000003",
// //   "email": "bububu.secondary@school.tz",
// //   "headTeacherName": "Mr. Hassan Omar"
// // }
// // {
// //   "name": "Donge Secondary School",
// //   "location": "Donge, Zanzibar North",
// //   "region": "NORTH_UNGUJA",
// //   "district": "KASKAZINI_A",
// //   "schoolTypes": ["SECONDARY"],
// //   "capacity": 18,
// //   "currentInternCount": 0,
// //   "phoneNumber": "0771000004",
// //   "email": "donge.secondary@school.tz",
// //   "headTeacherName": "Mr. Ali Suleiman"
// // }
// // {
// //   "name": "Mwembeladu School",
// //   "location": "Mwembeladu, Zanzibar",
// //   "region": "URBAN_WEST",
// //   "district": "MAGHARIBI_A",
// //   "schoolTypes": ["PRIMARY", "SECONDARY"],
// //   "capacity": 30,
// //   "currentInternCount": 0,
// //   "phoneNumber": "0771000005",
// //   "email": "mwembeladu.school@school.tz",
// //   "headTeacherName": "Mwalimu Salma Abdallah"
// // }
// // {
// //   "name": "Wete Combined School",
// //   "location": "Wete, Pemba",
// //   "region": "NORTH_PEMBA",
// //   "district": "WETE",
// //   "schoolTypes": ["PRIMARY", "SECONDARY"],
// //   "capacity": 35,
// //   "currentInternCount": 0,
// //   "phoneNumber": "0771000006",
// //   "email": "wete.combined@school.tz",
// //   "headTeacherName": "Mr. Juma Seif"
// // }
// package com.znz.tpip_backend.controller;

// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import com.znz.tpip_backend.dto.SchoolDto;
// import com.znz.tpip_backend.service.SchoolService;

// @RestController
// @RequestMapping("/api/v1/tpip/school")
// public class SchoolController {

//     @Autowired
//     private SchoolService schoolService;

//     @GetMapping
//     public ResponseEntity<List<SchoolDto>> getAllSchools() {
//         List<SchoolDto> schools = schoolService.getAllSchools();
//         return new ResponseEntity<>(schools, HttpStatus.OK);
//     }

//     @GetMapping("/{id}")
//     public ResponseEntity<SchoolDto> getSchoolById(@PathVariable Long id) {
//         SchoolDto schl = schoolService.getSchoolById(id);
//         return ResponseEntity.ok(schl);
//     }

//     @PostMapping
//     public ResponseEntity<SchoolDto> createSchool(@RequestBody SchoolDto schoolDto) {
//         SchoolDto schl = schoolService.createSchool(schoolDto);
//         return new ResponseEntity<>(schl, HttpStatus.CREATED);
//     }

//     @PutMapping("/{id}")
//     public ResponseEntity<SchoolDto> editSchool(@PathVariable Long id,
//             @RequestBody SchoolDto schoolDto) {
//         SchoolDto schl = schoolService.editSchool(id, schoolDto);
//         return new ResponseEntity<>(schl, HttpStatus.OK);
//     }

//     @DeleteMapping("/{id}")
//     public ResponseEntity<Void> deleteSchool(@PathVariable Long id) {
//         schoolService.deleteSchool(id);
//         return ResponseEntity.noContent().build();
//     }

// }


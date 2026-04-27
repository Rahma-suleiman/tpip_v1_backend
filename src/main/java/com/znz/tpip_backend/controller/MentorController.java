// package com.znz.tpip_backend.controller;

// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import com.znz.tpip_backend.dto.MentorDto;
// import com.znz.tpip_backend.service.MentorService;

// @RestController
// @RequestMapping("/api/v1/tpip/mentor")
// public class MentorController {

//     @Autowired
//     private MentorService mentorService;

//     @GetMapping
//     public ResponseEntity<List<MentorDto>> getAllMentors() {
//         List<MentorDto> mentors = mentorService.getAllMentors();
//         return new ResponseEntity<>(mentors, HttpStatus.OK);
//     }

//     @GetMapping("/{id}")
//     public ResponseEntity<MentorDto> getMentorById(@PathVariable Long id) {
//         MentorDto mentor = mentorService.getMentorById(id);
//         return ResponseEntity.ok(mentor);
//     }

//     @PostMapping
//     public ResponseEntity<MentorDto> addMentor(@RequestBody MentorDto mentorDto) {
//         MentorDto mentor = mentorService.addMentor (mentorDto);
//         return new ResponseEntity<>(mentor, HttpStatus.CREATED);
//     }

//     @PutMapping("/{id}")
//     public ResponseEntity<MentorDto> editMentor(@PathVariable Long id,
//             @RequestBody MentorDto mentorDto) {
//         MentorDto mentor = mentorService.editMentor(id, mentorDto);
//         return new ResponseEntity<>(mentor, HttpStatus.OK);
//     }

//     @DeleteMapping("/{id}")
//     public ResponseEntity<Void> deleteMentor(@PathVariable Long id) {
//         mentorService.deleteMentor(id);
//         return ResponseEntity.noContent().build();
//     }

// }

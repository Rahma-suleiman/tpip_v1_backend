// package com.znz.tpip_backend.controller;

// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import com.znz.tpip_backend.dto.ApplicationDto;
// import com.znz.tpip_backend.service.ApplicationService;


// @RestController
// @RequestMapping("/api/v1/tpip/application")
// public class ApplicationController {

//     @Autowired
//     private ApplicationService applicationService;

//     @GetMapping
//     public ResponseEntity<List<ApplicationDto>> getAllApplications() {
//         List<ApplicationDto> applications = applicationService.getAllApplications();
//         return new ResponseEntity<>(applications, HttpStatus.OK);        
//     }

//     @GetMapping("/{id}")
//     public ResponseEntity<ApplicationDto> getApplicationById(@PathVariable Long id) {
//         ApplicationDto app = applicationService.getApplicationById(id);
//         return ResponseEntity.ok(app);       
//     }
  
    
//     @PostMapping
//     public ResponseEntity<ApplicationDto> submitApplication(@RequestBody ApplicationDto applicationDto) {
//         ApplicationDto application = applicationService.submitApplication(applicationDto);
//         return new ResponseEntity<>(application, HttpStatus.CREATED);        
//     }
//     @PutMapping("/{id}")
//     public ResponseEntity<ApplicationDto> editApplication(@PathVariable Long id, @RequestBody ApplicationDto applicationDto){
//         ApplicationDto application = applicationService.editApplication(id, applicationDto);
//         return new ResponseEntity<>(application, HttpStatus.OK);        
//     }

//     @DeleteMapping("/{id}")
//     public ResponseEntity<Void> deleteApplication(@PathVariable Long id) {
//         applicationService.deleteApplication(id);
//         return ResponseEntity.noContent().build();
//     }
    
// }

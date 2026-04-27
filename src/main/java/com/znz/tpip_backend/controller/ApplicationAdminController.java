// package com.znz.tpip_backend.controller;

// import org.springframework.web.bind.annotation.*;

// import com.znz.tpip_backend.dto.ApplicationDto;
// import com.znz.tpip_backend.dto.ApplicationReviewDto;
// import com.znz.tpip_backend.service.ApplicationService;

// import lombok.RequiredArgsConstructor;

// @RestController
// @RequestMapping("/api/admin/applications")
// @RequiredArgsConstructor
// public class ApplicationAdminController {

//     private final ApplicationService applicationService;

//     @PutMapping("/{id}/review")
//     public ApplicationDto reviewApplication(
//             @PathVariable Long id,
//             @RequestBody ApplicationReviewDto request) {

//         return applicationService.reviewApplication(
//                 id,
//                 request.getStatus(),
//                 request.getReviewerName()
//         );
//     }
// }
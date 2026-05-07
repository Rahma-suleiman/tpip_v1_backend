// package com.znz.tpip_backend.controller;

// import com.znz.tpip_backend.dto.AdminActionRequest;
// import com.znz.tpip_backend.service.adminWorkflow.AdminReviewService;
// import lombok.RequiredArgsConstructor;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// @RestController
// @RequestMapping("/api/admin/applications")
// @RequiredArgsConstructor
// public class AdminApplicationController {

//     private final AdminReviewService adminReviewService;

//     // ================= START REVIEW =================
//     @PostMapping("/{applicationId}/start-review")
//     public ResponseEntity<String> startReview(@PathVariable Long applicationId) {

//         adminReviewService.startReview(applicationId);

//         return ResponseEntity.ok("Application moved to UNDER_REVIEW");
//     }

//     // ================= RETURN FOR CORRECTION =================
//     @PostMapping("/{applicationId}/return-correction")
//     public ResponseEntity<String> returnForCorrection(
//             @PathVariable Long applicationId,
//             @RequestBody AdminActionRequest request
//     ) {

//         adminReviewService.returnForCorrection(
//                 applicationId,
//                 request.getReason()
//         );

//         return ResponseEntity.ok("Application returned for correction");
//     }

//     // ================= MARK AS REVIEWED =================
//     @PostMapping("/{applicationId}/mark-reviewed")
//     public ResponseEntity<String> markReviewed(@PathVariable Long applicationId) {

//         adminReviewService.markReviewed(applicationId);

//         return ResponseEntity.ok("Application marked as REVIEWED");
//     }

//     // ================= APPROVE =================
//     @PostMapping("/{applicationId}/approve")
//     public ResponseEntity<String> approve(@PathVariable Long applicationId) {

//         adminReviewService.approve(applicationId);

//         return ResponseEntity.ok("Application APPROVED");
//     }

//     // ================= REJECT =================
//     @PostMapping("/{applicationId}/reject")
//     public ResponseEntity<String> reject(
//             @PathVariable Long applicationId,
//             @RequestBody AdminActionRequest request
//     ) {

//         adminReviewService.reject(
//                 applicationId,
//                 request.getReason()
//         );

//         return ResponseEntity.ok("Application REJECTED");
//     }

//     // ================= WAITLIST =================
//     @PostMapping("/{applicationId}/waitlist")
//     public ResponseEntity<String> waitlist(@PathVariable Long applicationId) {

//         adminReviewService.waitlist(applicationId);

//         return ResponseEntity.ok("Application WAITLISTED");
//     }
// }

package com.znz.tpip_backend.controller;

import com.znz.tpip_backend.dto.AdminActionRequest;
import com.znz.tpip_backend.service.adminWorkflow.AdminReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/applications")
@RequiredArgsConstructor
public class AdminApplicationController {

    private final AdminReviewService adminReviewService;

    @PostMapping("/{applicationId}/start-review")
    public ResponseEntity<String> startReview(@PathVariable Long applicationId) {

        adminReviewService.startReview(applicationId);
        return ResponseEntity.ok("UNDER_REVIEW");
    }

    @PostMapping("/{applicationId}/return-correction")
    public ResponseEntity<String> returnForCorrection(
            @PathVariable Long applicationId,
            @RequestBody AdminActionRequest request
    ) {
        adminReviewService.returnForCorrection(applicationId, request.getReason());
        return ResponseEntity.ok("RETURNED_FOR_CORRECTION");
    }

    @PostMapping("/{applicationId}/mark-reviewed")
    public ResponseEntity<String> markReviewed(@PathVariable Long applicationId) {

        adminReviewService.markReviewed(applicationId);
        return ResponseEntity.ok("REVIEWED");
    }

    @PostMapping("/{applicationId}/move-to-approval")
    public ResponseEntity<String> moveToApproval(@PathVariable Long applicationId) {

        adminReviewService.moveToApproval(applicationId);
        return ResponseEntity.ok("UNDER_APPROVAL");
    }

    @PostMapping("/{applicationId}/approve")
    public ResponseEntity<String> approve(@PathVariable Long applicationId) {

        adminReviewService.approve(applicationId);
        return ResponseEntity.ok("APPROVED");
    }

    @PostMapping("/{applicationId}/reject")
    public ResponseEntity<String> reject(
            @PathVariable Long applicationId,
            @RequestBody AdminActionRequest request
    ) {
        adminReviewService.reject(applicationId, request.getReason());
        return ResponseEntity.ok("REJECTED");
    }

    @PostMapping("/{applicationId}/waitlist")
    public ResponseEntity<String> waitlist(@PathVariable Long applicationId) {

        adminReviewService.waitlist(applicationId);
        return ResponseEntity.ok("WAITLISTED");
    }
}
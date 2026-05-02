// package com.znz.tpip_backend.controller;

// import com.znz.tpip_backend.service.PaymentProcessingService;
// import lombok.RequiredArgsConstructor;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// @RestController
// @RequestMapping("/webhook")
// @RequiredArgsConstructor
// public class PaymentWebhookController {

//     private final PaymentProcessingService processingService;

//     @PostMapping("/mpesa")
//     public ResponseEntity<String> mpesaWebhook(
//             @RequestBody String payload,
//             @RequestHeader("X-Signature") String signature) {

//         processingService.process("MPESA", payload, signature);
//         return ResponseEntity.ok("MPESA webhook processed");
//     }

//     @PostMapping("/tigopesa")
//     public ResponseEntity<String> tigopesaWebhook(
//             @RequestBody String payload,
//             @RequestHeader("X-Signature") String signature) {

//         processingService.process("TIGO", payload, signature);
//         return ResponseEntity.ok("TIGO webhook processed");
//     }
// }
package com.znz.tpip_backend.controller;

import com.znz.tpip_backend.dto.PaymentWebhookDTO;
import com.znz.tpip_backend.service.PaymentProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class PaymentWebhookController {

    private final PaymentProcessingService processingService;

    // ================= MPESA WEBHOOK =================
    @PostMapping("/mpesa")
    public ResponseEntity<String> mpesaWebhook(
            @RequestBody PaymentWebhookDTO dto,
            @RequestHeader("X-Signature") String signature) {

        dto.setProvider("MPESA");
        dto.setSignature(signature);

        processingService.process(dto);
        return ResponseEntity.ok("MPESA webhook processed");
    }

    // ================= TIGO PESA WEBHOOK =================
    @PostMapping("/tigopesa")
    public ResponseEntity<String> tigopesaWebhook(
            @RequestBody PaymentWebhookDTO dto,
            @RequestHeader("X-Signature") String signature) {

        dto.setProvider("TIGO_PESA");
        dto.setSignature(signature);

        processingService.process(dto);
        return ResponseEntity.ok("TIGO PESA webhook processed");
    }
}
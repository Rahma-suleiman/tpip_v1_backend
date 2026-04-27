// package com.znz.tpip_backend.controller;

// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import com.znz.tpip_backend.dto.ExtensionDto;
// import com.znz.tpip_backend.service.ExtensionService;

// @RestController
// @RequestMapping("/api/v1/tpip/extension")
// public class ExtensionController {

//     @Autowired
//     private ExtensionService extensionService;

//     @GetMapping
//     public ResponseEntity<List<ExtensionDto>> getAllExtensions() {
//         List<ExtensionDto> extensions = extensionService.getAllExtensions();
//         return new ResponseEntity<>(extensions, HttpStatus.OK);
//     }

//     @GetMapping("/{id}")
//     public ResponseEntity<ExtensionDto> getExtensionById(@PathVariable Long id) {
//         ExtensionDto ext = extensionService.getExtensionById(id);
//         return ResponseEntity.ok(ext);
//     }
//     @GetMapping("/placement/{placementId}")
//     public ResponseEntity<List<ExtensionDto>> getExtensionByPlacementId(@PathVariable Long placementId) {
//         List<ExtensionDto> ext = extensionService.getExtensionByPlacementId(placementId);
//         return ResponseEntity.ok(ext);
//     }

//     // AUTO CREATE FROM EVALUATION
//     @PostMapping("/from-evaluation/{evaluationId}")
//     public ResponseEntity<ExtensionDto> createExtensionFromEvaluation(@PathVariable Long evaluationId) {
//         ExtensionDto extension = extensionService.createExtensionFromEvaluation(evaluationId);
//         return new ResponseEntity<>(extension, HttpStatus.CREATED);
//     }

//     @PutMapping("/{id}/complete")
//     public ResponseEntity<Void> completeExtension(@PathVariable Long id) {
//         extensionService.completeExtension(id);
//         return ResponseEntity.noContent().build();
//     }
//     @PutMapping("/{id}/cancel")
//     public void cancel(@PathVariable Long id) {
//         extensionService.cancelExtension(id);
//     }

// }

// package com.znz.tpip_backend.controller;

// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import com.znz.tpip_backend.dto.EvaluationDto;
// import com.znz.tpip_backend.enums.EvaluationStatus;
// import com.znz.tpip_backend.service.EvaluationService;
// import com.znz.tpip_backend.service.ExtensionService;

// @RestController
// @RequestMapping("/api/v1/tpip/evaluation")
// public class EvaluationController {
//     @Autowired
//     private EvaluationService evaluationService;
//     @Autowired
//     private ExtensionService extensionService;

//     @GetMapping
//     public ResponseEntity<List<EvaluationDto>> getAllEvaluations() {
//         List<EvaluationDto> evaluations = evaluationService.getAllEvaluations();
//         return new ResponseEntity<>(evaluations, HttpStatus.OK);
//     }

//     @GetMapping("/{id}")
//     public ResponseEntity<EvaluationDto> getEvaluationById(@PathVariable Long id) {
//         EvaluationDto eva = evaluationService.getEvaluationById(id);
//         return ResponseEntity.ok(eva);
//     }

//     // GET BY PLACEMENT ID
//     @GetMapping("/placement/{placementId}")
//     public ResponseEntity<List<EvaluationDto>> getByPlacement(@PathVariable Long placementId) {
//         return ResponseEntity.ok(evaluationService.getByPlacement(placementId));
//     }

//     @PostMapping
//     public ResponseEntity<EvaluationDto> createEvaluation(
//     @RequestBody EvaluationDto dto,
//     @RequestHeader("mentorId") Long mentorId) {
//     EvaluationDto evaluation = evaluationService.createEvaluation(dto, mentorId);
//     return new ResponseEntity<>(evaluation, HttpStatus.CREATED);
//     }

  
//     // @PutMapping("/{id}")
//     // public ResponseEntity<EvaluationDto> editEvaluation(@PathVariable Long id,
//     // @RequestBody EvaluationDto evaluationDto) {
//     // EvaluationDto evaluation = evaluationService.editEvaluation(id,
//     // evaluationDto);
//     // return new ResponseEntity<>(evaluation, HttpStatus.OK);
//     // }

//     @PutMapping("/{id}")
//     public ResponseEntity<EvaluationDto> update(
//             @PathVariable Long id,
//             @RequestBody EvaluationDto dto,
//             @RequestHeader("mentorId") Long mentorId) {

//         return ResponseEntity.ok(evaluationService.updateEvaluation(id, dto, mentorId));
//     }

//     @DeleteMapping("/{id}")
//     public ResponseEntity<Void> deleteEvaluation(@PathVariable Long id) {
//         evaluationService.deleteEvaluation(id);
//         return ResponseEntity.noContent().build();
//     }
// }

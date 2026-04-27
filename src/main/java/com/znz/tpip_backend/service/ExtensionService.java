// package com.znz.tpip_backend.service;

// import java.time.LocalDate;
// import java.util.List;
// import java.util.stream.Collectors;

// import org.modelmapper.ModelMapper;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import com.znz.tpip_backend.dto.ExtensionDto;
// import com.znz.tpip_backend.enums.EvaluationStatus;
// import com.znz.tpip_backend.enums.EvaluationType;
// import com.znz.tpip_backend.enums.ExtensionStatus;
// import com.znz.tpip_backend.model.Evaluation;
// import com.znz.tpip_backend.model.Extension;
// import com.znz.tpip_backend.model.Placement;
// import com.znz.tpip_backend.repository.EvaluationRepository;
// import com.znz.tpip_backend.repository.ExtensionRepository;
// // import com.znz.tpip_backend.repository.PlacementRepository;

// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class ExtensionService {

//     private final ExtensionRepository extensionRepository;
//     private final EvaluationRepository evaluationRepository;
//     // private final PlacementRepository placementRepository;
//     // private final ModelMapper modelMapper;

//     public List<ExtensionDto> getAllExtensions() {
//         List<Extension> extensions = extensionRepository.findAll();
//         return extensions.stream()
//                 .map(this::mapToDto)
//                 .collect(Collectors.toList());
//     }

//     public ExtensionDto getExtensionById(Long id) {
//         Extension ext = extensionRepository.findById(id)
//                 .orElseThrow(() -> new IllegalStateException("Extension not found"));

//         return mapToDto(ext);
//     }

//     public List<ExtensionDto> getExtensionByPlacementId(Long placementId) {
//         return extensionRepository.findByPlacementId(placementId)
//                 .stream()
//                 .map(this::mapToDto)
//                 .collect(Collectors.toList());
//     }

//     // AUTO CREATE EXTENSION FROM EVALUATION
//     public ExtensionDto createExtensionFromEvaluation(Long evaluationId) {

//         Evaluation evaluation = evaluationRepository.findById(evaluationId)
//                 .orElseThrow(() -> new IllegalStateException("Evaluation not found"));

//         // must require extension
//         if (evaluation.getStatus() != EvaluationStatus.REQUIRES_EXTENSION) {
//             throw new IllegalStateException("Extension only allowed for REQUIRES_EXTENSION");
//         }

//         // only FINAL or REASSESSMENT
//         if (evaluation.getEvaluationType() == EvaluationType.MIDTERM) {
//             throw new IllegalStateException("MIDTERM evaluation cannot create extension");
//         }
//         if (evaluation.getExtension() != null) {
//             throw new IllegalStateException("Extension already exists for this evaluation");
//         }
//         if (evaluation.getPlacement() == null) {
//             throw new IllegalStateException("Evaluation is not linked to a placement");
//         }
//         Placement placement = evaluation.getPlacement();
//         // prevent duplicate active extension
//         boolean exists = extensionRepository
//                 .existsByPlacementIdAndStatus(
//                         placement.getId(),
//                         ExtensionStatus.ACTIVE);

//         if (exists) {
//             throw new IllegalStateException("Active extension already exists for this placement");
//         }

//         // ✅ CREATE EXTENSION
//         Extension extension = new Extension();
//         extension.setPlacement(placement);
//         extension.setEvaluation(evaluation);

//         if (evaluation.getEvaluationType() == EvaluationType.FINAL) {
//             extension.setExtraDays(14);
//         } else {
//             extension.setExtraDays(7);
//         }
//         // extension.setReason(evaluation.getRemarks()); OR
//         extension.setReason(
//                 evaluation.getRemarks() != null
//                         ? evaluation.getRemarks()
//                         : "Performance needs improvement");

//         LocalDate start = LocalDate.now();
//         extension.setStartDate(start);
//         extension.setEndDate(start.plusDays(extension.getExtraDays()));

//         extension.setStatus(ExtensionStatus.ACTIVE);

//         Extension saved = extensionRepository.save(extension);

//         evaluation.setExtension(saved);
//         evaluationRepository.save(evaluation);
//         return mapToDto(saved);
//     }

//     public void completeExtension(Long id) {
//         Extension extension = extensionRepository.findById(id)
//                 .orElseThrow(() -> new IllegalStateException("Extension not found"));

//         if (extension.getStatus() != ExtensionStatus.ACTIVE) {
//             throw new IllegalStateException("Only ACTIVE extensions can be completed");
//         }
//         extension.setStatus(ExtensionStatus.COMPLETED);
//         extensionRepository.save(extension);
//     }

//     public void cancelExtension(Long id) {
//         Extension extension = extensionRepository.findById(id)
//                 .orElseThrow(() -> new IllegalStateException("Extension not found"));

//         if (extension.getStatus() != ExtensionStatus.ACTIVE) {
//             throw new IllegalStateException("Only ACTIVE extensions can be cancelled");
//         }
//         extension.setStatus(ExtensionStatus.CANCELLED);
//         extensionRepository.save(extension);
//     }

//     private ExtensionDto mapToDto(Extension ext) {
//         ExtensionDto dto = new ExtensionDto();

//         dto.setId(ext.getId());
//         dto.setExtraDays(ext.getExtraDays());
//         dto.setReason(ext.getReason());
//         dto.setStartDate(ext.getStartDate());
//         dto.setEndDate(ext.getEndDate());
//         dto.setStatus(ext.getStatus());

//         if (ext.getPlacement() != null) {
//             dto.setPlacementId(ext.getPlacement().getId());
//         }

//         if (ext.getEvaluation() != null) {
//             dto.setEvaluationId(ext.getEvaluation().getId());
//         }

//         return dto;
//     }
// }
// // Mentor evaluates intern
// // ↓
// // Evaluation created
// // ↓
// // IF REQUIRES_EXTENSION
// // ↓
// // Extension AUTO created
// // ↓
// // Intern continues internship
// // ↓
// // Reassessment done later
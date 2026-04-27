// package com.znz.tpip_backend.controller;

// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import com.znz.tpip_backend.dto.AssignRequestDto;
// import com.znz.tpip_backend.dto.PlacementDto;
// import com.znz.tpip_backend.service.PlacementService;
// import org.springframework.web.bind.annotation.PutMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.PathVariable;


// @RestController
// @RequestMapping("/api/v1/tpip/placements")
// public class PlacementController {

//     @Autowired
//     private PlacementService placementService;

//     @GetMapping
//     public ResponseEntity<List<PlacementDto>> getAllPlacements() {
//         List<PlacementDto> placements = placementService.getAllPlacements();
//         return new ResponseEntity<>(placements, HttpStatus.OK);
//     }

//     @GetMapping("/{id}")
//     public ResponseEntity<PlacementDto> getPlacementById(@PathVariable Long id) {
//         PlacementDto placement = placementService.getPlacementById(id);
//         return ResponseEntity.ok(placement);
//     }

//     // ASSIGN/REASSIGN INTERN
//     // although all 3 parameters r ids, we cant use @PathVariable for all, bcz @PathVariable is used for ONLY ONE resource id, but if u hv many other id inputs/params the others use @RequestParam
//     // @PostMapping
//     // public ResponseEntity<InternDto> assignIntern(@PathVariable Long internId,
//     //     @RequestParam Long schoolId, @RequestParam Long mentorId){
//     //     InternDto intern = placementService.assignIntern(internId,schoolId,mentorId);
//     //     return new ResponseEntity<>(intern, HttpStatus.CREATED);
//     // }
//     // OR (BELOW IS BEST)
//     @PostMapping
//     public ResponseEntity<PlacementDto> createPlacement(@RequestBody AssignRequestDto request){
//         PlacementDto placement = placementService.assignOrReassignIntern(request);  
//         return new ResponseEntity<>(placement, HttpStatus.CREATED);
//     }

//     @PutMapping("/{id}")
//     public ResponseEntity<PlacementDto> editPlacement(@PathVariable Long id,
//             @RequestBody PlacementDto placementDto) {
//         PlacementDto updated = placementService.updatePlacement(id, placementDto);
//         return new ResponseEntity<>(updated, HttpStatus.OK);
//     }
//     // @PutMapping("/interns/{id}/assign")
//     // public ResponseEntity<InternDto> assignIntern(@PathVariable Long id,
//     //         @RequestParam Long schoolId, @RequestParam Long mentorId) {
//     //     InternDto dto = placementService.assignIntern(id, schoolId,mentorId);
//     //     return new ResponseEntity<>(dto, HttpStatus.OK);
//     // }

//     // start internship
//     @PutMapping("/{id}/start")
//     public ResponseEntity<PlacementDto> startInternship(@PathVariable Long id) {
//         PlacementDto dto = placementService.startInternship(id);
//         return ResponseEntity.ok(dto);
//     }
//     @DeleteMapping("/{id}")
//     public ResponseEntity<Void> deletePlacement(@PathVariable Long id) {
//         placementService.deletePlacement(id);
//         return ResponseEntity.noContent().build();
//     }

// }

// package com.znz.tpip_backend.controller;

// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import com.znz.tpip_backend.dto.InternDto;
// import com.znz.tpip_backend.model.Application;
// import com.znz.tpip_backend.model.Intern;
// import com.znz.tpip_backend.service.InternService;

// @RestController
// @RequestMapping("/api/v1/tpip/intern")
// public class InternController {
    
//     @Autowired
//     private InternService internService;

//     @GetMapping
//     public ResponseEntity<List<InternDto>> getAllInterns() {
//         List<InternDto> interns = internService.getAllInterns();
//         return new ResponseEntity<>(interns, HttpStatus.OK);
//     }

//     @GetMapping("/{id}")
//     public ResponseEntity<InternDto> getInternById(@PathVariable Long id) {
//         InternDto intern = internService.getInternById(id);
//         return ResponseEntity.ok(intern);
//     }


//     // @PutMapping("/{id}")
//     // public ResponseEntity<InternDto> editIntern(@PathVariable Long id,
//     //         @RequestBody InternDto internDto) {
//     //     InternDto intern = internService.editIntern(id, internDto);
//     //     return new ResponseEntity<>(intern, HttpStatus.OK);
//     // }

//     @DeleteMapping("/{id}")
//     public ResponseEntity<Void> deleteIntern(@PathVariable Long id) {
//         internService.deleteIntern(id);
//         return ResponseEntity.noContent().build();
//     }
// }

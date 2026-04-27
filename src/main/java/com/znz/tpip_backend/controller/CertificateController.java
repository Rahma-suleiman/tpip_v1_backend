// package com.znz.tpip_backend.controller;

// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import com.znz.tpip_backend.dto.CertificateDto;
// import com.znz.tpip_backend.service.CertificateService;

// @RestController
// @RequestMapping("/api/v1/tpip/certificate")
// public class CertificateController {

//     @Autowired
//     private CertificateService certificateService;

//     @GetMapping
//     public ResponseEntity<List<CertificateDto>> getAllCertificates() {
//         List<CertificateDto> certificates= certificateService.getAllCertificates();
//         return new ResponseEntity<>(certificates, HttpStatus.OK);        
//     }

//     @GetMapping("/{id}")
//     public ResponseEntity<CertificateDto> getCertificateById(@PathVariable Long id) {
//         CertificateDto cert = certificateService.getCertificateById(id);
//         return ResponseEntity.ok(cert);       
//     }
  
    
//     @PostMapping
//     public ResponseEntity<CertificateDto> addCertificate(@RequestBody CertificateDto certificateDto) {
//         CertificateDto certificate = certificateService.addCertificate(certificateDto);
//         return new ResponseEntity<>(certificate, HttpStatus.CREATED);        
//     }
//     @PutMapping("/{id}")
//     public ResponseEntity<CertificateDto> editCertificate(@PathVariable Long id, @RequestBody CertificateDto certificateDto){
//         CertificateDto certificate = certificateService.editCertificate(id, certificateDto);
//         return new ResponseEntity<>(certificate, HttpStatus.OK);        
//     }

//     @DeleteMapping("/{id}")
//     public ResponseEntity<Void> deleteCertificate(@PathVariable Long id) {
//         certificateService.deleteCertificate(id);
//         return ResponseEntity.noContent().build();
//     }
// }

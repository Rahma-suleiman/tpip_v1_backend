// package com.znz.tpip_backend.service;

// import java.util.List;

// import org.modelmapper.ModelMapper;
// import org.springframework.stereotype.Service;

// import com.znz.tpip_backend.dto.InternDto;
// import com.znz.tpip_backend.enums.InternStatus;
// import com.znz.tpip_backend.model.Application;
// import com.znz.tpip_backend.model.Evaluation;
// import com.znz.tpip_backend.model.Extension;
// import com.znz.tpip_backend.model.Intern;
// import com.znz.tpip_backend.repository.InternRepository;

// import jakarta.transaction.Transactional;
// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class InternService {

//     private final InternRepository internRepository;
//     private final ModelMapper modelMapper;

//     public List<InternDto> getAllInterns() {
//         List<Intern> interns = internRepository.findAll();
//         // interns.stream() → convert list into stream (process items one by one)
//         // .map(...) → transform each Intern into something else
//         // this → current class (InternService)
//         // :: → call method
//         // mapToDto → method that converts Intern → InternDto
//         // .toList() → convert stream back into list
//         return interns.stream()
//                 .map(this::mapToDto)
//                 .toList();
//     }

//     public InternDto getInternById(Long id) {
//         Intern intern = internRepository.findById(id)
//                 .orElseThrow(() -> new RuntimeException("intern not found with id " + id));
//         // mapToDto(intern) → convert Intern → InternDto
//         return mapToDto(intern);
//     }

//     // @Transactional
//     // public Intern createInternFromApplication(Application app) {

//     // if (app == null) {
//     // throw new IllegalStateException("Application is null");
//     // }

//     // Intern intern = new Intern();

//     // intern.setApplication(app);
//     // intern.setStatus(InternStatus.ACTIVE);
//     // intern.setStartDate(LocalDate.now());
//     // intern.setEducationLevel(app.getEducationLevel());
//     // intern.setSpecialization(app.getCourseStudied());
//     // intern.setGraduationYear(app.getGraduationYear());

//     // return internRepository.save(intern);
//     // }
//     // BEST WAY: BCZ of Separation of responsibilities : reviewApplication()= for
//     // admin decision(approave/reject) while createInternFromApplication()= for
//     // intern creation logic
//     @Transactional
//     public Intern createInternFromApplication(Application app) {

//         if (app == null) {
//             throw new IllegalStateException("Application is null");
//         }
//         if (app.getIntern() != null) {
//             throw new IllegalStateException("Intern already exists for this application");
//         }

//         Intern intern = new Intern();
//         intern.setApplication(app);

//         intern.setStatus(InternStatus.ACTIVE);
//         // intern.setStartDate(LocalDate.now());
//         intern.setEducationLevel(app.getEducationLevel());
//         intern.setSpecialization(app.getCourseStudied());
//         intern.setGraduationYear(app.getGraduationYear());

//         Intern savedIntern = internRepository.save(intern);

//         app.setIntern(savedIntern); // set the intern reference in the application
//         return savedIntern;
//     }

//     // public InternDto editIntern(Long id, InternDto internDto) {
//     // // TODO Auto-generated method stub
//     // throw new UnsupportedOperationException("Unimplemented method 'editIntern'");
//     // }

//     public void deleteIntern(Long id) {

//         Intern intern = internRepository.findById(id)
//                 .orElseThrow(() -> new RuntimeException("Intern not found"));

//         // if (intern.getStatus() == PlacementStatus.ACTIVE) {
//         // throw new IllegalStateException("Cannot delete intern who is active");
//         // }

//         // BUT TPIP RULE:

//         // 👉 Intern should NOT be deleted if:

//         // Placement is ACTIVE
//         // Has evaluations
//         // Has certificate
//         // ✔ Better logic:
//         // SOFT DELETE OR BLOCK DELETE IF ACTIVE
//         internRepository.delete(intern);

//     }

//     private InternDto mapToDto(Intern intern) {
//         InternDto dto = modelMapper.map(intern, InternDto.class);

//         // manually map fk
//         // if (intern.getUser() != null) {
//         // dto.setUserId(intern.getUser().getId());
//         // // dto.setUserName(intern.getUser().getName());
//         // // dto.setUserEmail(intern.getUser().getEmail());
//         // }
//         if (intern.getApplication() != null) {
//             dto.setApplicationId(intern.getApplication().getId());
//             dto.setApplicantName(intern.getApplication().getFirstName() + " " +
//                     intern.getApplication().getLastName());
//             dto.setApplicantEmail(intern.getApplication().getEmail());
//         }
//         // manually map reverse r/ship ids only
        
//         // using Method Reference
//         // dto.setEvaluationIds(
//         //         intern.getEvaluations() != null
//         //                 ? intern.getEvaluations()
//         //                         .stream()
//         //                         .map(Evaluation::getId)
//         //                         .toList()


//         //                 : List.of());

//         // using Lambda
//         if (intern.getActivityLogs() != null) {
//             dto.setActivityLogIds(
//                     intern.getActivityLogs()
//                             // .stream() → convert list to stream (process one by one)
//                             // .map(...) → transform each item
//                             // act.getId() → get ID of each activity log
//                             .stream()
//                             .map(act -> act.getId())

//                             // .toList() → convert stream to list
//                             .toList());
//         }
//         if (intern.getCertificate() != null) {
//             dto.setCertificateId(intern.getCertificate().getId());
//         }
//         if (intern.getPlacement() != null) {
//             dto.setPlacementId(intern.getPlacement().getId());
//         }

//         return dto;

//     }
// }

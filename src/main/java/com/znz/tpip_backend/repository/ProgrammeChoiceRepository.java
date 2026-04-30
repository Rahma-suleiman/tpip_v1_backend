package com.znz.tpip_backend.repository;

import com.znz.tpip_backend.model.ProgrammeChoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProgrammeChoiceRepository extends JpaRepository<ProgrammeChoice, Long> {

    // get all choices for an application (used in validation + fetch)
    List<ProgrammeChoice> findByApplicationId(Long applicationId);

    // optional (better for UI sorting)
    List<ProgrammeChoice> findByApplicationIdOrderByPreferenceRankAsc(Long applicationId);

    // used for stronger validation (optional optimization)
    boolean existsByApplicationIdAndPreferenceRank(Long applicationId, Integer preferenceRank);

    boolean existsByApplicationIdAndProgrammeId(Long applicationId, Long programmeId);
}

// HOW the 4 repo CONNECT (VERY IMPORTANT)

// Your flow now works like this:

// 🔹 Step 4 (Programme Choice)

// Service calls:

// ProgrammeChoiceRepository.findByApplicationId()
// → for validation (max 3, rank, duplicates)
// ProgrammeRepository.findById()
// → get selected programme
// EducationRepository.findByApplicantId()
// → check eligibility
// ApplicationRepository.findById()
// → link everything
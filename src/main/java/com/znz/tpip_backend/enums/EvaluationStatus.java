package com.znz.tpip_backend.enums;

import com.znz.tpip_backend.model.Evaluation;

public enum EvaluationStatus {
    PENDING,            // created but not finalized(Used for:MIDTERM Evaluation not final yet)
    PASSED,             // successful(Final or reassessment score ≥ 50)
    FAILED,             // failed final evaluation(Final decision = intern did not qualify (NO extension))
    REQUIRES_EXTENSION  // must extend internship(Intern failed BUT allowed to continue)
}

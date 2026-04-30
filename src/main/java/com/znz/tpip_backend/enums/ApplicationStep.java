package com.znz.tpip_backend.enums;

public enum ApplicationStep {

    PERSONAL_INFO(1),
    EDUCATION(2),
    WORK_EXPERIENCE(3),
    PROGRAMME_CHOICE(4),
    REFEREES(5),
    PAYMENT(6),
    REVIEW(7),
    SUBMISSION(8);

    private final int order;

    ApplicationStep(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }
}
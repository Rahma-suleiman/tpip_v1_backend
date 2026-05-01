package com.znz.tpip_backend.event;

import com.znz.tpip_backend.enums.ApplicationStep;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApplicationStepUpdatedEvent {

    private Long applicationId;
    private Long applicantId;
    private ApplicationStep step;
}
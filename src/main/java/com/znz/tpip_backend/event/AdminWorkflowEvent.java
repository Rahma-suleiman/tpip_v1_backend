package com.znz.tpip_backend.event;

import com.znz.tpip_backend.enums.AdminState;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminWorkflowEvent {

    private final Long applicationId;
    private final AdminState state;
}
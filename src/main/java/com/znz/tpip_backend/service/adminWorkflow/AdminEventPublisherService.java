package com.znz.tpip_backend.service.adminWorkflow;

import com.znz.tpip_backend.enums.AdminState;
import com.znz.tpip_backend.event.AdminWorkflowEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminEventPublisherService {

    private final ApplicationEventPublisher publisher;

    public void publish(Long applicationId, AdminState state) {

        publisher.publishEvent(
                new AdminWorkflowEvent(applicationId, state)
        );
    }
}
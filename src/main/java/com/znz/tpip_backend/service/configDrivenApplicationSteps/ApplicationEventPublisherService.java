package com.znz.tpip_backend.service.configDrivenApplicationSteps;

// import com.znz.tpip_backend.event.ApplicationStepUpdatedEvent;
import com.znz.tpip_backend.enums.ApplicationStep;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplicationEventPublisherService {

    private final ApplicationEventPublisher publisher;

    public void publish(Long applicationId, Long applicantId, ApplicationStep step) {

        publisher.publishEvent(
                new ApplicationStepUpdatedEvent(applicationId, applicantId, step)
        );
    }
}
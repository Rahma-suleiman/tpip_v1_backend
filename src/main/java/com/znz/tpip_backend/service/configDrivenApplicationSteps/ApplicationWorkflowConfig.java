package com.znz.tpip_backend.service.configDrivenApplicationSteps;

import com.znz.tpip_backend.enums.ApplicationStep;
import com.znz.tpip_backend.model.Application;
import com.znz.tpip_backend.service.ApplicationStepEvaluator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.EnumMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ApplicationWorkflowConfig {

    private final ApplicationStepEvaluator evaluator;

    @Getter
    private final Map<ApplicationStep, StepConfig> workflow = new EnumMap<>(ApplicationStep.class);

    @PostConstruct
    public void init() {

        register(ApplicationStep.PERSONAL_INFO,
                ApplicationStep.EDUCATION,
                evaluator::isPersonalInfoComplete);

        register(ApplicationStep.EDUCATION,
                ApplicationStep.WORK_EXPERIENCE,
                evaluator::isEducationComplete);

        register(ApplicationStep.WORK_EXPERIENCE,
                ApplicationStep.PROGRAMME_CHOICE,
                evaluator::isWorkExperienceComplete);

        register(ApplicationStep.PROGRAMME_CHOICE,
                ApplicationStep.REFEREES,
                evaluator::isProgrammeChoiceComplete);

        register(ApplicationStep.REFEREES,
                ApplicationStep.PAYMENT,
                evaluator::isRefereesComplete);

        register(ApplicationStep.PAYMENT,
                ApplicationStep.SUBMISSION,
                evaluator::isPaymentComplete);
    }

    // cleaner helper
    private void register(ApplicationStep current,
                          ApplicationStep next,
                          java.util.function.Predicate<Application> condition) {

        workflow.put(current, new StepConfig(next, condition));
    }

    public StepConfig getConfig(ApplicationStep step) {

        StepConfig config = workflow.get(step);

        if (config == null) {
            throw new IllegalStateException("No workflow config found for step: " + step);
        }

        return config;
    }
}
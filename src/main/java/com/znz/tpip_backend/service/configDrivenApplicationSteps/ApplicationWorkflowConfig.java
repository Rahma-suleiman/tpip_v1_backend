package com.znz.tpip_backend.service.configDrivenApplicationSteps;

import com.znz.tpip_backend.enums.ApplicationStep;
import com.znz.tpip_backend.model.Application;
import com.znz.tpip_backend.service.ApplicationStepEvaluator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

// import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Component
@RequiredArgsConstructor
public class ApplicationWorkflowConfig {

        private final ApplicationStepEvaluator evaluator;

        @Getter
        private final Map<ApplicationStep, StepConfig> workflow = new EnumMap<>(ApplicationStep.class);

        @PostConstruct
        public void init() {

                register(ApplicationStep.PERSONAL_INFO, ApplicationStep.EDUCATION, null, 1,
                                evaluator::isPersonalInfoComplete);

                register(ApplicationStep.EDUCATION, ApplicationStep.WORK_EXPERIENCE, ApplicationStep.PERSONAL_INFO, 2,
                                evaluator::isEducationComplete);

                register(ApplicationStep.WORK_EXPERIENCE, ApplicationStep.PROGRAMME_CHOICE, ApplicationStep.EDUCATION,
                                3, evaluator::isWorkExperienceComplete);

                register(ApplicationStep.PROGRAMME_CHOICE, ApplicationStep.REFEREES, ApplicationStep.WORK_EXPERIENCE, 4,
                                evaluator::isProgrammeChoiceComplete);

                register(ApplicationStep.REFEREES, ApplicationStep.PAYMENT, ApplicationStep.PROGRAMME_CHOICE, 5,
                                evaluator::isRefereesComplete);

                register(ApplicationStep.PAYMENT, ApplicationStep.REVIEW, ApplicationStep.REFEREES, 6,
                                evaluator::isPaymentComplete);

                register(ApplicationStep.REVIEW, ApplicationStep.SUBMISSION, ApplicationStep.PAYMENT, 7,
                                app -> true);

                register(ApplicationStep.SUBMISSION, null, ApplicationStep.REVIEW, 8,
                                app -> true);
        }

        private void register(
                        ApplicationStep step,
                        ApplicationStep next,
                        ApplicationStep prev,
                        int order,
                        Predicate<Application> condition) {
                workflow.put(step, new StepConfig(step, next, prev, order, condition));
        }
        // public StepConfig getConfig(ApplicationStep step) {
        // return workflow.get(step);
        // }

        public StepConfig getConfig(ApplicationStep step) {
                StepConfig config = workflow.get(step);

                if (config == null) {
                        throw new IllegalStateException("No workflow config found for step: " + step);
                }

                return config;
        }

        public List<StepConfig> getAllSteps() {
                return workflow.values()
                                .stream()
                                .sorted((a, b) -> Integer.compare(a.getOrder(), b.getOrder()))
                                .toList();
        }
}


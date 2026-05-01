package com.znz.tpip_backend.service;

import java.time.LocalDateTime;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.znz.tpip_backend.dto.RefereeSubmissionDTO;
import com.znz.tpip_backend.enums.RefereeStatus;
import com.znz.tpip_backend.model.Referee;
import com.znz.tpip_backend.model.RefereeSubmission;
import com.znz.tpip_backend.repository.RefereeRepository;
import com.znz.tpip_backend.repository.RefereeSubmissionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefereeSubmissionService {

    private final RefereeRepository refereeRepository;
    private final RefereeSubmissionRepository submissionRepository;
    private final ModelMapper modelMapper;
    private final ApplicationEventPublisherService eventPublisher;

    public RefereeSubmissionDTO submit(Long refereeId, RefereeSubmissionDTO dto) {

        Referee referee = refereeRepository.findById(refereeId)
                .orElseThrow(() -> new RuntimeException("Referee not found"));

        RefereeSubmission submission = modelMapper.map(dto, RefereeSubmission.class);
        submission.setReferee(referee);

        RefereeSubmission saved = submissionRepository.save(submission);

        referee.setStatus(RefereeStatus.SUBMITTED);
        referee.setSubmittedAt(LocalDateTime.now());
        refereeRepository.save(referee);

        // 🔥 EVENT TRIGGER
        eventPublisher.publish(
                referee.getApplication().getId(),
                referee.getApplication().getApplicant().getId(),
                referee.getApplication().getCurrentStep()
        );

        return modelMapper.map(saved, RefereeSubmissionDTO.class);
    }
}

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

    public RefereeSubmissionDTO submitByToken(String token, RefereeSubmissionDTO dto) {

        Referee referee = refereeRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or missing token"));

        // check expiry
        if (referee.getTokenExpiry() == null ||
                referee.getTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }

        // prevent double submission
        if (referee.getStatus() == RefereeStatus.SUBMITTED) {
            throw new RuntimeException("Referee already submitted response");
        }

        // map submission
        RefereeSubmission submission = modelMapper.map(dto, RefereeSubmission.class);
        submission.setReferee(referee);

        RefereeSubmission saved = submissionRepository.save(submission);

        // update referee status
        referee.setStatus(RefereeStatus.SUBMITTED);
        referee.setSubmittedAt(LocalDateTime.now());
        refereeRepository.save(referee);

        // trigger workflow event
        eventPublisher.publish(
                referee.getApplication().getId(),
                referee.getApplication().getApplicant().getId(),
                referee.getApplication().getCurrentStep()
        );

        return modelMapper.map(saved, RefereeSubmissionDTO.class);
    }
}
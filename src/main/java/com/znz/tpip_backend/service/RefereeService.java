package com.znz.tpip_backend.service;

import com.znz.tpip_backend.dto.RefereeDTO;
// import com.znz.tpip_backend.enums.ApplicationStep;
import com.znz.tpip_backend.enums.RefereeStatus;
import com.znz.tpip_backend.model.Application;
import com.znz.tpip_backend.model.Referee;
import com.znz.tpip_backend.repository.ApplicationRepository;
import com.znz.tpip_backend.repository.RefereeRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class RefereeService {

    private final RefereeRepository refereeRepository;
    private final ApplicationRepository applicationRepository;
    private final ModelMapper modelMapper;
    private final ApplicationEventPublisherService eventPublisher;

    public RefereeDTO create(RefereeDTO dto) {

        Application app = applicationRepository.findById(dto.getApplicationId())
                .orElseThrow(() -> new RuntimeException("Application not found"));

        Referee referee = modelMapper.map(dto, Referee.class);
        referee.setApplication(app);
        referee.setStatus(RefereeStatus.PENDING);

        Referee saved = refereeRepository.save(referee);

        // 🔥 EVENT TRIGGER
        eventPublisher.publish(
                app.getId(),
                app.getApplicant().getId(),
                app.getCurrentStep()
        );

        return modelMapper.map(saved, RefereeDTO.class);
    }

    public List<RefereeDTO> getByApplication(Long applicationId) {

        return refereeRepository.findByApplicationId(applicationId)
                .stream()
                .map(r -> modelMapper.map(r, RefereeDTO.class))
                .toList();
    }
}
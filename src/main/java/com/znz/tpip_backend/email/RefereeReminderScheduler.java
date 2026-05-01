package com.znz.tpip_backend.email;

import com.znz.tpip_backend.enums.RefereeStatus;
import com.znz.tpip_backend.model.Referee;
import com.znz.tpip_backend.repository.RefereeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RefereeReminderScheduler {

    private final RefereeRepository refereeRepository;
    private final RefereeEmailService emailService;

    // runs every day at 8 AM
    @Scheduled(cron = "0 0 8 * * *")
    public void sendReminders() {

        List<Referee> pending = refereeRepository
                .findByStatus(RefereeStatus.PENDING);

        for (Referee r : pending) {

            if (r.getTokenExpiry() != null &&
                    r.getTokenExpiry().isAfter(LocalDateTime.now())) {

                emailService.sendReminder(r);
            }
        }
    }
}
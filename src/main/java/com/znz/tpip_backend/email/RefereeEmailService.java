package com.znz.tpip_backend.email;

import com.znz.tpip_backend.model.Referee;
import com.znz.tpip_backend.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefereeEmailService {

    private final EmailService emailService;
    private final EmailTemplateBuilder templateBuilder;
    private final TokenService tokenService;

    private static final String FRONTEND_URL =
            "https://tpip-system.zanzibar/referee";

    public void sendInvitation(Referee referee) {

        // generate token only
        String token = tokenService.generateToken();

        referee.setToken(token);
        referee.setTokenExpiry(tokenService.expiryTime());

        String link = FRONTEND_URL + "?token=" + token;

        String body = templateBuilder.buildRefereeInvitation(
                referee.getFullName(),
                link
        );

        emailService.sendEmail(
                referee.getEmail(),
                "Referee Invitation - TPIP System",
                body
        );
    }

    public void sendReminder(Referee referee) {

        String link = FRONTEND_URL + "?token=" + referee.getToken();

        String body = templateBuilder.buildReminder(
                referee.getFullName(),
                link
        );

        emailService.sendEmail(
                referee.getEmail(),
                "Reminder: Referee Submission Pending",
                body
        );
    }
}
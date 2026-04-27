package com.znz.tpip_backend.auth;

import java.time.LocalDate;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.znz.tpip_backend.auth.dto.LoginRequest;
import com.znz.tpip_backend.auth.dto.LoginResponse;
import com.znz.tpip_backend.auth.dto.RegisterRequest;
import com.znz.tpip_backend.enums.UserRole;
import com.znz.tpip_backend.model.Applicant;
import com.znz.tpip_backend.model.User;
import com.znz.tpip_backend.repository.ApplicantRepository;
import com.znz.tpip_backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ApplicantRepository applicantRepository;
    private final PasswordEncoder passwordEncoder;

    // ================= REGISTER =================
    public String register(RegisterRequest request) {

        // 1. Email check
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // 2. Validate exam index
        if (request.getIndexNumber() == null || request.getIndexNumber().isBlank()) {
            throw new RuntimeException("Index number (Form IV/VI) is required");
        }

        // 3. Prevent duplicate exam index
        if (applicantRepository.existsByIndexNumber(request.getIndexNumber())) {
            throw new RuntimeException("Index number already exists");
        }

        // 4. Create User
        User user = new User();
        user.setEmail(request.getEmail());
        user.setMobileNumber(request.getMobileNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.APPLICANT);
        user.setIsVerified(false);

        // 5. Create Applicant
        Applicant applicant = new Applicant();
        applicant.setUser(user);
        applicant.setIndexNumber(request.getIndexNumber());

        // 6. Generate system index
        String applicationIndex = generateApplicationIndex();
        applicant.setApplicationIndexNumber(applicationIndex);

        user.setApplicant(applicant);

        userRepository.save(user);

        return "Account created successfully. Your Application Index is: " + applicationIndex;
    }

    // ================= LOGIN =================
    public LoginResponse login(LoginRequest request) {

        // 1. Find user
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Check password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // 3. Check verification
        if (!user.getIsVerified()) {
            throw new RuntimeException("Account not verified");
        }
        Applicant applicant = user.getApplicant();

        LoginResponse response = new LoginResponse();
        response.setUserId(user.getId());
        response.setApplicantId(applicant.getId());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().name());
        response.setApplicationIndexNumber(applicant.getApplicationIndexNumber());

        return response;

    }

    // ================= INDEX GENERATOR =================
    private String generateApplicationIndex() {

        String year = String.valueOf(LocalDate.now().getYear());

        long count = applicantRepository.count() + 1;

        return String.format("INT/%s/%05d", year, count);
    }
}
package com.znz.tpip_backend.auth;

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

        // 5. Create Applicant
        Applicant applicant = new Applicant();
        applicant.setUser(user);
        applicant.setIndexNumber(request.getIndexNumber());

        user.setApplicant(applicant);

        userRepository.save(user);

        return "Account created successfully";
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

        Applicant applicant = user.getApplicant();

        LoginResponse response = new LoginResponse();
        response.setUserId(user.getId());
        response.setApplicantId(applicant.getId());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().name());

        return response;
    }

}
// REGISTER
//   {
//     "firstName": "Amina",
//     "middleName": "Salum",
//     "lastName": "Hassan",
//     "email": "amina@gmail.com",
//     "mobileNumber": "0712345678",
//     "password": "Amina@123",
//     "indexNumber": "S1234/001/2020"
//   }
//   {
//     "firstName": "Mohamed",
//     "middleName": "Ali",
//     "lastName": "Juma",
//     "email": "mohamed@gmail.com",
//     "mobileNumber": "0756789123",
//     "password": "Moha@456",
//     "indexNumber": "S5678/002/2019"
//   }
//   {
//     "firstName": "Fatma",
//     "middleName": "Omar",
//     "lastName": "Said",
//     "email": "fatma@gmail.com",
//     "mobileNumber": "0789456123",
//     "password": "Fatma@789",
//     "indexNumber": "S9101/003/2021"
//   }
// LOGIN 
//   {
//     "email": "amina@gmail.com",
//     "password": "Amina@123"
//   }
//   {
//     "email": "mohamed@gmail.com",
//     "password": "Moha@456"
//   }
//   {
//     "email": "fatma@gmail.com",
//     "password": "Fatma@789"
//   }

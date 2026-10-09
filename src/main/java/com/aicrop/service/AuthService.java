package com.aicrop.service;

import com.aicrop.dto.*;
import com.aicrop.model.User;
import com.aicrop.repository.UserRepository;
import com.aicrop.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private AuthenticationManager authenticationManager;

    public AuthResponse login(AuthRequest request) {
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(user.getUsername());
        return AuthResponse.builder()
            .token(token)
            .tokenType("Bearer")
            .userId(user.getId())
            .username(user.getUsername())
            .fullName(user.getFullName())
            .role(user.getRole().name())
            .email(user.getEmail())
            .build();
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        String finalEmail = (request.getEmail() != null && !request.getEmail().trim().isEmpty())
            ? request.getEmail().trim()
            : request.getUsername().toLowerCase().replaceAll("[^a-z0-9]", "") + "@farmer.krishimitra.in";

        if (userRepository.existsByEmail(finalEmail)) {
            finalEmail = request.getUsername().toLowerCase().replaceAll("[^a-z0-9]", "") + "_" + System.currentTimeMillis() + "@farmer.krishimitra.in";
        }

        User user = User.builder()
            .username(request.getUsername())
            .email(finalEmail)
            .password(passwordEncoder.encode(request.getPassword()))
            .fullName(request.getFullName())
            .phone(request.getPhone())
            .village(request.getVillage())
            .district(request.getDistrict())
            .state(request.getState())
            .landAreaAcres(request.getLandAreaAcres())
            .role(User.Role.FARMER)
            .enabled(true)
            .build();

        userRepository.save(user);
        String token = jwtUtil.generateToken(user.getUsername());

        return AuthResponse.builder()
            .token(token)
            .tokenType("Bearer")
            .userId(user.getId())
            .username(user.getUsername())
            .fullName(user.getFullName())
            .role(user.getRole().name())
            .email(user.getEmail())
            .build();
    }
}

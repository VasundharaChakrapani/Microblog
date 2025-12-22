package com.example.microblog.controller;



import com.example.microblog.config.JwtUtil;
import com.example.microblog.dto.UserSignupRequest;
import com.example.microblog.entity.*;
import com.example.microblog.repository.UserRepository;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 🔹 Register new user
   @PostMapping("/register")
public ResponseEntity<?> register(@RequestBody UserSignupRequest userRequest) {
    if (userRepository.findByUsername(userRequest.getUsername()).isPresent()) {
        return ResponseEntity.badRequest().body(Map.of("message", "Username already taken!"));
    }

    if (userRequest.getEmail() == null || userRequest.getEmail().isEmpty()) {
        return ResponseEntity.badRequest().body(Map.of("message", "Email is required!"));
    }

    // Create a new entity
    User user = new User();
    user.setUsername(userRequest.getUsername());
    user.setEmail(userRequest.getEmail());
    user.setPassword(passwordEncoder.encode(userRequest.getPassword()));

    userRepository.save(user);

    return ResponseEntity.ok(Map.of("message", "User registered successfully!"));
}

    // 🔹 Login and get JWT
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
        );

        String token = jwtUtil.generateToken(authentication.getName());

        return ResponseEntity.ok(Map.of("token", token));
    }
}

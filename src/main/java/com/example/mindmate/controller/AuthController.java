package com.example.mindmate.controller;

import com.example.mindmate.model.User;
import com.example.mindmate.repository.UserRepository;
import com.example.mindmate.service.EmailService;
import com.example.mindmate.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final OtpService otpService;

    @Autowired
    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          EmailService emailService,
                          OtpService otpService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.otpService = otpService;
    }

    // REGISTER
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody User user) {
        Map<String, String> resp = new HashMap<>();
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            resp.put("message", "Email already registered!");
            return ResponseEntity.status(400).body(resp);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        resp.put("message", "User registered successfully!");
        return ResponseEntity.ok(resp);
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");
        Map<String, String> resp = new HashMap<>();

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            resp.put("message", "User not found!");
            return ResponseEntity.status(404).body(resp);
        }
        User user = optionalUser.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            resp.put("message", "Invalid password!");
            return ResponseEntity.status(401).body(resp);
        }
        resp.put("message", "Login successful!");
        resp.put("username", user.getUsername());
        resp.put("email", user.getEmail());
        return ResponseEntity.ok(resp);
    }

    // SEND OTP
    @PostMapping("/send-otp")
    public ResponseEntity<Map<String, String>> sendOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        Map<String, String> resp = new HashMap<>();
        try {
            Optional<User> optionalUser = userRepository.findByEmail(email);
            if (optionalUser.isEmpty()) {
                resp.put("message", "User not found!");
                return ResponseEntity.status(404).body(resp);
            }

            String otp = String.valueOf((int)(Math.random()*900000)+100000);
            otpService.storeOtp(email, otp);
            emailService.sendOtpEmail(email, otp);

            resp.put("message", "OTP sent successfully!");
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("message", "Failed to send OTP. Please try again.");
            return ResponseEntity.status(500).body(resp);
        }
    }

    // VERIFY OTP
    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, String>> verifyOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        Map<String, String> resp = new HashMap<>();
        boolean ok = otpService.verifyOtp(email, otp);
        if (ok) {
            resp.put("message", "OTP verified successfully!");
            return ResponseEntity.ok(resp);
        } else {
            resp.put("message", "Invalid or expired OTP!");
            return ResponseEntity.status(400).body(resp);
        }
    }

    // RESET PASSWORD
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String newPassword = request.get("newPassword");
        Map<String, String> resp = new HashMap<>();
        try {
            Optional<User> optionalUser = userRepository.findByEmail(email);
            if (optionalUser.isEmpty()) {
                resp.put("message", "User not found!");
                return ResponseEntity.status(404).body(resp);
            }
            User user = optionalUser.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            resp.put("message", "Password reset successfully!");
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.put("message", "Error resetting password. Please try again.");
            return ResponseEntity.status(500).body(resp);
        }
    }
}

package com.example.mindmate.service;

public interface OtpService {
    void storeOtp(String email, String otp);
    boolean verifyOtp(String email, String otp);
    void removeOtp(String email);
}

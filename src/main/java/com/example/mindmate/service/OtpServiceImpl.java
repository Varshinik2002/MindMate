package com.example.mindmate.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpServiceImpl implements OtpService {

    private static class OtpData {
        final String otp;
        final LocalDateTime expiryTime;
        OtpData(String otp, LocalDateTime expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
        }
    }

    private final Map<String, OtpData> otpStore = new ConcurrentHashMap<>();

    @Override
    public void storeOtp(String email, String otp) {
        // valid for 10 minutes
        otpStore.put(email, new OtpData(otp, LocalDateTime.now().plusMinutes(10)));
    }

    @Override
    public boolean verifyOtp(String email, String otp) {
        OtpData data = otpStore.get(email);
        if (data == null) return false;
        if (data.expiryTime.isBefore(LocalDateTime.now())) {
            otpStore.remove(email);
            return false;
        }
        boolean ok = data.otp.equals(otp);
        if (ok) otpStore.remove(email);
        return ok;
    }

    @Override
    public void removeOtp(String email) {
        otpStore.remove(email);
    }
}

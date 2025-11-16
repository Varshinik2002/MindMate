package com.example.mindmate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Your MindMate OTP");
            message.setText(
                "Hello,\n\n" +
                "Your OTP for password reset is: " + otp + "\n" +
                "This OTP is valid for 10 minutes.\n\n" +
                "Regards,\nMindMate Team"
            );
            mailSender.send(message);
            System.out.println("✅ OTP email sent to: " + toEmail);
        } catch (Exception e) {
            // Log error (not exposing stack in production)
            System.err.println("❌ Error sending OTP to " + toEmail + " : " + e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }
}

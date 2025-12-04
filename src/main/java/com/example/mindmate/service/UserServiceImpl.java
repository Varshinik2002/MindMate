package com.example.mindmate.service;

import com.example.mindmate.model.User;
import com.example.mindmate.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public String registerUser(User user) {
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            return "User already exists!";
        }
        userRepository.save(user);
        return "Registration successful!";
    }

    @Override
    public String loginUser(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return "Invalid email or password!";
        }

        User user = userOpt.get();

        if (!user.getPassword().equals(password)) {   // ❗ simple check (no encryption)
            return "Invalid email or password!";
        }

        return "Login successful!";
    }

    @Override
    public void updatePassword(String email, String newPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setPassword(newPassword);
            userRepository.save(user);
        }
    }
}

package com.example.mindmate.service;

import com.example.mindmate.model.User;

public interface UserService {

    String registerUser(User user);

    String loginUser(String email, String password);

    void updatePassword(String email, String newPassword);
}


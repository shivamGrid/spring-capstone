package com.storeapp.cart.service;

import com.storeapp.cart.dto.*;
import com.storeapp.cart.model.User;
import com.storeapp.cart.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public void registerUser(UserRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new IllegalArgumentException("User already exists");
        }
        String hashedPassword = BCrypt.hashpw(userRequest.getPassword(), BCrypt.gensalt());
        User user = new User(userRequest.getEmail(), hashedPassword);
        userRepository.save(user);
    }

    public User loginUser(UserRequest userRequest) {
        User user = userRepository.findByEmail(userRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        if (!BCrypt.checkpw(userRequest.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        return user;
    }

    public Long getUserIdByEmail(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Invalid UserEmail"));
        return user.getId();
    }
}

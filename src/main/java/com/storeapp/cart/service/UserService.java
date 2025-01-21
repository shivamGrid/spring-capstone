package com.storeapp.cart.service;

import com.storeapp.cart.dto.*;
import com.storeapp.cart.exception.UnauthorizedException;
import com.storeapp.cart.model.User;
import com.storeapp.cart.repository.UserRepository;
import com.storeapp.cart.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCrypt;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public void registerUser(UserRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new UnauthorizedException(Constants.USER_ALREADY_EXISTS);
        }
        String hashedPassword = BCrypt.hashpw(userRequest.getPassword(), BCrypt.gensalt());
        User user = new User(userRequest.getEmail(), hashedPassword);
        userRepository.save(user);
    }

    public User loginUser(UserRequest userRequest) {
        User user = userRepository.findByEmail(userRequest.getEmail())
                .orElseThrow(() -> new UnauthorizedException(Constants.INVALID_USER_CREDENTIALS));
        if (!BCrypt.checkpw(userRequest.getPassword(), user.getPassword())) {
            throw new UnauthorizedException(Constants.INVALID_USER_CREDENTIALS);
        }
        return user;
    }

    public Long getUserIdByEmail(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UnauthorizedException(Constants.INVALID_EMAIL));
        return user.getId();
    }
}

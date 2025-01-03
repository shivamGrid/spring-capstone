package com.storeapp.cart.controller;

import com.storeapp.cart.dto.*;
import com.storeapp.cart.model.User;
import com.storeapp.cart.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserRequest userRequest) {
        try {
            userService.registerUser(userRequest);
            return ResponseEntity.status(201).body("User registered successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }


    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody UserRequest userRequest, HttpSession session) {
        try {
            User user = userService.loginUser(userRequest);
            String sessionId = UUID.randomUUID().toString();
            session.setAttribute("userId", user.getId());
            return ResponseEntity.ok(Map.of("sessionId", sessionId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }
}

package com.storeapp.cart.controller;

import com.storeapp.cart.service.OrderService;
import com.storeapp.cart.service.SessionService;
import com.storeapp.cart.util.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/checkout")
@Tag(name = "Checkout Controller", description = "APIs for managing the checkout process")
public class CheckoutController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private SessionService sessionService;

    @Operation(summary = "Checkout", description = "Processes for placing an order for a user")
    @PostMapping("/checkout")
    public ResponseEntity<String> checkout(HttpSession session) {
        Long userId = sessionService.getUserIdFromSession(session);
        orderService.processCheckout(userId);
        return ResponseEntity.ok(Constants.ORDER_PLACED_SUCCESS);
    }
}

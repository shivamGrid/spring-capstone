package com.storeapp.cart.controller;

import com.storeapp.cart.dto.CartItemResponse;
import com.storeapp.cart.model.Product;
import com.storeapp.cart.repository.ProductRepository;
import com.storeapp.cart.service.CartService;
import com.storeapp.cart.service.OrderService;
import com.storeapp.cart.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {
    @Autowired
    private CartService cartService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<String> checkout(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        List<CartItemResponse> cartItems = cartService.viewCart(userId);
        if (cartItems.isEmpty()) {
            return ResponseEntity.status(400).body("Cart is empty");
        }

        for (CartItemResponse item : cartItems) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + item.getProductId()));

            if (product.getAvailable() < item.getQuantity()) {
                return ResponseEntity.status(400).body("Insufficient stock for product: " + product.getTitle());
            }

            if (product.getPrice() != item.getPricePerUnit()) {
                return ResponseEntity.status(400).body("Price mismatch for product: " + product.getTitle());
            }
        }

        cartItems.forEach(item -> {
            Product product = productRepository.findById(item.getProductId()).orElseThrow();
            product.setAvailable(product.getAvailable() - item.getQuantity());
            productRepository.save(product);
        });

        orderService.createOrder(userId, cartItems);
        cartService.clearCart(userId);

        return ResponseEntity.ok("Order placed successfully");
    }

}

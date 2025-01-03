package com.storeapp.cart.controller;

import com.storeapp.cart.dto.*;
import com.storeapp.cart.service.CartService;
import com.storeapp.cart.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    @Autowired
    UserService userService;
    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<String> addToCart(HttpSession session, @RequestBody CartItemRequest request) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        cartService.addToCart(userId, request);
        return ResponseEntity.ok("Item added to cart");
    }

    @GetMapping
    public ResponseEntity<List<CartItemResponse>> viewCart(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(null);
        }
        return ResponseEntity.ok(cartService.viewCart(userId));
    }

    @PutMapping("/modify")
    public ResponseEntity<String> modifyCartItem(HttpSession session, @RequestBody CartItemModifyRequest request) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        cartService.modifyCartItem(userId, request);
        return ResponseEntity.ok("Cart item updated");
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> removeCartItem(HttpSession session, @RequestParam Long itemId) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        cartService.removeCartItem(userId, itemId);
        return ResponseEntity.ok("Item removed from cart");
    }

    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        cartService.clearCart(userId);
        return ResponseEntity.ok("Cart cleared successfully");
    }

}

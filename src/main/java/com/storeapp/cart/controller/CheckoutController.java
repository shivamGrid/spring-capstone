package com.storeapp.cart.controller;

import com.storeapp.cart.dto.CartItemResponse;
import com.storeapp.cart.model.Product;
import com.storeapp.cart.repository.ProductRepository;
import com.storeapp.cart.service.CartService;
import com.storeapp.cart.service.OrderService;
import com.storeapp.cart.service.UserService;
import com.storeapp.cart.util.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/checkout")
@Tag(name = "Checkout Controller", description = "APIs for managing the checkout process")
public class CheckoutController {
    @Autowired
    private CartService cartService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;

    @Operation(summary = "Checkout", description = "Processes for placing order of a user")
    @PostMapping
    public ResponseEntity<String> checkout(HttpSession session) {
        Long userId = (Long) session.getAttribute(Constants.USER_ID);
        if (userId == null) {
            return ResponseEntity.status(401).body(Constants.UNAUTHORIZED);
        }
        List<CartItemResponse> cartItems = cartService.viewCart(userId);
        if (cartItems.isEmpty()) {
            return ResponseEntity.status(400).body(Constants.CART_NOT_FOUND);
        }

        for (CartItemResponse item : cartItems) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException(Constants.PRODUCT_NOT_FOUND + item.getProductId()));

            if (product.getAvailable() < item.getQuantity()) {
                return ResponseEntity.status(400).body(Constants.INSUFFICIENT_STOCK + product.getTitle());
            }

            if (product.getPrice() != item.getPricePerUnit()) {
                return ResponseEntity.status(400).body(Constants.PRICE_MISMATCH + product.getTitle());
            }
        }

        cartItems.forEach(item -> {
            Product product = productRepository.findById(item.getProductId()).orElseThrow();
            product.setAvailable(product.getAvailable() - item.getQuantity());
            productRepository.save(product);
        });

        orderService.createOrder(userId, cartItems);
        cartService.clearCart(userId);

        return ResponseEntity.ok(Constants.ORDER_PLACED_SUCCESS);
    }

}

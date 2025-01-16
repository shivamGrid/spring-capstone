package com.storeapp.cart.controller;

import com.storeapp.cart.dto.*;
import com.storeapp.cart.service.CartService;
import com.storeapp.cart.service.UserService;
import com.storeapp.cart.util.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@Tag(name = "Cart Controller", description = "APIs for managing the shopping cart")
public class CartController {
    @Autowired
    UserService userService;
    @Autowired
    private CartService cartService;

    @Operation(summary = "Add item to cart", description = "Adds an item to the user's cart")
    @PostMapping("/add")
    public ResponseEntity<String> addToCart(HttpSession session, @RequestBody CartItemRequest request) {
        Long userId = (Long) session.getAttribute(Constants.USER_ID);
        if (userId == null) {
            return ResponseEntity.status(401).body(Constants.UNAUTHORIZED);
        }
        cartService.addToCart(userId, request);
        return ResponseEntity.ok(Constants.ITEM_ADDED);
    }

    @Operation(summary = "View cart", description = "Returns the list of items in the user's cart")
    @GetMapping
    public ResponseEntity<List<CartItemResponse>> viewCart(HttpSession session) {
        Long userId = (Long) session.getAttribute(Constants.USER_ID);
        if (userId == null) {
            return ResponseEntity.status(401).body(null);
        }
        return ResponseEntity.ok(cartService.viewCart(userId));
    }

    @Operation(summary = "Modify cart item", description = "Modifies the quantity of an item in the cart")
    @PutMapping("/modify")
    public ResponseEntity<String> modifyCartItem(HttpSession session, @RequestBody CartItemModifyRequest request) {
        Long userId = (Long) session.getAttribute(Constants.USER_ID);
        if (userId == null) {
            return ResponseEntity.status(401).body(Constants.UNAUTHORIZED);
        }
        cartService.modifyCartItem(userId, request);
        return ResponseEntity.ok(Constants.CART_UPDATED);
    }

    @Operation(summary = "Remove item from cart", description = "Removes an item from the user's cart")
    @DeleteMapping("/remove")
    public ResponseEntity<String> removeCartItem(HttpSession session, @RequestParam Long itemId) {
        Long userId = (Long) session.getAttribute(Constants.USER_ID);
        if (userId == null) {
            return ResponseEntity.status(401).body(Constants.UNAUTHORIZED);
        }
        cartService.removeCartItem(userId, itemId);
        return ResponseEntity.ok(Constants.CART_ITEM_REMOVED);
    }

    @Operation(summary = "Clear cart", description = "Clears all items from the user's cart")
    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart(HttpSession session) {
        Long userId = (Long) session.getAttribute(Constants.USER_ID);
        if (userId == null) {
            return ResponseEntity.status(401).body(Constants.UNAUTHORIZED);
        }
        cartService.clearCart(userId);
        return ResponseEntity.ok(Constants.CART_CLEARED);
    }

}

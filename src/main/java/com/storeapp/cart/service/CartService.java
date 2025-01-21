package com.storeapp.cart.service;

import com.storeapp.cart.dto.*;
import com.storeapp.cart.exception.BadRequestException;
import com.storeapp.cart.exception.InsufficientStockException;
import com.storeapp.cart.exception.ResourceNotFoundException;
import com.storeapp.cart.model.*;
import com.storeapp.cart.repository.CartRepository;
import com.storeapp.cart.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;


    public void addToCart(Long userId, CartItemRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (request.getQuantity() <= 0) {
            throw new BadRequestException("Quantity must be greater than zero");
        }

        validateStockAvailability(product, request.getQuantity());

        Cart cart = cartRepository.findByUserId(userId).orElse(new Cart(userId));
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + request.getQuantity();
            validateStockAvailability(product, newQuantity);
            item.setQuantity(newQuantity);
            System.out.println("Updated cart item: " + item.getProduct().getTitle() + " | Quantity: " + item.getQuantity());
        } else {
            CartItem newItem = new CartItem(cart, product, request.getQuantity());
            cart.getItems().add(newItem);
            System.out.println("Added new cart item: " + product.getTitle() + " | Quantity: " + request.getQuantity());
        }

        cartRepository.save(cart);
    }

    public List<CartItemResponse> viewCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cart is empty"));

        return cart.getItems().stream()
                .map(item -> new CartItemResponse(
                        item.getProduct().getId(),
                        item.getProduct().getTitle(),
                        item.getQuantity(),
                        item.getProduct().getPrice(),
                        item.getProduct().getPrice() * item.getQuantity()
                ))
                .collect(Collectors.toList());
    }

    public void modifyCartItem(Long userId, CartItemModifyRequest request) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cart is empty"));

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(request.getProductId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item not found in cart"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (request.getQuantity() > product.getAvailable()) {
            throw new IllegalArgumentException("Insufficient stock available");
        }
        item.setQuantity(request.getQuantity());
        cartRepository.save(cart);
    }

    public void removeCartItem(Long userId, Long productId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cart is empty"));

        cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));
        cartRepository.save(cart);
    }

    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cart is empty"));

        cart.getItems().clear();
        cartRepository.save(cart);
    }

    private void validateStockAvailability(Product product, int requestedQuantity) {
        if (product.getAvailable() < requestedQuantity) {
            throw new InsufficientStockException("Insufficient stock available");
        }
    }
}

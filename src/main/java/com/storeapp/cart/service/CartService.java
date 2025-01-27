package com.storeapp.cart.service;

import com.storeapp.cart.dto.*;
import com.storeapp.cart.exception.BadRequestException;
import com.storeapp.cart.exception.ResourceNotFoundException;
import com.storeapp.cart.model.*;
import com.storeapp.cart.repository.CartRepository;
import com.storeapp.cart.util.Constants;
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
    private ProductService productService;

    public void addToCart(Long userId, CartItemRequest request) {
        Product product = productService.getProductById(request.getProductId());

        if (request.getQuantity() <= 0) {
            throw new BadRequestException(Constants.QUANTITY_LESS_THAN_ZERO);
        }

        productService.validateStockAvailability(request.getProductId(), request.getQuantity());

        Cart cart = cartRepository.findByUserId(userId).orElse(new Cart(userId));
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + request.getQuantity();
            productService.validateStockAvailability(product.getId(), newQuantity);
            item.setQuantity(newQuantity);
        } else {
            CartItem newItem = new CartItem(cart, product, request.getQuantity());
            cart.getItems().add(newItem);
        }

        cartRepository.save(cart);
    }

    public List<CartItemResponse> viewCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.CART_NOT_FOUND));

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
                .orElseThrow(() -> new ResourceNotFoundException(Constants.CART_NOT_FOUND));

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(request.getProductId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(Constants.ITEM_NOT_FOUND));

        productService.validateStockAvailability(request.getProductId(), request.getQuantity());
        item.setQuantity(request.getQuantity());
        cartRepository.save(cart);
    }

    public void removeCartItem(Long userId, Long productId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.CART_NOT_FOUND));

        cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));
        cartRepository.save(cart);
    }

    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.CART_NOT_FOUND));

        cart.getItems().clear();
        cartRepository.save(cart);
    }
}

package com.storeapp.cart.service;

import com.storeapp.cart.dto.CartItemResponse;
import com.storeapp.cart.exception.BadRequestException;
import com.storeapp.cart.model.*;
import com.storeapp.cart.repository.OrderRepository;
import com.storeapp.cart.util.Constants;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private CartService cartService;

    public void processCheckout(Long userId) {
        List<CartItemResponse> cartItems = cartService.viewCart(userId);
        if (cartItems.isEmpty()) {
            throw new BadRequestException(Constants.CART_NOT_FOUND);
        }

        for (CartItemResponse item : cartItems) {
            Product product = productService.getProductById(item.getProductId());

            if (product.getPrice() != item.getPricePerUnit()) {
                throw new BadRequestException(Constants.PRICE_MISMATCH + product.getTitle());
            }

            if (product.getAvailable() < item.getQuantity()) {
                throw new BadRequestException(Constants.INSUFFICIENT_STOCK + product.getTitle());
            }
        }
        cartItems.forEach(item -> productService.updateProductStock(item.getProductId(), item.getQuantity()));

        createOrder(userId, cartItems);
        cartService.clearCart(userId);
    }

    public void createOrder(Long userId, List<CartItemResponse> cartItems) {
        Order order = Order.builder()
        .userId(userId)
        .orderDate(new Date())
        .build();

        List<OrderItem> orderItems = cartItems.stream()
                .map(item -> new OrderItem(item.getProductId(), item.getQuantity(), item.getPricePerUnit()))
                .collect(Collectors.toList());

        order.setItems(orderItems);
        orderRepository.save(order);
    }
}

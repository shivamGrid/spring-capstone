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
            throw new BadRequestException(Constants.EMPTY_CART);
        }

        for (CartItemResponse item : cartItems) {
            Product product = productService.getProductById(item.getProductId());
            if (product.getPrice() != item.getPricePerUnit()) {
                throw new BadRequestException(Constants.PRICE_MISMATCH + product.getTitle());
            }
            productService.validateStockAvailability(item.getProductId(), item.getQuantity());
        }

        cartItems.forEach(item -> productService.updateProductStock(item.getProductId(), item.getQuantity()));

        createOrder(userId, cartItems);
        cartService.clearCart(userId);
    }

    public void createOrder(Long userId, List<CartItemResponse> cartItems) {
        Order order = new Order();
        order.setUserId(userId);
        order.setOrderDate(new Date());

        List<OrderItem> orderItems = cartItems.stream()
                .map(item -> {
                    OrderItem orderItem = new OrderItem(item.getProductId(), item.getQuantity(), item.getPricePerUnit());
                    orderItem.setOrder(order); // Set the order reference
                    return orderItem;
                })
                .collect(Collectors.toList());

        order.setItems(orderItems);
        orderRepository.save(order);
    }
}

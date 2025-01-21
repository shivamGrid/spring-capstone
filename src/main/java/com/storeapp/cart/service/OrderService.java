package com.storeapp.cart.service;

import com.storeapp.cart.dto.CartItemResponse;
import com.storeapp.cart.model.*;
import com.storeapp.cart.repository.OrderRepository;
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

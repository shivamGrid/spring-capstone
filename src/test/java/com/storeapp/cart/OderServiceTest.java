package com.storeapp.cart;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.storeapp.cart.exception.BadRequestException;
import com.storeapp.cart.model.Order;
import com.storeapp.cart.model.OrderItem;
import com.storeapp.cart.model.Product;
import com.storeapp.cart.repository.OrderRepository;
import com.storeapp.cart.dto.CartItemRequest;
import com.storeapp.cart.dto.CartItemResponse;
import com.storeapp.cart.service.CartService;
import com.storeapp.cart.service.OrderService;
import com.storeapp.cart.service.ProductService;
import com.storeapp.cart.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductService productService;

    @Mock
    private CartService cartService;

    @InjectMocks
    private OrderService orderService;

    private Product product;
    private CartItemResponse cartItem;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        product = new Product();
        product.setId(1L);
        product.setTitle("Laptop");
        product.setPrice(1000);
        product.setAvailable(5);

        cartItem = new CartItemResponse(1L, "Laptop", 2, 1000, 2000);
    }

    @Test
    void testProcessCheckout_Success() {
        List<CartItemResponse> cartItems = Arrays.asList(cartItem);

        when(cartService.viewCart(1L)).thenReturn(cartItems);
        when(productService.getProductById(1L)).thenReturn(product);

        orderService.processCheckout(1L);

        verify(productService, times(1)).updateProductStock(1L, 2);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(cartService, times(1)).clearCart(1L);
    }

    @Test
    void testProcessCheckout_InsufficientStock() {
        cartItem = new CartItemResponse(1L, "Laptop", 10, 1000, 10000); // Requesting more than available stock
        List<CartItemResponse> cartItems = Arrays.asList(cartItem);

        when(cartService.viewCart(1L)).thenReturn(cartItems);
        when(productService.getProductById(1L)).thenReturn(product);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> orderService.processCheckout(1L));

        assertTrue(exception.getMessage().contains(Constants.INSUFFICIENT_STOCK));
    }

    @Test
    void testCreateOrder_Success() {
        List<CartItemResponse> cartItems = Arrays.asList(cartItem);

        orderService.createOrder(1L, cartItems);

        verify(orderRepository, times(1)).save(any(Order.class));
    }
}


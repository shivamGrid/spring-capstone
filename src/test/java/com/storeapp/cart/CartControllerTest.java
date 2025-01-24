package com.storeapp.cart;

import com.storeapp.cart.controller.CartController;
import com.storeapp.cart.dto.CartItemModifyRequest;
import com.storeapp.cart.dto.CartItemRequest;
import com.storeapp.cart.dto.CartItemResponse;
import com.storeapp.cart.service.CartService;
import com.storeapp.cart.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class CartControllerTest {
    @Mock
    private UserService userService;

    @Mock
    private CartService cartService;

    @Mock
    private HttpSession session;

    @InjectMocks
    private CartController cartController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddToCart_unauthorized() {
        when(session.getAttribute("userId")).thenReturn(null);

        ResponseEntity<String> response = cartController.addToCart(session, new CartItemRequest());
        assertEquals("Unauthorized", response.getBody());
    }

    @Test
    void testAddToCart_success() {
        Long userId = 1L;
        when(session.getAttribute("userId")).thenReturn(userId);

        doNothing().when(cartService).addToCart(eq(userId), any(CartItemRequest.class));

        ResponseEntity<String> response = cartController.addToCart(session, new CartItemRequest());

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Item added to cart", response.getBody());
        verify(cartService, times(1)).addToCart(eq(userId), any(CartItemRequest.class));
    }

    @Test
    void testViewCart_unauthorized() {
        when(session.getAttribute("userId")).thenReturn(null);

        ResponseEntity<List<CartItemResponse>> response = cartController.viewCart(session);

        assertEquals(401, response.getStatusCodeValue());
        assertNull(response.getBody());
    }
    @Test
    void testModifyCartItem_success() {
        Long userId = 1L;
        when(session.getAttribute("userId")).thenReturn(userId);

        doNothing().when(cartService).modifyCartItem(eq(userId), any(CartItemModifyRequest.class));

        ResponseEntity<String> response = cartController.modifyCartItem(session, new CartItemModifyRequest());

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Cart item updated", response.getBody());
        verify(cartService, times(1)).modifyCartItem(eq(userId), any(CartItemModifyRequest.class));
    }
    @Test
    void testRemoveCartItem_unauthorized() {
        when(session.getAttribute("userId")).thenReturn(null);

        ResponseEntity<String> response = cartController.removeCartItem(session, 1L);

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Unauthorized", response.getBody());
    }

    @Test
    void testRemoveCartItem_success() {
        Long userId = 1L;
        when(session.getAttribute("userId")).thenReturn(userId);

        doNothing().when(cartService).removeCartItem(userId, 1L);

        ResponseEntity<String> response = cartController.removeCartItem(session, 1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Item removed from cart", response.getBody());
        verify(cartService, times(1)).removeCartItem(userId, 1L);
    }
    @Test
    void testClearCart_success() {
        Long userId = 1L;
        when(session.getAttribute("userId")).thenReturn(userId);

        doNothing().when(cartService).clearCart(userId);

        ResponseEntity<String> response = cartController.clearCart(session);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Cart cleared successfully", response.getBody());
        verify(cartService, times(1)).clearCart(userId);
    }
}

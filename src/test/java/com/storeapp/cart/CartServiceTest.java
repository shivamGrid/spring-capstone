package com.storeapp.cart;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.storeapp.cart.exception.BadRequestException;
import com.storeapp.cart.exception.ResourceNotFoundException;
import com.storeapp.cart.model.*;
import com.storeapp.cart.repository.CartRepository;
import com.storeapp.cart.dto.CartItemModifyRequest;
import com.storeapp.cart.dto.CartItemRequest;
import com.storeapp.cart.dto.CartItemResponse;
import com.storeapp.cart.service.CartService;
import com.storeapp.cart.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private CartService cartService;

    private Product product;
    private Cart cart;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        product = new Product();
        product.setId(1L);
        product.setTitle("Laptop");
        product.setPrice(1000);
        product.setAvailable(10);

        cart = new Cart(1L);
        cartItem = new CartItem(cart, product, 2);
        cart.getItems().add(cartItem);
    }

    @Test
    void testAddToCart_success() {
        CartItemRequest request = new CartItemRequest();
        request.setProductId(1L);
        request.setQuantity(2);

        when(productService.getProductById(1L)).thenReturn(product);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        cartService.addToCart(1L, request);

        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void testAddToCart_quantityLessThanZero() {
        CartItemRequest request = new CartItemRequest();
        request.setProductId(1L);
        request.setQuantity(0);
        assertThrows(BadRequestException.class, () -> cartService.addToCart(1L, request));
    }

    @Test
    void testViewCart_success() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        List<CartItemResponse> cartItems = cartService.viewCart(1L);
        assertEquals(1, cartItems.size());
        assertEquals("Laptop", cartItems.get(0).getProductName());
    }

    @Test
    void testViewCart_cartNotFound() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cartService.viewCart(1L));
    }

    @Test
    void testModifyCartItem_success() {
        CartItemModifyRequest request = new CartItemModifyRequest();
        request.setProductId(1L);
        request.setQuantity(5);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        cartService.modifyCartItem(1L, request);

        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void testModifyCartItem_cartNotFound() {
        CartItemModifyRequest request = new CartItemModifyRequest();
        request.setProductId(1L);
        request.setQuantity(5);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cartService.modifyCartItem(1L, request));
    }

    @Test
    void testRemoveCartItem_success() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        cartService.removeCartItem(1L, 1L);

        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void testRemoveCartItem_cartNotFound() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cartService.removeCartItem(1L, 1L));
    }

    @Test
    void testClearCart_success() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        cartService.clearCart(1L);

        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void testClearCart_cartNotFound() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> cartService.clearCart(1L));
    }
}

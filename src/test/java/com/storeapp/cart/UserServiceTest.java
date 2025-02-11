package com.storeapp.cart;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.storeapp.cart.exception.*;
import com.storeapp.cart.model.User;
import com.storeapp.cart.repository.UserRepository;
import com.storeapp.cart.dto.UserRequest;
import com.storeapp.cart.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.Optional;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser_success() {
        UserRequest request = new UserRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);

        userService.registerUser(request);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUser_userAlreadyExists() {
        UserRequest request = new UserRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> userService.registerUser(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testLoginUser_success() {
        String email = "test@example.com";
        String password = "password123";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        User user = new User(email, hashedPassword);
        UserRequest request = new UserRequest();
        request.setEmail(email);
        request.setPassword(password);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        User loggedInUser = userService.loginUser(request);
        assertNotNull(loggedInUser);
    }

    @Test
    void testLoginUser_invalidEmail() {
        UserRequest request = new UserRequest();
        request.setEmail("nonexistent@example.com");
        request.setPassword("password123");
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class, () -> userService.loginUser(request));
    }

    @Test
    void testLoginUser_invalidPassword() {
        String email = "test@example.com";
        String password = "password123";
        String wrongPassword = "wrongpassword";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        User user = new User(email, hashedPassword);
        UserRequest request = new UserRequest();
        request.setEmail(email);
        request.setPassword(wrongPassword);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        assertThrows(UnauthorizedException.class, () -> userService.loginUser(request));
    }

    @Test
    void testGetUserIdByEmail_success() {
        String email = "test@example.com";
        User user = new User(email, "hashedpassword");
        user.setId(1L);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        Long userId = userService.getUserIdByEmail(email);
        assertEquals(1L, userId);
    }

    @Test
    void testGetUserIdByEmail_notFound() {
        when(userRepository.findByEmail("nonexist@example.com")).thenReturn(Optional.empty());
         assertThrows(ResourceNotFoundException.class, () -> userService.getUserIdByEmail("nonexist@example.com"));
    }
}

package com.storeapp.cart.integrationTest;

import com.storeapp.cart.model.Product;
import com.storeapp.cart.model.User;
import com.storeapp.cart.repository.CartRepository;
import com.storeapp.cart.repository.OrderRepository;
import com.storeapp.cart.repository.ProductRepository;
import com.storeapp.cart.repository.UserRepository;
import com.storeapp.cart.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ApplicationIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        Product product = new Product();
        product.setTitle("Apple");
        product.setAvailable(10);
        product.setPrice(150);
        productRepository.save(product);

        User user = new User("shivam@example.com", BCrypt.hashpw("password", BCrypt.gensalt()));
        userRepository.save(user);
    }

    @Test
    void testUserRegistrationAndLogin() throws Exception {
        // Register a user
        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"newuser@example.com\",\"password\":\"password\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().string(Constants.USER_REGISTERED));

        // Login with the registered user
        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"newuser@example.com\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").isNotEmpty());
    }

    @Test
    void testAddToCart() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"shivam@example.com\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession();

        // Add an item to the cart
        mockMvc.perform(post("/api/cart/add")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":1,\"quantity\":2}")) // Ensure productId matches saved product
                .andExpect(status().isOk())
                .andExpect(content().string(Constants.ITEM_ADDED));
    }

    @Test
    void testViewCart() throws Exception {

        MvcResult loginResult = mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"shivam@example.com\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession();


        mockMvc.perform(post("/api/cart/add")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":1,\"quantity\":2}"))
                .andExpect(status().isOk());

        // View cart
        mockMvc.perform(get("/api/cart")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value(1))
                .andExpect(jsonPath("$[0].quantity").value(2));
    }

    @Test
    void testCheckout() throws Exception {

        MvcResult loginResult = mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"shivam@example.com\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession();


        mockMvc.perform(post("/api/cart/add")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":1,\"quantity\":2}"))
                .andExpect(status().isOk());


        mockMvc.perform(post("/api/checkout")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(Constants.ORDER_PLACED_SUCCESS));
    }

    @Test
    void testModifyAndRemoveCartItem() throws Exception {

        MvcResult loginResult = mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"shivam@example.com\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession();


        mockMvc.perform(post("/api/cart/add")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":1,\"quantity\":2}"))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/cart/modify")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":1,\"quantity\":5}"))
                .andExpect(status().isOk())
                .andExpect(content().string(Constants.CART_UPDATED));


        mockMvc.perform(delete("/api/cart/remove")
                        .session(session)
                        .param("itemId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string(Constants.CART_ITEM_REMOVED));
    }
}

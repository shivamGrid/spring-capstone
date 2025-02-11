package com.storeapp.cart.service;

import com.storeapp.cart.dto.*;
import com.storeapp.cart.exception.InsufficientStockException;
import com.storeapp.cart.exception.ResourceNotFoundException;
import com.storeapp.cart.model.Product;
import com.storeapp.cart.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) {
            throw new ResourceNotFoundException("No products available");
        }
        return products.stream()
                .map(product -> new ProductResponse(
                        product.getId(),
                        product.getTitle(),
                        product.getAvailable(),
                        product.getPrice()
                ))
                .collect(Collectors.toList());
    }

    public Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));
    }

    public void validateStockAvailability(Long productId, int requestedQuantity) {
        Product product = getProductById(productId);
        if (product.getAvailable() < requestedQuantity) {
            throw new InsufficientStockException("Insufficient stock for product: " + product.getTitle());
        }
    }

    public void updateProductStock(Long productId, int quantity) {
        Product product = getProductById(productId);
        product.setAvailable(product.getAvailable() - quantity);
        productRepository.save(product);
    }
}

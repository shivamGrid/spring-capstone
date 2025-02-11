package com.storeapp.cart.dto;

import lombok.Data;

@Data
public class CartItemModifyRequest {
    private Long productId;
    private int quantity;

    public Long getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }
}

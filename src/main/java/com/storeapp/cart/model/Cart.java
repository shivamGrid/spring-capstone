package com.storeapp.cart.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "cart")
    private List<CartItem> items = new ArrayList<>();

    public Cart() {}

    public Cart(Long userId) {
        this.userId = userId;
    }

    public void addItem(CartItem item) {
        this.items.add(item);
    }

    public void removeItem(Long productId) {
        this.items.removeIf(item -> item.getProduct().getId().equals(productId));
    }

    public List<CartItem> getItems() {
        return items;
    }
}

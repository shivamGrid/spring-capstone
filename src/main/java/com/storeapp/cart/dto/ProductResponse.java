package com.storeapp.cart.dto;

public class ProductResponse {
    private Long id;
    private String title;
    private int available;
    private double price;

    public ProductResponse(Long id, String title, int available, double price) {
        this.id = id;
        this.title = title;
        this.available = available;
        this.price = price;
    }
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}

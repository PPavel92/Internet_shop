package com.example.shop.observer;

import com.example.shop.model.Product;

public class CartItem {
    private Product product;

    public CartItem(Product product) {
        this.product = product;
    }

    public Product getProduct() {
        return product;
    }

    public String getDescription() {
        return product.getDescription();
    }

    public double getPrice() {
        return product.getPrice();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CartItem cartItem = (CartItem) obj;
        return Double.compare(cartItem.getPrice(), getPrice()) == 0 &&
                getDescription().equals(cartItem.getDescription());
    }

    @Override
    public int hashCode() {
        return 31 * getDescription().hashCode() + (int) getPrice();
    }
}

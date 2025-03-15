package com.example.shop.observer;

import com.example.shop.model.Product;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private List<CartItem> items;
    private List<CartObserver> observers;

    public Cart() {
        this.items = new ArrayList<>();
        this.observers = new ArrayList<>();
    }

    public void addItem(Product product) {
        this.items.add(new CartItem(product));
        notifyObservers("Добавлен товар: " + product.getDescription());
    }

    public void removeItem(CartItem item) {
        this.items.remove(item);
        notifyObservers("Удален товар: " + item.getDescription());
    }

    public List<CartItem> getItems() {
        return items;
    }

    public double getTotalPrice() {
        return items.stream().mapToDouble(CartItem::getPrice).sum();
    }

    public String getCartItemsString() {
        StringBuilder cartContent = new StringBuilder();
        for (CartItem item : items) {
            cartContent.append(item.getDescription())
                    .append(" - ")
                    .append(item.getPrice())
                    .append(" руб.\n");
        }
        return cartContent.toString();
    }

    public void addObserver(CartObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers(String message) {
        for (CartObserver observer : observers) {
            observer.update(message);
        }
    }
}


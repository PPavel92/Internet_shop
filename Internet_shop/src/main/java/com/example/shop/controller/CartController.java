package com.example.shop.controller;

import com.example.shop.observer.Cart;
import com.example.shop.observer.CartItem;
import com.example.shop.model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class CartController {
    private final Cart cart;
    private final ObservableList<Product> cartItems;
    private final TextArea cartText;
    private final Label totalLabel;

    public CartController() {
        this.cart = new Cart();
        this.cartItems = FXCollections.observableArrayList();
        this.cartText = new TextArea();
        this.totalLabel = new Label("Общая сумма: 0 руб.");
    }

    public VBox getCartView() {
        VBox cartPanel = new VBox(10, cartText, totalLabel);
        return cartPanel;
    }

    public void addToCart(Product product) {
        cart.addItem(product);
        cartItems.add(product);
        updateCartDisplay();
    }

    public void removeFromCart(Product product) {
        cartItems.remove(product);
        cart.getItems().removeIf(item -> item.getProduct().equals(product));
        updateCartDisplay();
    }

    private void updateCartDisplay() {
        cartText.setText("Ваша корзина:\n" + cart.getCartItemsString());
        totalLabel.setText("Общая сумма: " + cart.getTotalPrice() + " руб.");
    }

    public Cart getCart() {
        return cart;
    }

    public double getTotalPrice() {
        return cart.getTotalPrice();
    }

    public String getCartItemsString() {
        StringBuilder cartContent = new StringBuilder();
        for (CartItem item : cart.getItems()) {
            cartContent.append(item.getProduct().getDescription()).append(" - ")
                    .append(item.getProduct().getPrice()).append(" руб.\n");
        }
        return cartContent.toString();
    }

}



package com.example.shop.factory;

import com.example.shop.model.Product;

public class ProductFactory {

    public static Product createProduct(String description, double price, String imagePath, String s) {

        double defaultRating = 3.0;
        return new Product(description, price, "Без категории", defaultRating, imagePath);
    }
}

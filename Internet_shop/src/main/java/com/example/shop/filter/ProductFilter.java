package com.example.shop.filter;

import com.example.shop.model.Product;
import java.util.List;
import java.util.stream.Collectors;

public class ProductFilter {

    public static List<Product> filterByCategory(List<Product> products, String category) {
        return products.parallelStream()
                .filter(product -> product.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    public static List<Product> filterByPrice(List<Product> products, double minPrice, double maxPrice) {
        return products.parallelStream()
                .filter(product -> product.getPrice() >= minPrice && product.getPrice() <= maxPrice)
                .collect(Collectors.toList());
    }

    public static List<Product> filterByRating(List<Product> products, double minRating) {
        return products.parallelStream()
                .filter(product -> product.getRating() >= minRating)
                .collect(Collectors.toList());
    }
}
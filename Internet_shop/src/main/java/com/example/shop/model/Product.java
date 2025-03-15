package com.example.shop.model;

import java.util.ArrayList;
import java.util.List;

public class Product implements Cloneable {

    private String description;
    private double price;
    private String category;
    private double rating;
    private String imagePath;
    private List<Review> reviews;

    public Product(String description, double price, String category, double rating, String imagePath) {
        this.description = description;
        this.price = price;
        this.category = category;
        this.rating = rating;
        this.imagePath = imagePath;
        this.reviews = new ArrayList<>();
    }

    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public String getCategory() { return category; }
    public double getRating() { return rating; }
    public String getImagePath() { return imagePath; }
    public List<Review> getReviews() { return reviews; }

    public void addReview(Review review) { reviews.add(review); }

    public double calculateAverageRating() {
        if (reviews.isEmpty()) return 0;
        double sum = reviews.stream().mapToDouble(Review::getRating).sum();
        return sum / reviews.size();
    }

    @Override
    public Product clone() {
        return new Product(this.description, this.price, this.category, this.rating, this.imagePath);
    }
}

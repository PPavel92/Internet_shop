package com.example.shop.model;

public class Review {

    private String username;
    private String comment;
    private double rating;

    public Review(String username, String comment, double rating) {
        this.username = username;
        this.comment = comment;
        this.rating = rating;
    }

    public String getUsername() {
        return username;
    }

    public String getComment() {
        return comment;
    }

    public double getRating() {
        return rating;
    }
}

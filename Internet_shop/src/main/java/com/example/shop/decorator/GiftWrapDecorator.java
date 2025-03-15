package com.example.shop.decorator;

import com.example.shop.model.Product;

public class GiftWrapDecorator extends Product {

    public GiftWrapDecorator(Product product) {

        super(product.getDescription(), product.getPrice() + 5, "Gift Wrap", product.getRating(), product.getImagePath());
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " с упаковкой";
    }
}

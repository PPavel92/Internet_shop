package com.example.shop.controller;

import com.example.shop.model.Product;
import com.example.shop.factory.ProductFactory;
import com.example.shop.model.Review;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class ProductController {
    private final ObservableList<Product> allProducts;
    private final VBox productListView;
    private final CartController cartController;
    private final VBox productInfoPanel;

    public ProductController(CartController cartController, VBox productInfoPanel) {
        this.cartController = cartController;
        this.productInfoPanel = productInfoPanel;
        this.allProducts = FXCollections.observableArrayList();
        this.productListView = new VBox(10);

        loadProducts();
        updateProductList(allProducts);
    }

    private void loadProducts() {
        System.out.println("Загрузка товаров...");
        allProducts.addAll(
                ProductFactory.createProduct("Ноутбук", 50000, "Ноутбуки", "images/laptop.jpg"),
                ProductFactory.createProduct("Смартфон", 30000, "Смартфоны", "images/smartphone.jpg"),
                ProductFactory.createProduct("Наушники", 5000, "Аксессуары", "images/headphones.jpg")
        );
        System.out.println("Загружено товаров: " + allProducts.size());
    }


    public List<Product> getAllProducts() {
        return new ArrayList<>(allProducts);
    }


    public VBox getProductView() {
        return productListView;
    }

    public void updateProductList(List<Product> filteredProducts) {
        System.out.println("Обновление списка товаров... Найдено: " + filteredProducts.size());
        productListView.getChildren().clear();

        for (Product product : filteredProducts) {
            System.out.println("Добавление товара в UI: " + product.getDescription());
            Button productButton = new Button(product.getDescription() + " - " + product.getPrice() + " руб.");
            productButton.setOnAction(e -> showProductDetails(product));
            productListView.getChildren().add(productButton);
        }
    }


    private void showProductDetails(Product product) {
        System.out.println("Открытие панели описания: " + product.getDescription());
        productInfoPanel.getChildren().clear();

        Label nameLabel = new Label("Название: " + product.getDescription());
        Label priceLabel = new Label("Цена: " + product.getPrice() + " руб.");
        Label categoryLabel = new Label("Категория: " + product.getCategory());

        VBox reviewBox = new VBox();
        for (Review review : product.getReviews()) {
            System.out.println("Отображение отзыва: " + review.getComment());
            reviewBox.getChildren().add(new Label(review.getUsername() + " (" + review.getRating() + "): " + review.getComment()));
        }

        Button addToCartButton = new Button("Добавить в корзину");
        addToCartButton.setOnAction(e -> cartController.addToCart(product));

        Button closeButton = new Button("Закрыть");
        closeButton.setOnAction(e -> productInfoPanel.getChildren().clear());

        productInfoPanel.getChildren().addAll(nameLabel, priceLabel, categoryLabel, reviewBox, addToCartButton, closeButton);
    }

}

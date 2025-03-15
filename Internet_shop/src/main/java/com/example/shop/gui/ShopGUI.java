package com.example.shop.gui;

import com.example.shop.factory.ProductFactory;
import com.example.shop.model.Product;
import com.example.shop.model.Review;
import com.example.shop.service.CreditCardPayment;
import com.example.shop.service.PayPalPayment;
import com.example.shop.service.PaymentStrategy;
import com.example.shop.observer.Cart;
import com.example.shop.observer.CartItem;
import com.example.shop.util.FileUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class ShopGUI extends Application {

    private Cart cart;
    private TextArea cartText;
    private Label totalLabel;
    private String customerName;
    private String customerSurname;
    private boolean isAuthenticated = false;
    private PaymentStrategy paymentStrategy;
    private VBox checkBoxesPanel;

    private TextField minPriceField;
    private TextField maxPriceField;
    private ComboBox<String> productComboBox;
    private Button filterButton;
    private Button resetFilterButton;
    private VBox productListView;
    private TabPane tabPane;
    private VBox productInfoPanel;
    private BorderPane mainLayout;


    private ComboBox<String> priceSortComboBox;


    private List<Product> allProducts;

    public ShopGUI() {
        cart = new Cart();
        allProducts = new ArrayList<>();
    }

    {
        this.productInfoPanel = new VBox();
    }

    @Override
    public void start(Stage primaryStage) {
        createMainShopWindow(primaryStage);
    }

    private void createMainShopWindow(Stage primaryStage) {
        mainLayout = new BorderPane();



        tabPane = new TabPane();


        Tab productsTab = new Tab("Список товаров");
        productsTab.setClosable(false);
        VBox productPanel = new VBox(10);
        loadProducts(productPanel);
        productsTab.setContent(productPanel);


        Tab cartTab = new Tab("Корзина");
        cartTab.setClosable(false);
        VBox cartPanel = new VBox(10);
        loadCart(cartPanel);
        cartTab.setContent(cartPanel);


        tabPane.getTabs().addAll(productsTab, cartTab);
        mainLayout.setCenter(tabPane);

        HBox footer = new HBox(10);
        footer.setPadding(new javafx.geometry.Insets(20));
        Button checkoutButton = new Button("Перейти к оформлению");
        checkoutButton.setOnAction(e -> handleCheckout(primaryStage));
        footer.getChildren().add(checkoutButton);
        mainLayout.setBottom(footer);

        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setTitle("Интернет-магазин");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadProducts(VBox productPanel) {

        String[] productNames = {
                "Ноутбук", "Смартфон", "Наушники", "Телевизор", "Планшет", "Монитор",
                "Клавиатура", "Мышь", "Камера", "Флешка"
        };

        double[] productPrices = {
                50000, 30000, 5000, 40000, 25000, 15000, 2000, 1500, 10000, 2000
        };

        String[] productImages = {
                "images/laptop.jpg", "images/smartphone.jpg", "images/headphones.jpg", "images/tv.jpg",
                "images/tablet.jpg", "images/monitor.jpg", "images/keyboard.jpg", "images/mouse.jpg",
                "images/camera.jpg", "images/flashdrive.jpg"
        };


        priceSortComboBox = new ComboBox<>();
        priceSortComboBox.getItems().addAll("По возрастанию цены", "По убыванию цены");
        priceSortComboBox.setValue("По возрастанию цены");
        priceSortComboBox.setOnAction(e -> updateProductList());

        minPriceField = new TextField();
        minPriceField.setPromptText("Мин. цена");
        maxPriceField = new TextField();
        maxPriceField.setPromptText("Макс. цена");


        filterButton = new Button("Применить фильтр");
        filterButton.setOnAction(e -> updateProductList());


        resetFilterButton = new Button("Сбросить фильтр");
        resetFilterButton.setOnAction(e -> resetFilters());

        HBox filterPanel = new HBox(10);
        filterPanel.getChildren().addAll(new Label("Цена:"), minPriceField, maxPriceField, filterButton, resetFilterButton);
        productPanel.getChildren().add(filterPanel);

        productComboBox = new ComboBox<>();
        productComboBox.getItems().addAll("Все товары", "Ноутбук", "Смартфон", "Наушники", "Телевизор", "Планшет", "Монитор", "Клавиатура", "Мышь", "Камера", "Флешка");
        productComboBox.setValue("Все товары");
        productComboBox.setOnAction(e -> updateProductList());
        HBox productPanelBox = new HBox(10);
        productPanelBox.getChildren().addAll(new Label("Товары:"), productComboBox);
        productPanel.getChildren().add(productPanelBox);

        for (int i = 0; i < productNames.length; i++) {
            Product product = ProductFactory.createProduct(productNames[i], productPrices[i], productImages[i], "images/laptop.jpg");
            allProducts.add(product);
        }

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        productListView = new VBox(10);
        productListView.setSpacing(10);

        updateProductList();

        scrollPane.setContent(productListView);

        productPanel.getChildren().add(scrollPane);
    }

    private void updateProductList() {
        String selectedProduct = productComboBox.getValue();

        List<Product> filteredProducts = new ArrayList<>();
        for (Product product : allProducts) {
            if (selectedProduct.equals("Все товары") || product.getDescription().equals(selectedProduct)) {
                filteredProducts.add(product);
            }
        }

        double minPrice = minPriceField.getText().isEmpty() ? 0 : Double.parseDouble(minPriceField.getText());
        double maxPrice = maxPriceField.getText().isEmpty() ? Double.MAX_VALUE : Double.parseDouble(maxPriceField.getText());
        filteredProducts.removeIf(product -> product.getPrice() < minPrice || product.getPrice() > maxPrice);

        String priceSort = priceSortComboBox.getValue();
        if (priceSort.equals("По возрастанию цены")) {
            filteredProducts.sort(Comparator.comparingDouble(Product::getPrice));
        } else if (priceSort.equals("По убыванию цены")) {
            filteredProducts.sort(Comparator.comparingDouble(Product::getPrice).reversed());
        }

        productListView.getChildren().clear();
        for (Product product : filteredProducts) {
            Button productButton = new Button("" + product.getDescription() + " - " + product.getPrice() + " руб.");
            productButton.setPrefSize(300, 120);

            try {
                Image productImage = new Image(getClass().getResourceAsStream("/" + product.getImagePath()));
                ImageView imageView = new ImageView(productImage);
                imageView.setFitWidth(50);
                imageView.setFitHeight(50);
                productButton.setGraphic(imageView);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Ошибка загрузки изображения: " + product.getImagePath());
            }

            productButton.setOnAction(e -> handleProductSelection(product));
            productListView.getChildren().add(productButton);
        }
    }

    private void resetFilters() {
        minPriceField.clear();
        maxPriceField.clear();
        productComboBox.setValue("Все товары");
        priceSortComboBox.setValue("По возрастанию цены");
        updateProductList();
    }


    private void handleProductSelection(Product product) {
        showProductDetails(product);
        updateCartDisplay();
    }

    private void loadCart(VBox cartPanel) {
        cartText = new TextArea("Ваша корзина:");
        cartPanel.getChildren().add(cartText);

        totalLabel = new Label("Общая сумма: 0 руб.");
        cartPanel.getChildren().add(totalLabel);

        checkBoxesPanel = new VBox(10);
        cartPanel.getChildren().add(checkBoxesPanel);

        Button removeButton = new Button("Удалить выбранные товары");
        removeButton.setOnAction(e -> handleRemoveItems());
        cartPanel.getChildren().add(removeButton);
    }

    private void updateCartDisplay() {
        cartText.setText("Ваша корзина:\n" + cart.getCartItemsString());
        totalLabel.setText("Общая сумма: " + cart.getTotalPrice() + " руб.");
        updateCheckBoxes();
    }

    private void updateCheckBoxes() {
        checkBoxesPanel.getChildren().clear();

        for (int i = 0; i < cart.getItems().size(); i++) {
            CartItem item = cart.getItems().get(i);
            CheckBox checkBox = new CheckBox("Удалить: " + item.getDescription() + " - " + item.getPrice() + " руб.");
            checkBox.setUserData(item);
            checkBoxesPanel.getChildren().add(checkBox);
        }
    }

    private void handleRemoveItems() {
        List<CheckBox> selectedCheckBoxes = new ArrayList<>();

        for (javafx.scene.Node node : checkBoxesPanel.getChildren()) {
            CheckBox checkBox = (CheckBox) node;
            if (checkBox.isSelected()) {
                selectedCheckBoxes.add(checkBox);
            }
        }

        for (CheckBox checkBox : selectedCheckBoxes) {
            CartItem item = (CartItem) checkBox.getUserData();
            cart.removeItem(item);
        }

        updateCartDisplay();
    }

    private void handleCheckout(Stage primaryStage) {
        if (!isAuthenticated) {
            showLoginDialog(primaryStage);
        } else {
            ChoiceDialog<String> paymentDialog = new ChoiceDialog<>("Кредитная карта", "Кредитная карта", "PayPal");
            paymentDialog.setTitle("Выбор способа оплаты");
            paymentDialog.setHeaderText("Выберите способ оплаты");
            paymentDialog.showAndWait().ifPresent(paymentMethod -> {
                if (paymentMethod.equals("Кредитная карта")) {
                    paymentStrategy = new CreditCardPayment();
                } else {
                    paymentStrategy = new PayPalPayment();
                }

                paymentStrategy.pay(cart.getTotalPrice());
                generateReceipt();
                showConfirmationDialog(primaryStage);
            });
        }
    }

    private void showLoginDialog(Stage primaryStage) {
        TextInputDialog loginDialog = new TextInputDialog();
        loginDialog.setTitle("Авторизация");
        loginDialog.setHeaderText("Введите ваше имя:");
        loginDialog.showAndWait().ifPresent(name -> {
            customerName = name;
            TextInputDialog surnameDialog = new TextInputDialog();
            surnameDialog.setTitle("Авторизация");
            surnameDialog.setHeaderText("Введите вашу фамилию:");
            surnameDialog.showAndWait().ifPresent(surname -> {
                customerSurname = surname;
                isAuthenticated = true;
                handleCheckout(primaryStage);
            });
        });
    }

    private void generateReceipt() {
        List<CartItem> purchasedItems = List.copyOf(cart.getItems());

        CompletableFuture.runAsync(() -> {
            String receipt = "Чек от " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")) + "\n" +
                    "Покупатель: " + customerName + " " + customerSurname + "\n" +
                    "Товары:\n" + cart.getCartItemsString() + "\n" +
                    "Общая сумма: " + cart.getTotalPrice() + " руб.";

            String filename = "receipts/receipt_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt";
            FileUtil.writeToFile(filename, receipt);

            Platform.runLater(() -> {
                cart.getItems().clear();
                updateCartDisplay();
                showReviewDialogsSequentially(purchasedItems, 0);
            });
        });
    }


    private void showProductDetails(Product product) {
        productInfoPanel.getChildren().clear();
        Label nameLabel = new Label("Название: " + product.getDescription());
        Label priceLabel = new Label("Цена: " + product.getPrice() + " руб.");
        Label categoryLabel = new Label("Категория: " + product.getCategory());

        VBox reviewBox = new VBox();
        for (Review review : product.getReviews()) {
            reviewBox.getChildren().add(new Label(review.getUsername() + " (" + review.getRating() + "): " + review.getComment()));
        }

        Button addToCartButton = new Button("Добавить в корзину");
        addToCartButton.setOnAction(e -> {
            cart.addItem(product);
            updateCartDisplay();
        });

        Button closeButton = new Button("Закрыть");
        closeButton.setOnAction(e -> productInfoPanel.getChildren().clear());

        productInfoPanel.getChildren().addAll(nameLabel, priceLabel, categoryLabel, reviewBox, addToCartButton, closeButton);
        mainLayout.setRight(productInfoPanel);
    }

    private void showConfirmationDialog(Stage primaryStage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Подтверждение");
        alert.setHeaderText("Заказ оформлен успешно!");
        alert.setContentText("Спасибо за покупку!");
        alert.showAndWait();
    }

    private void showReviewDialog(List<CartItem> purchasedItems) {
        Stage reviewStage = new Stage();
        VBox reviewBox = new VBox(10);

        for (CartItem item : purchasedItems) {
            TextArea reviewField = new TextArea();
            Button submitButton = new Button("Оставить отзыв");

            submitButton.setOnAction(e -> {
                Review review = new Review(customerName + " " + customerSurname, reviewField.getText(), 5.0);
                item.getProduct().addReview(review);
                reviewStage.close();
            });

            reviewBox.getChildren().addAll(new Label("Отзыв на " + item.getDescription()), reviewField, submitButton);
        }

        Scene scene = new Scene(reviewBox, 400, 300);
        reviewStage.setScene(scene);
        reviewStage.show();
    }

    private void showReviewDialogsSequentially(List<CartItem> items, int index) {
        if (index >= items.size()) return;

        Stage reviewStage = new Stage();
        VBox reviewBox = new VBox(10);
        TextArea reviewField = new TextArea();
        Button submitButton = new Button("Оставить отзыв");

        CartItem item = items.get(index);
        submitButton.setOnAction(e -> {
            Review review = new Review(customerName + " " + customerSurname, reviewField.getText(), 5.0);
            item.getProduct().addReview(review);
            reviewStage.close();
            showReviewDialogsSequentially(items, index + 1);
        });

        reviewBox.getChildren().addAll(new Label("Отзыв на " + item.getDescription()), reviewField, submitButton);

        Scene scene = new Scene(reviewBox, 400, 300);
        reviewStage.setScene(scene);
        reviewStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

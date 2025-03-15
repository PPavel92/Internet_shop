package com.example.shop.controller;

import com.example.shop.service.CreditCardPayment;
import com.example.shop.service.PayPalPayment;
import com.example.shop.service.PaymentStrategy;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;

public class PaymentController {
    private final CartController cartController;
    private PaymentStrategy paymentStrategy;

    public PaymentController(CartController cartController) {
        this.cartController = cartController;
    }

    public void processPayment() {
        if (cartController.getCart().getItems().isEmpty()) {
            showAlert("Ошибка", "Корзина пуста!");
            return;
        }

        double totalAmount = cartController.getTotalPrice();
        System.out.println("Общая сумма к оплате: " + totalAmount + " руб.");

        ChoiceDialog<String> paymentDialog = new ChoiceDialog<>("Кредитная карта", "Кредитная карта", "PayPal");
        paymentDialog.setTitle("Выбор оплаты");
        paymentDialog.setHeaderText("Выберите способ оплаты");

        paymentDialog.showAndWait().ifPresent(method -> {
            paymentStrategy = method.equals("Кредитная карта") ? new CreditCardPayment() : new PayPalPayment();
            paymentStrategy.pay(totalAmount);
            showSuccessDialog();
        });
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(content);
        alert.showAndWait();
    }

    private void showSuccessDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Оплата успешна");
        alert.setHeaderText("Ваш заказ оплачен!");
        alert.setContentText("Спасибо за покупку.");
        alert.showAndWait();
    }
}



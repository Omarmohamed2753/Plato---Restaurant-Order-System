package javaproject1.UI.JavaFX.Controller;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javaproject1.BLL.Service.implementation.*;
import javaproject1.DAL.Entity.*;

public class CartController {
    private static CartServiceImpl cartService = new CartServiceImpl();

    public static void show(Stage stage, User user) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f7fa;");

        HBox navbar = ClientMainController.createNavBar(stage, user);
        root.setTop(navbar);

        VBox contentBox = new VBox(20);
        contentBox.setPadding(new Insets(30));

        Label titleLabel = new Label("Shopping Cart");
        titleLabel.setStyle(
            "-fx-font-size: 28px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #000000;"
        );

        VBox cartItems = new VBox(10);
        Cart cart = user.getCart();
        
        if (cart != null && cart.getItems() != null && !cart.getItems().isEmpty()) {
            for (CartItem item : cart.getItems()) {
                cartItems.getChildren().add(createCartItemRow(stage, user, cart, item));
            }

            double total = cartService.calculateTotal(cart);
            Label totalLabel = new Label("Total: $" + String.format("%.2f", total));
            totalLabel.setStyle(
                "-fx-font-size: 24px; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: #000000;"
            );
            
            Button checkoutButton = new Button("Checkout");
            checkoutButton.setStyle(
                "-fx-background-color: #27ae60; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 16px; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 12 30; " +
                "-fx-background-radius: 20; " +
                "-fx-cursor: hand;"
            );
            checkoutButton.setOnAction(e -> CheckoutController.show(stage, user));

            contentBox.getChildren().addAll(titleLabel, cartItems, totalLabel, checkoutButton);
        } else {
            Label emptyLabel = new Label("Your cart is empty");
            emptyLabel.setStyle(
                "-fx-font-size: 18px; " +
                "-fx-text-fill: #636e72;"
            );
            contentBox.getChildren().addAll(titleLabel, emptyLabel);
        }

        root.setCenter(contentBox);
        Scene scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
    }

    private static HBox createCartItemRow(Stage stage, User user, Cart cart, CartItem item) {
        HBox row = new HBox(20);
        row.setPadding(new Insets(15));
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 8; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);"
        );

        Label nameLabel = new Label(item.getMenuItem().getName());
        nameLabel.setStyle(
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #000000;"
        );
        nameLabel.setPrefWidth(200);

        Label qtyLabel = new Label("Qty: " + item.getQuantity());
        qtyLabel.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-text-fill: #4a5568;"
        );
        qtyLabel.setPrefWidth(100);

        Label priceLabel = new Label("$" + String.format("%.2f", item.getSubPrice()));
        priceLabel.setStyle(
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #000000;"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button removeButton = new Button("Remove");
        removeButton.setStyle(
            "-fx-background-color: #e74c3c; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 6 12; " +
            "-fx-background-radius: 12; " +
            "-fx-cursor: hand; " +
            "-fx-font-size: 14px;"
        );
        removeButton.setOnAction(e -> {
            cartService.removeItem(cart, item);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Item removed from cart");
            alert.showAndWait();
            // Refresh the cart view
            show(stage, user);
        });

        row.getChildren().addAll(nameLabel, qtyLabel, priceLabel, spacer, removeButton);
        return row;
    }
}
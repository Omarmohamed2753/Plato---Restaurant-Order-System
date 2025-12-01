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
import javaproject1.DAL.Enums.OrderStatus;

import java.util.List;
import javafx.scene.control.Alert;
import javafx.scene.layout.HBox;

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
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 28));

        VBox cartItems = new VBox(10);
        Cart cart = user.getCart();
        
        if (cart != null && cart.getItems() != null && !cart.getItems().isEmpty()) {
            for (CartItem item : cart.getItems()) {
                cartItems.getChildren().add(createCartItemRow(user, cart, item));
            }

            double total = cartService.calculateTotal(cart);
            Label totalLabel = new Label("Total: $" + String.format("%.2f", total));
            totalLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
            
            Button checkoutButton = new Button("Checkout");
            checkoutButton.setStyle(
                "-fx-background-color: #27ae60; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 16px; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 12 30 12 30; " +
                "-fx-background-radius: 20; " +
                "-fx-cursor: hand;"
            );
            checkoutButton.setOnAction(e -> CheckoutController.show(stage, user));

            contentBox.getChildren().addAll(titleLabel, cartItems, totalLabel, checkoutButton);
        } else {
            Label emptyLabel = new Label("Your cart is empty");
            emptyLabel.setFont(Font.font("System", 18));
            contentBox.getChildren().addAll(titleLabel, emptyLabel);
        }

        root.setCenter(contentBox);
        Scene scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
    }

    private static HBox createCartItemRow(User user, Cart cart, CartItem item) {
        HBox row = new HBox(20);
        row.setPadding(new Insets(15));
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-background-color: white; -fx-background-radius: 8;");

        Label nameLabel = new Label(item.getMenuItem().getName());
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        nameLabel.setPrefWidth(200);

        Label qtyLabel = new Label("Qty: " + item.getQuantity());
        qtyLabel.setPrefWidth(100);

        Label priceLabel = new Label("$" + String.format("%.2f", item.getSubPrice()));
        priceLabel.setFont(Font.font("System", FontWeight.BOLD, 16));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button removeButton = new Button("Remove");
        removeButton.setStyle(
            "-fx-background-color: #e74c3c; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 6 12 6 12; " +
            "-fx-background-radius: 12; " +
            "-fx-cursor: hand;"
        );
        removeButton.setOnAction(e -> {
            cartService.removeItem(cart, item);
            // Refresh view - in real app you'd reload the scene
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Item removed from cart");
            alert.showAndWait();
        });

        row.getChildren().addAll(nameLabel, qtyLabel, priceLabel, spacer, removeButton);
        return row;
    }
}


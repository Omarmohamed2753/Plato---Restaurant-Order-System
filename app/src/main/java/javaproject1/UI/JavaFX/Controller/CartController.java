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

    // Theme Colors
    private static final String BACKGROUND_DARK = "#1f2937";
    private static final String PRIMARY_COLOR = "#059669";
    private static final String ACCENT_GOLD = "#fcd34d";
    private static final String CARD_BACKGROUND = "#374151";
    private static final String TEXT_COLOR_LIGHT = "#f9fafb";
    private static final String TEXT_COLOR_SECONDARY = "#d1d5db";
    private static final String ITEM_BACKGROUND = "#2b3543"; 
    private static final String REMOVE_RED = "#ef4444";
    
    private static final String BUTTON_STYLE_BASE = 
        "-fx-font-weight: bold; -fx-padding: 12 30; -fx-background-radius: 20; -fx-cursor: hand; -fx-font-size: 16px;";
        
    private static final String REMOVE_BUTTON_BASE = 
        "-fx-font-weight: bold; -fx-padding: 8 18; -fx-background-radius: 15; -fx-cursor: hand; -fx-font-size: 14px;";

    public static void show(Stage stage, User user) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND_DARK + ";");

        HBox navbar = ClientMainController.createNavBar(stage, user);
        root.setTop(navbar);

        VBox contentBox = new VBox(25);
        contentBox.setPadding(new Insets(50));
        contentBox.setAlignment(Pos.TOP_CENTER);
        contentBox.setMaxWidth(1100);

        Label titleLabel = new Label("🛒 Your Shopping Cart");
        titleLabel.setStyle(
            "-fx-font-size: 38px; " +
            "-fx-font-weight: extra-bold; " +
            "-fx-text-fill: " + ACCENT_GOLD + ";"
        );
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        VBox cartItems = new VBox(15);
        cartItems.setAlignment(Pos.TOP_CENTER);
        Cart cart = user.getCart();
        
        HBox totalAndCheckout = new HBox(40);
        totalAndCheckout.setAlignment(Pos.CENTER_RIGHT);
        totalAndCheckout.setPadding(new Insets(20, 0, 0, 0));


        if (cart != null && cart.getItems() != null && !cart.getItems().isEmpty()) {
            for (CartItem item : cart.getItems()) {
                cartItems.getChildren().add(createCartItemRow(stage, user, cart, item));
            }

            double total = cartService.calculateTotal(cart);
            Label totalLabel = new Label("Grand Total: $" + String.format("%.2f", total));
            totalLabel.setStyle(
                "-fx-font-size: 28px; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: " + PRIMARY_COLOR + ";"
            );
            
            Button checkoutButton = new Button("Proceed to Checkout 💳");
            checkoutButton.setStyle(
                "-fx-background-color: " + PRIMARY_COLOR + "; " +
                "-fx-text-fill: " + TEXT_COLOR_LIGHT + "; " +
                BUTTON_STYLE_BASE
            );
            checkoutButton.setOnMouseEntered(e -> checkoutButton.setStyle(checkoutButton.getStyle() + "-fx-background-color: #047857;"));
            checkoutButton.setOnMouseExited(e -> checkoutButton.setStyle(checkoutButton.getStyle().replace("-fx-background-color: #047857;", "-fx-background-color: " + PRIMARY_COLOR + ";")));
            checkoutButton.setOnAction(e -> CheckoutController.show(stage, user));

            totalAndCheckout.getChildren().addAll(totalLabel, checkoutButton);
            
            scrollPane.setContent(cartItems);
            contentBox.getChildren().addAll(titleLabel, scrollPane, totalAndCheckout);
        } else {
            Label emptyLabel = new Label("Your cart is empty. Start adding delicious items!");
            emptyLabel.setStyle(
                "-fx-font-size: 20px; " +
                "-fx-text-fill: " + TEXT_COLOR_SECONDARY + ";"
            );
            emptyLabel.setPadding(new Insets(50, 0, 0, 0));
            contentBox.getChildren().addAll(titleLabel, emptyLabel);
        }

        root.setCenter(contentBox);
        Scene scene = new Scene(root, 1200, 800);
        stage.setScene(scene);
    }

    private static HBox createCartItemRow(Stage stage, User user, Cart cart, CartItem item) {
        HBox row = new HBox(25);
        row.setPadding(new Insets(20));
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle(
            "-fx-background-color: " + ITEM_BACKGROUND + "; " +
            "-fx-background-radius: 12; " + 
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 3);"
        );

        Label nameLabel = new Label(item.getMenuItem().getName());
        nameLabel.setStyle(
            "-fx-font-size: 18px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: " + TEXT_COLOR_LIGHT + ";"
        );
        nameLabel.setPrefWidth(350);

        Label qtyLabel = new Label("Quantity: " + item.getQuantity());
        qtyLabel.setStyle(
            "-fx-font-size: 16px; " +
            "-fx-text-fill: " + TEXT_COLOR_SECONDARY + ";"
        );
        qtyLabel.setPrefWidth(210);

        Label priceLabel = new Label("Price: $" + String.format("%.2f", item.getSubPrice()));
        priceLabel.setStyle(
            "-fx-font-size: 18px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: " + ACCENT_GOLD + ";"
        );
        priceLabel.setPrefWidth(210);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // تم تغيير نص الزر لضمان ظهور كلمة "Remove" بالكامل
        Button removeButton = new Button("Remove Item ");
        removeButton.setStyle(
            "-fx-background-color: " + REMOVE_RED + "; " +
            "-fx-text-fill: " + TEXT_COLOR_LIGHT + "; " +
            REMOVE_BUTTON_BASE
        );
        removeButton.setOnMouseEntered(e -> removeButton.setStyle(removeButton.getStyle() + "-fx-background-color: #dc2626;"));
        removeButton.setOnMouseExited(e -> removeButton.setStyle(removeButton.getStyle().replace("-fx-background-color: #dc2626;", "-fx-background-color: " + REMOVE_RED + ";")));
        
        removeButton.setOnAction(e -> {
            cartService.removeItem(cart, item);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Item removed from cart", ButtonType.OK);
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.setStyle("-fx-background-color: " + CARD_BACKGROUND + ";");
            dialogPane.lookup(".label.content").setStyle("-fx-text-fill: " + TEXT_COLOR_LIGHT + "; -fx-font-size: 14px;");
            
            alert.showAndWait();
            // Refresh the cart view
            show(stage, user);
        });

        removeButton.setPrefWidth(210); 

        row.getChildren().addAll(nameLabel, qtyLabel, priceLabel, spacer, removeButton);
        return row;
    }
}
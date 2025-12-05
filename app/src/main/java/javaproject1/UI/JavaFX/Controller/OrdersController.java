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

import java.text.SimpleDateFormat;

public class OrdersController {
    private static OrderServiceImpl orderService = new OrderServiceImpl();

    public static void show(Stage stage, User user) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f7fa;");

        HBox navbar = ClientMainController.createNavBar(stage, user);
        root.setTop(navbar);

        VBox contentBox = new VBox(20);
        contentBox.setPadding(new Insets(30));

        Label titleLabel = new Label("My Orders");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.web("#1a1a1a"));

        VBox ordersBox = new VBox(15);
        
        // Refresh user data to get latest orders
        UserServiceImpl userService = new UserServiceImpl();
        User freshUser = userService.getUserById(Integer.parseInt(user.getId()));
        
        if (freshUser != null && freshUser.getOrders() != null && !freshUser.getOrders().isEmpty()) {
            System.out.println("DEBUG: Found " + freshUser.getOrders().size() + " orders for user " + user.getName());
            
            for (Order order : freshUser.getOrders()) {
                System.out.println("DEBUG: Creating card for order #" + order.getOrderId());
                ordersBox.getChildren().add(createOrderCard(order));
            }
        } else {
            System.out.println("DEBUG: No orders found for user");
            Label emptyLabel = new Label("No orders yet. Start shopping to place your first order!");
            emptyLabel.setFont(Font.font("System", 18));
            emptyLabel.setTextFill(Color.web("#636e72"));
            emptyLabel.setWrapText(true);
            ordersBox.getChildren().add(emptyLabel);
        }

        contentBox.getChildren().addAll(titleLabel, ordersBox);

        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        root.setCenter(scrollPane);
        Scene scene = new Scene(root, 1000, 700);
        stage.setScene(scene);
    }

    private static VBox createOrderCard(Order order) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 10; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 10, 0, 0, 3);" +
            "-fx-border-color: #e0e0e0; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 10;"
        );
        card.setMinHeight(150); // Ensure minimum height
        card.setPrefWidth(800);

        System.out.println("DEBUG: Creating card for order #" + order.getOrderId());
        System.out.println("  Status: " + order.getStatus());
        System.out.println("  Total: $" + order.getTotalAmount());
        System.out.println("  Restaurant: " + (order.getRestaurant() != null ? order.getRestaurant().getName() : "N/A"));

        // Header with order ID and status
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);

        Label idLabel = new Label("Order #" + (order.getOrderId() != null ? order.getOrderId() : "N/A"));
        idLabel.setStyle(
            "-fx-font-size: 18px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #000000;"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label statusLabel = new Label(order.getStatus() != null ? order.getStatus().toString() : "UNKNOWN");
        statusLabel.setStyle(getStatusStyle(order.getStatus()));
        statusLabel.setPadding(new Insets(8, 20, 8, 20));

        header.getChildren().addAll(idLabel, spacer, statusLabel);

        // Restaurant info
        String restaurantName = order.getRestaurant() != null ? order.getRestaurant().getName() : "N/A";
        Label restaurantLabel = new Label("🏪 Restaurant: " + restaurantName);
        restaurantLabel.setStyle(
            "-fx-font-size: 15px; " +
            "-fx-text-fill: #2d3436; " +
            "-fx-font-weight: bold;"
        );

        // Order date
        String dateStr = "N/A";
        if (order.getOrderDate() != null) {
            dateStr = new SimpleDateFormat("MMM dd, yyyy HH:mm").format(order.getOrderDate());
        }
        Label dateLabel = new Label("📅 Date: " + dateStr);
        dateLabel.setStyle(
            "-fx-font-size: 13px; " +
            "-fx-text-fill: #636e72;"
        );

        // Items section
        VBox itemsBox = new VBox(8);
        itemsBox.setPadding(new Insets(10, 0, 10, 0));
        itemsBox.setStyle(
            "-fx-background-color: #f8f9fa; " +
            "-fx-background-radius: 8; " +
            "-fx-padding: 15;"
        );

        if (order.getItems() != null && !order.getItems().isEmpty()) {
            Label itemsTitle = new Label("📦 Items:");
            itemsTitle.setStyle(
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: #000000;"
            );
            itemsBox.getChildren().add(itemsTitle);

            System.out.println("  Items count: " + order.getItems().size());
            for (CartItem item : order.getItems()) {
                String itemName = item.getMenuItem() != null ? item.getMenuItem().getName() : "Unknown Item";
                int qty = item.getQuantity();
                double price = item.getSubPrice();
                
                System.out.println("    - " + itemName + " x" + qty + " = $" + String.format("%.2f", price));
                
                Label itemLabel = new Label("  • " + itemName + " x" + qty + " - $" + String.format("%.2f", price));
                itemLabel.setStyle(
                    "-fx-font-size: 13px; " +
                    "-fx-text-fill: #2d3436; " +
                    "-fx-padding: 3 0 3 10;"
                );
                itemsBox.getChildren().add(itemLabel);
            }
        } else {
            System.out.println("  No items found!");
            Label noItems = new Label("No items information available");
            noItems.setStyle(
                "-fx-font-size: 13px; " +
                "-fx-text-fill: #636e72; " +
                "-fx-font-style: italic;"
            );
            itemsBox.getChildren().add(noItems);
        }

        // Footer with total and address
        HBox footer = new HBox(30);
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.setPadding(new Insets(10, 0, 0, 0));

        VBox leftFooter = new VBox(5);
        Label totalLabel = new Label("Total: $" + String.format("%.2f", order.getTotalAmount()));
        totalLabel.setStyle(
            "-fx-font-size: 20px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #27ae60;"
        );
        leftFooter.getChildren().add(totalLabel);

        VBox rightFooter = new VBox(5);
        String addressStr = order.getDeliveryAddress() != null ? order.getDeliveryAddress().toString() : "N/A";
        Label addressLabel = new Label("📍 Delivery to: " + addressStr);
        addressLabel.setStyle(
            "-fx-font-size: 12px; " +
            "-fx-text-fill: #636e72;"
        );
        addressLabel.setWrapText(true);
        addressLabel.setMaxWidth(400);
        rightFooter.getChildren().add(addressLabel);

        footer.getChildren().addAll(leftFooter, rightFooter);

        // Add all sections to card
        card.getChildren().addAll(header, restaurantLabel, dateLabel, itemsBox, footer);
        
        System.out.println("DEBUG: Card created successfully");
        return card;
    }

    private static String getStatusStyle(javaproject1.DAL.Enums.OrderStatus status) {
        if (status == null) {
            return "-fx-background-color: #e9ecef; -fx-text-fill: #495057; -fx-background-radius: 15; -fx-font-size: 13px; -fx-font-weight: bold;";
        }
        
        return switch (status) {
            case PENDING -> "-fx-background-color: #fff3cd; -fx-text-fill: #856404; -fx-background-radius: 15; -fx-font-size: 13px; -fx-font-weight: bold;";
            case CONFIRMED, PREPARING -> "-fx-background-color: #cfe2ff; -fx-text-fill: #084298; -fx-background-radius: 15; -fx-font-size: 13px; -fx-font-weight: bold;";
            case READY_FOR_DELIVERY, OUT_FOR_DELIVERY -> "-fx-background-color: #d1ecf1; -fx-text-fill: #0c5460; -fx-background-radius: 15; -fx-font-size: 13px; -fx-font-weight: bold;";
            case DELIVERED -> "-fx-background-color: #d1e7dd; -fx-text-fill: #0f5132; -fx-background-radius: 15; -fx-font-size: 13px; -fx-font-weight: bold;";
            case CANCELLED -> "-fx-background-color: #f8d7da; -fx-text-fill: #842029; -fx-background-radius: 15; -fx-font-size: 13px; -fx-font-weight: bold;";
            default -> "-fx-background-color: #e9ecef; -fx-text-fill: #495057; -fx-background-radius: 15; -fx-font-size: 13px; -fx-font-weight: bold;";
        };
    }
}

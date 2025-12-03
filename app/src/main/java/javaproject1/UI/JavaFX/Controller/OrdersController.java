package javaproject1.UI.JavaFX.Controller;

import javafx.geometry.Insets;
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
        titleLabel.setTextFill(Color.web("#000000"));

        VBox ordersBox = new VBox(15);
        
        // Refresh user data to get latest orders
        UserServiceImpl userService = new UserServiceImpl();
        User freshUser = userService.getUserById(Integer.parseInt(user.getId()));
        
        if (freshUser != null && freshUser.getOrders() != null && !freshUser.getOrders().isEmpty()) {
            System.out.println("Found " + freshUser.getOrders().size() + " orders for user " + user.getName());
            
            for (Order order : freshUser.getOrders()) {
                ordersBox.getChildren().add(createOrderCard(order));
            }
        } else {
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
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 8, 0, 0, 2);" +
            "-fx-border-color:rgba(224, 224, 224, 0.33); -fx-border-radius: 10;"
        );

        // Header with order ID and status
        HBox header = new HBox(20);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label idLabel = new Label("Order #" + order.getOrderId());
        idLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        idLabel.setTextFill(Color.BLACK);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label statusLabel = new Label(order.getStatus().toString());
        statusLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        statusLabel.setStyle(getStatusStyle(order.getStatus()));
        statusLabel.setPadding(new Insets(5, 15, 5, 15));

        header.getChildren().addAll(idLabel, spacer, statusLabel);

        // Restaurant info
        Label restaurantLabel = new Label("Restaurant: " + 
            (order.getRestaurant() != null ? order.getRestaurant().getName() : "N/A"));
        restaurantLabel.setFont(Font.font("System", 14));
        restaurantLabel.setTextFill(Color.web("#2d3436"));

        // Order date
        Label dateLabel = new Label("Date: " + 
            (order.getOrderDate() != null ? 
                new SimpleDateFormat("MMM dd, yyyy HH:mm").format(order.getOrderDate()) : "N/A"));
        dateLabel.setFont(Font.font("System", 12));
        dateLabel.setTextFill(Color.web("#636e72"));

        // Items
        VBox itemsBox = new VBox(5);
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            Label itemsTitle = new Label("Items:");
            itemsTitle.setFont(Font.font("System", FontWeight.BOLD, 13));
            itemsTitle.setTextFill(Color.BLACK);
            itemsBox.getChildren().add(itemsTitle);

            for (CartItem item : order.getItems()) {
                Label itemLabel = new Label("  • " + item.getMenuItem().getName() + 
                    " x" + item.getQuantity() + " - $" + String.format("%.2f", item.getSubPrice()));
                itemLabel.setFont(Font.font("System", 12));
                itemLabel.setTextFill(Color.web("#2d3436"));
                itemsBox.getChildren().add(itemLabel);
            }
        }

        // Total
        Label totalLabel = new Label("Total: $" + String.format("%.2f", order.getTotalAmount()));
        totalLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        totalLabel.setTextFill(Color.web("#27ae60"));

        // Delivery address
        Label addressLabel = new Label("Delivery to: " + 
            (order.getDeliveryAddress() != null ? order.getDeliveryAddress().toString() : "N/A"));
        addressLabel.setFont(Font.font("System", 12));
        addressLabel.setTextFill(Color.web("#636e72"));
        addressLabel.setWrapText(true);

        card.getChildren().addAll(header, restaurantLabel, dateLabel, itemsBox, totalLabel, addressLabel);
        return card;
    }

    private static String getStatusStyle(javaproject1.DAL.Enums.OrderStatus status) {
        return switch (status) {
            case PENDING -> "-fx-background-color: #fff3cd; -fx-text-fill: #856404; -fx-background-radius: 15;";
            case CONFIRMED, PREPARING -> "-fx-background-color: #cfe2ff; -fx-text-fill: #084298; -fx-background-radius: 15;";
            case READY_FOR_DELIVERY, OUT_FOR_DELIVERY -> "-fx-background-color: #d1ecf1; -fx-text-fill: #0c5460; -fx-background-radius: 15;";
            case DELIVERED -> "-fx-background-color: #d1e7dd; -fx-text-fill: #0f5132; -fx-background-radius: 15;";
            case CANCELLED -> "-fx-background-color: #f8d7da; -fx-text-fill: #842029; -fx-background-radius: 15;";
            default -> "-fx-background-color: #e9ecef; -fx-text-fill: #495057; -fx-background-radius: 15;";
        };
    }
}
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
import java.util.List;

public class OrdersController {
    private static OrderServiceImpl orderService = new OrderServiceImpl();

    // Dark Theme Colors
    private static final String BACKGROUND_DARK = "#1f2937";
    private static final String PRIMARY_COLOR = "#059669";
    private static final String ACCENT_GOLD = "#fcd34d";
    private static final String CARD_BACKGROUND = "#374151";
    private static final String TEXT_COLOR_LIGHT = "#f9fafb";
    private static final String TEXT_COLOR_SECONDARY = "#d1d5db";
    private static final String ITEM_BACKGROUND = "#2b3543";

    public static void show(Stage stage, User user) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BACKGROUND_DARK + ";");

        HBox navbar = ClientMainController.createNavBar(stage, user);
        root.setTop(navbar);

        VBox contentBox = new VBox(30);
        contentBox.setPadding(new Insets(50));
        contentBox.setAlignment(Pos.TOP_CENTER);
        contentBox.setMaxWidth(1100);

        Label titleLabel = new Label("📦 My Orders");
        titleLabel.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 42));
        titleLabel.setTextFill(Color.web(ACCENT_GOLD));

        VBox ordersBox = new VBox(20);
        ordersBox.setAlignment(Pos.TOP_CENTER);
        
        // Get all orders and filter for current user
        List<Order> allOrders = orderService.getAllOrders();
        List<Order> userOrders = allOrders.stream()
            .filter(o -> o.getUser() != null && 
                        o.getUser().getId() != null && 
                        o.getUser().getId().equals(user.getId()))
            .toList();
        
        System.out.println("DEBUG OrdersController: Found " + userOrders.size() + " orders for user " + user.getName());
        
        if (!userOrders.isEmpty()) {
            for (Order order : userOrders) {
                System.out.println("DEBUG: Creating card for order #" + order.getOrderId());
                ordersBox.getChildren().add(createOrderCard(order));
            }
        } else {
            System.out.println("DEBUG: No orders found for user");
            VBox emptyBox = new VBox(20);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(60));
            emptyBox.setStyle(
                "-fx-background-color: " + CARD_BACKGROUND + "; " +
                "-fx-background-radius: 20; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 15, 0, 0, 5);"
            );
            
            Label emptyIcon = new Label("🛒");
            emptyIcon.setFont(Font.font("System", 60));
            
            Label emptyLabel = new Label("No orders yet!");
            emptyLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
            emptyLabel.setTextFill(Color.web(TEXT_COLOR_LIGHT));
            
            Label emptySubLabel = new Label("Start shopping to place your first order");
            emptySubLabel.setFont(Font.font("System", 18));
            emptySubLabel.setTextFill(Color.web(TEXT_COLOR_SECONDARY));
            
            emptyBox.getChildren().addAll(emptyIcon, emptyLabel, emptySubLabel);
            ordersBox.getChildren().add(emptyBox);
        }

        contentBox.getChildren().addAll(titleLabel, ordersBox);

        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        root.setCenter(scrollPane);
        Scene scene = new Scene(root, 1200, 800);
        stage.setScene(scene);
    }

    private static VBox createOrderCard(Order order) {
        VBox card = new VBox(20);
        card.setPadding(new Insets(30));
        card.setStyle(
            "-fx-background-color: " + CARD_BACKGROUND + "; " +
            "-fx-background-radius: 18; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 15, 0, 0, 5);"
        );
        card.setMinHeight(150);
        card.setPrefWidth(900);
        card.setMaxWidth(900);

        System.out.println("DEBUG: Creating card for order #" + order.getOrderId());
        System.out.println("  Status: " + order.getStatus());
        System.out.println("  Total: $" + order.getTotalAmount());
        System.out.println("  Restaurant: " + (order.getRestaurant() != null ? order.getRestaurant().getName() : "N/A"));

        // Header with order ID and status
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);

        Label idLabel = new Label("Order #" + (order.getOrderId() != null ? order.getOrderId() : "N/A"));
        idLabel.setStyle(
            "-fx-font-size: 22px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: " + TEXT_COLOR_LIGHT + ";"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label statusLabel = new Label(order.getStatus() != null ? order.getStatus().toString() : "UNKNOWN");
        statusLabel.setStyle(getStatusStyle(order.getStatus()));
        statusLabel.setPadding(new Insets(10, 25, 10, 25));

        header.getChildren().addAll(idLabel, spacer, statusLabel);

        // Restaurant info
        String restaurantName = "Unknown Restaurant";
        if (order.getRestaurant() != null && order.getRestaurant().getName() != null) {
            restaurantName = order.getRestaurant().getName();
            System.out.println("  Restaurant found: " + restaurantName);
        } else {
            System.out.println("  WARNING: Restaurant data is null for order #" + order.getOrderId());
        }
        
        Label restaurantLabel = new Label("🏪 " + restaurantName);
        restaurantLabel.setStyle(
            "-fx-font-size: 18px; " +
            "-fx-text-fill: " + PRIMARY_COLOR + "; " +
            "-fx-font-weight: bold;"
        );

        // Order date
        String dateStr = "N/A";
        if (order.getOrderDate() != null) {
            dateStr = new SimpleDateFormat("MMM dd, yyyy HH:mm").format(order.getOrderDate());
        }
        Label dateLabel = new Label("📅 " + dateStr);
        dateLabel.setStyle(
            "-fx-font-size: 15px; " +
            "-fx-text-fill: " + TEXT_COLOR_SECONDARY + ";"
        );

        // Items section
        VBox itemsBox = new VBox(12);
        itemsBox.setPadding(new Insets(15));
        itemsBox.setStyle(
            "-fx-background-color: " + ITEM_BACKGROUND + "; " +
            "-fx-background-radius: 12; " +
            "-fx-padding: 20;"
        );

        if (order.getItems() != null && !order.getItems().isEmpty()) {
            Label itemsTitle = new Label("📦 Items:");
            itemsTitle.setStyle(
                "-fx-font-size: 16px; " +
                "-fx-font-weight: bold; " +
                "-fx-text-fill: " + TEXT_COLOR_LIGHT + ";"
            );
            itemsBox.getChildren().add(itemsTitle);

            System.out.println("  Items count: " + order.getItems().size());
            for (CartItem item : order.getItems()) {
                String itemName = "Unknown Item";
                if (item.getMenuItem() != null && item.getMenuItem().getName() != null) {
                    itemName = item.getMenuItem().getName();
                }
                
                int qty = item.getQuantity();
                double price = item.getSubPrice();
                
                System.out.println("    - " + itemName + " x" + qty + " = $" + String.format("%.2f", price));
                
                HBox itemRow = new HBox(15);
                itemRow.setAlignment(Pos.CENTER_LEFT);
                
                Label itemLabel = new Label("• " + itemName);
                itemLabel.setStyle(
                    "-fx-font-size: 15px; " +
                    "-fx-text-fill: " + TEXT_COLOR_LIGHT + ";"
                );
                itemLabel.setPrefWidth(400);
                
                Label qtyLabel = new Label("x" + qty);
                qtyLabel.setStyle(
                    "-fx-font-size: 15px; " +
                    "-fx-text-fill: " + TEXT_COLOR_SECONDARY + ";"
                );
                qtyLabel.setPrefWidth(50);
                
                Region itemSpacer = new Region();
                HBox.setHgrow(itemSpacer, Priority.ALWAYS);
                
                Label priceLabel = new Label("$" + String.format("%.2f", price));
                priceLabel.setStyle(
                    "-fx-font-size: 15px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-text-fill: " + ACCENT_GOLD + ";"
                );
                
                itemRow.getChildren().addAll(itemLabel, qtyLabel, itemSpacer, priceLabel);
                itemsBox.getChildren().add(itemRow);
            }
        } else {
            System.out.println("  WARNING: No items found for order #" + order.getOrderId());
            Label noItems = new Label("No items information available");
            noItems.setStyle(
                "-fx-font-size: 15px; " +
                "-fx-text-fill: " + TEXT_COLOR_SECONDARY + "; " +
                "-fx-font-style: italic;"
            );
            itemsBox.getChildren().add(noItems);
        }

        // Footer with total and details
        HBox footer = new HBox(40);
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.setPadding(new Insets(10, 0, 0, 0));

        VBox leftFooter = new VBox(5);
        Label totalLabel = new Label("Total: $" + String.format("%.2f", order.getTotalAmount()));
        totalLabel.setStyle(
            "-fx-font-size: 26px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: " + PRIMARY_COLOR + ";"
        );
        leftFooter.getChildren().add(totalLabel);

        VBox rightFooter = new VBox(10);
        
        // Delivery Address
        String addressStr = "Not specified";
        if (order.getDeliveryAddress() != null) {
            Address addr = order.getDeliveryAddress();
            addressStr = String.format("%s, %s, Building %d", 
                addr.getStreet() != null ? addr.getStreet() : "Unknown Street",
                addr.getCity() != null ? addr.getCity() : "Unknown City",
                addr.getBuildingNumber());
            System.out.println("  Address found: " + addressStr);
        } else {
            System.out.println("  WARNING: No delivery address for order #" + order.getOrderId());
        }
        
        Label addressLabel = new Label("📍 " + addressStr);
        addressLabel.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-text-fill: " + TEXT_COLOR_SECONDARY + ";"
        );
        addressLabel.setWrapText(true);
        addressLabel.setMaxWidth(500);
        rightFooter.getChildren().add(addressLabel);
        
        // Delivery Person Info
        String deliveryPersonInfo = "Not assigned yet";
        if (order.getDelivery() != null && order.getDelivery().getDeliveryPerson() != null) {
            Employee deliveryPerson = order.getDelivery().getDeliveryPerson();
            String name = deliveryPerson.getName() != null ? deliveryPerson.getName() : "Unknown";
            String phone = deliveryPerson.getPhoneNumber() != null ? deliveryPerson.getPhoneNumber() : "N/A";
            deliveryPersonInfo = String.format("%s - %s", name, phone);
            System.out.println("  Delivery person found: " + deliveryPersonInfo);
        } else {
            System.out.println("  WARNING: No delivery person assigned for order #" + order.getOrderId());
        }
        
        Label deliveryPersonLabel = new Label("🚴 " + deliveryPersonInfo);
        deliveryPersonLabel.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-text-fill: " + TEXT_COLOR_SECONDARY + ";"
        );
        deliveryPersonLabel.setWrapText(true);
        deliveryPersonLabel.setMaxWidth(500);
        rightFooter.getChildren().add(deliveryPersonLabel);

        footer.getChildren().addAll(leftFooter, rightFooter);

        // Add all sections to card
        card.getChildren().addAll(header, restaurantLabel, dateLabel, itemsBox, footer);
        
        System.out.println("DEBUG: Card created successfully for order #" + order.getOrderId());
        return card;
    }

    private static String getStatusStyle(javaproject1.DAL.Enums.OrderStatus status) {
        if (status == null) {
            return "-fx-background-color: #4b5563; -fx-text-fill: " + TEXT_COLOR_LIGHT + "; -fx-background-radius: 18; -fx-font-size: 15px; -fx-font-weight: bold;";
        }
        
        return switch (status) {
            case PENDING -> "-fx-background-color: #fbbf24; -fx-text-fill: #78350f; -fx-background-radius: 18; -fx-font-size: 15px; -fx-font-weight: bold;";
            case CONFIRMED, PREPARING -> "-fx-background-color: #60a5fa; -fx-text-fill: #1e3a8a; -fx-background-radius: 18; -fx-font-size: 15px; -fx-font-weight: bold;";
            case READY_FOR_DELIVERY, OUT_FOR_DELIVERY -> "-fx-background-color: #a78bfa; -fx-text-fill: #4c1d95; -fx-background-radius: 18; -fx-font-size: 15px; -fx-font-weight: bold;";
            case DELIVERED -> "-fx-background-color: #10b981; -fx-text-fill: #064e3b; -fx-background-radius: 18; -fx-font-size: 15px; -fx-font-weight: bold;";
            case CANCELLED -> "-fx-background-color: #ef4444; -fx-text-fill: #7f1d1d; -fx-background-radius: 18; -fx-font-size: 15px; -fx-font-weight: bold;";
            default -> "-fx-background-color: #4b5563; -fx-text-fill: " + TEXT_COLOR_LIGHT + "; -fx-background-radius: 18; -fx-font-size: 15px; -fx-font-weight: bold;";
        };
    }
}
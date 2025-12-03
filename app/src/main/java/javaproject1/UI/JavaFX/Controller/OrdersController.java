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
import javafx.scene.control.Alert;
import javafx.scene.layout.HBox;
import javaproject1.BLL.Service.implementation.*;
import javaproject1.DAL.Entity.*;
import javaproject1.DAL.Enums.OrderStatus;

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
        
        if (user.getOrders() != null && !user.getOrders().isEmpty()) {
            for (Order order : user.getOrders()) {
                ordersBox.getChildren().add(createOrderCard(order));
            }
        } else {
            Label emptyLabel = new Label("No orders yet");
            emptyLabel.setFont(Font.font("System", 18));
            emptyLabel.setTextFill(Color.web("#636e72"));
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
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 10; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 8, 0, 0, 2);"
        );

        Label idLabel = new Label("Order #" + order.getOrderId());
        idLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        idLabel.setTextFill(Color.web("#1a1a1a"));

        Label statusLabel = new Label("Status: " + order.getStatus());
        statusLabel.setFont(Font.font("System", 14));
        statusLabel.setTextFill(Color.web("#4a5568"));

        Label totalLabel = new Label("Total: $" + String.format("%.2f", order.getTotalAmount()));
        totalLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        totalLabel.setTextFill(Color.web("#1a1a1a"));

        card.getChildren().addAll(idLabel, statusLabel, totalLabel);
        return card;
    }
}

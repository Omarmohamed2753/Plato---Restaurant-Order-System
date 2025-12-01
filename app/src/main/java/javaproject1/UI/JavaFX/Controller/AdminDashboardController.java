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

public class AdminDashboardController {

    private static AdminServiceImpl adminService = new AdminServiceImpl();
    private static OrderServiceImpl orderService = new OrderServiceImpl();
    private static UserServiceImpl userService = new UserServiceImpl();
    private static EmployeeServiceImpl employeeService = new EmployeeServiceImpl();

    public static void show(Stage stage, Admin admin) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f7fa;");

        // Admin Navigation Bar
        HBox navbar = createAdminNavBar(stage, admin);
        root.setTop(navbar);

        // Sidebar
        VBox sidebar = createSidebar(stage, admin);
        root.setLeft(sidebar);

        // Main Dashboard Content
        VBox contentBox = new VBox(25);
        contentBox.setPadding(new Insets(30));

        Label titleLabel = new Label("Admin Dashboard");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 32));
        titleLabel.setTextFill(Color.web("#2d3436"));

        Label restaurantLabel = new Label("Restaurant: " + 
            (admin.getRestaurant() != null ? admin.getRestaurant().getName() : "N/A"));
        restaurantLabel.setFont(Font.font("System", FontWeight.NORMAL, 18));
        restaurantLabel.setTextFill(Color.web("#636e72"));

        // Statistics Cards
        HBox statsBox = createStatsCards(admin);

        // Recent Orders
        VBox ordersSection = createOrdersSection(admin);

        contentBox.getChildren().addAll(titleLabel, restaurantLabel, statsBox, ordersSection);

        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        root.setCenter(scrollPane);

        Scene scene = new Scene(root, 1200, 750);
        stage.setScene(scene);
    }

    private static HBox createAdminNavBar(Stage stage, Admin admin) {
        HBox navbar = new HBox(20);
        navbar.setPadding(new Insets(15, 30, 15, 30));
        navbar.setAlignment(Pos.CENTER_LEFT);
        navbar.setStyle("-fx-background-color: #e74c3c; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);");

        Label brandLabel = new Label("🔐 Admin Portal");
        brandLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        brandLabel.setTextFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label adminNameLabel = new Label("Admin: " + admin.getName());
        adminNameLabel.setFont(Font.font("System", 14));
        adminNameLabel.setTextFill(Color.WHITE);

        Button logoutButton = new Button("Logout");
        logoutButton.setStyle(
            "-fx-background-color: rgba(255,255,255,0.2); " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 13px; " +
            "-fx-padding: 8 15 8 15; " +
            "-fx-background-radius: 15; " +
            "-fx-cursor: hand;"
        );
        logoutButton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Logout from admin panel?");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    WelcomeController.show(stage);
                }
            });
        });

        navbar.getChildren().addAll(brandLabel, spacer, adminNameLabel, logoutButton);
        return navbar;
    }

    private static VBox createSidebar(Stage stage, Admin admin) {
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(220);
        sidebar.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 2, 0);");

        Label menuLabel = new Label("MENU");
        menuLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        menuLabel.setTextFill(Color.web("#95a5a6"));

        Button dashboardBtn = createSidebarButton("📊 Dashboard", true);
        dashboardBtn.setOnAction(e -> show(stage, admin));

        Button ordersBtn = createSidebarButton("📦 Orders", false);
        ordersBtn.setOnAction(e -> AdminOrdersController.show(stage, admin));

        Button menuBtn = createSidebarButton("🍽️ Menu", false);
        menuBtn.setOnAction(e -> AdminMenuController.show(stage, admin));

        Button employeesBtn = createSidebarButton("👥 Employees", false);
        employeesBtn.setOnAction(e -> AdminEmployeesController.show(stage, admin));

        Button usersBtn = createSidebarButton("👤 Users", false);
        usersBtn.setOnAction(e -> AdminUsersController.show(stage, admin));

        Button reviewsBtn = createSidebarButton("⭐ Reviews", false);
        reviewsBtn.setOnAction(e -> AdminReviewsController.show(stage, admin));

        sidebar.getChildren().addAll(
            menuLabel,
            dashboardBtn,
            ordersBtn,
            menuBtn,
            employeesBtn,
            usersBtn,
            reviewsBtn
        );

        return sidebar;
    }

    private static Button createSidebarButton(String text, boolean active) {
        Button button = new Button(text);
        button.setPrefWidth(180);
        button.setAlignment(Pos.CENTER_LEFT);
        
        if (active) {
            button.setStyle(
                "-fx-background-color: #667eea; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-padding: 12 15 12 15; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand;"
            );
        } else {
            button.setStyle(
                "-fx-background-color: transparent; " +
                "-fx-text-fill: #2d3436; " +
                "-fx-font-size: 14px; " +
                "-fx-padding: 12 15 12 15; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand;"
            );
            button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: #f5f7fa; " +
                "-fx-text-fill: #667eea; " +
                "-fx-font-size: 14px; " +
                "-fx-padding: 12 15 12 15; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand;"
            ));
            button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: transparent; " +
                "-fx-text-fill: #2d3436; " +
                "-fx-font-size: 14px; " +
                "-fx-padding: 12 15 12 15; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand;"
            ));
        }
        
        return button;
    }

    private static HBox createStatsCards(Admin admin) {
        HBox statsBox = new HBox(20);
        statsBox.setAlignment(Pos.CENTER);

        // Total Orders Card
        List<Order> allOrders = orderService.getAllOrders();
        int totalOrders = (int) allOrders.stream()
            .filter(o -> admin.getRestaurant() != null && 
                        o.getRestaurant() != null &&
                        o.getRestaurant().getRestaurantId().equals(admin.getRestaurant().getRestaurantId()))
            .count();

        VBox ordersCard = createStatCard("📦", "Total Orders", String.valueOf(totalOrders), "#3498db");

        // Total Users Card
        int totalUsers = userService.getAllUsers().size();
        VBox usersCard = createStatCard("👥", "Total Users", String.valueOf(totalUsers), "#9b59b6");

        // Total Employees Card
        int totalEmployees = (int) employeeService.getAllEmployees().stream()
            .filter(e -> admin.getRestaurant() != null &&
                        e.getRestaurant() != null &&
                        e.getRestaurant().getRestaurantId().equals(admin.getRestaurant().getRestaurantId()))
            .count();
        VBox employeesCard = createStatCard("👔", "Employees", String.valueOf(totalEmployees), "#e67e22");

        // Pending Orders Card
        int pendingOrders = (int) allOrders.stream()
            .filter(o -> o.getStatus() == OrderStatus.PENDING || o.getStatus() == OrderStatus.CONFIRMED)
            .count();
        VBox pendingCard = createStatCard("⏳", "Pending", String.valueOf(pendingOrders), "#e74c3c");

        statsBox.getChildren().addAll(ordersCard, usersCard, employeesCard, pendingCard);
        return statsBox;
    }

    private static VBox createStatCard(String icon, String title, String value, String color) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(25));
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(200);
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 3);"
        );

        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("System", 36));

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", 14));
        titleLabel.setTextFill(Color.web("#7f8c8d"));

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("System", FontWeight.BOLD, 28));
        valueLabel.setTextFill(Color.web(color));

        card.getChildren().addAll(iconLabel, titleLabel, valueLabel);
        return card;
    }

    private static VBox createOrdersSection(Admin admin) {
        VBox section = new VBox(15);
        section.setPadding(new Insets(20));
        section.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 3);"
        );

        Label titleLabel = new Label("Recent Orders");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 20));

        TableView<Order> table = new TableView<>();
        table.setPrefHeight(300);

        TableColumn<Order, String> idCol = new TableColumn<>("Order ID");
        idCol.setPrefWidth(100);
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getOrderId()));

        TableColumn<Order, String> userCol = new TableColumn<>("User");
        userCol.setPrefWidth(150);
        userCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            data.getValue().getUser() != null ? data.getValue().getUser().getName() : "N/A"
        ));

        TableColumn<Order, String> totalCol = new TableColumn<>("Total");
        totalCol.setPrefWidth(100);
        totalCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            "$" + String.format("%.2f", data.getValue().getTotalAmount())
        ));

        TableColumn<Order, String> statusCol = new TableColumn<>("Status");
        statusCol.setPrefWidth(150);
        statusCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            data.getValue().getStatus().toString()
        ));

        table.getColumns().addAll(idCol, userCol, totalCol, statusCol);

        // Load recent orders
        List<Order> recentOrders = orderService.getAllOrders().stream()
            .filter(o -> admin.getRestaurant() != null && 
                        o.getRestaurant() != null &&
                        o.getRestaurant().getRestaurantId().equals(admin.getRestaurant().getRestaurantId()))
            .limit(10)
            .toList();

        table.getItems().addAll(recentOrders);

        section.getChildren().addAll(titleLabel, table);
        return section;
    }
}